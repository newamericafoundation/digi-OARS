import React, { useState, useContext } from "react";
import {
  CCard,
  CCardHeader,
  CBadge,
  CButton,
  CCardBody,
  CDataTable,
  CCollapse,
  CCol,
  CRow,
  CProgress,
  CCallout,
  CSpinner,
  CTooltip,
} from "@coreui/react";
import moment from "moment-timezone";
import axios from "axios";
import * as Constants from "../../../constants";
import { APIContext } from "../../../providers/APIProvider";
import EllipsesText from "react-ellipsis-text";
import { toCountryByIsoFromX500 ,toCurrency } from "../../../utilities"

export const FundsTable = ({ funds, isReceiver, refreshTableCallback }) => {
  const [api] = useContext(APIContext);
  const [details, setDetails] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

  const toggleDetails = (index) => {
    const position = details.indexOf(index);
    let newDetails = details.slice();
    if (position !== -1) {
      newDetails.splice(position, 1);
    } else {
      newDetails = [...details, index];
    }
    setDetails(newDetails);
  };

  const fields = [
    { key: "receivingParty", label: "Receiving Country" },
    { key: "amount" },
    { key: "balance" },
    { key: "maxWithdrawalAmount" },
    { key: "createdDateTime", label: "Created Date" },
    { key: "status", _style: { width: "20%" } },
    {
      key: "show_details",
      label: "",
      _style: { width: "1%" },
      sorter: false,
      filter: false,
    },
  ];

  const getStatusBadge = (status) => {
    switch (status) {
      case "PAID":
        return "light";
      case "RECEIVED":
        return "success";
      case "ISSUED":
        return "warning";
      default:
        return "primary";
    }
  };

  const getReceivedButton = (status, id, index) => {
    if (isReceiver) {
      if (status === "ISSUED") {
        return (
          <CButton
            className={"float-right mb-0"}
            color="success"
            variant="outline"
            shape="square"
            size="sm"
            onClick={() => onHandleReceiveClick(id, index)}
          >
            {isLoading ? (
              <CSpinner
                className="spinner-border spinner-border-sm mr-1"
                role="status"
                aria-hidden="true"
              />
            ) : null}
            Receive Funds
          </CButton>
        );
      }
    }
  };

  const onHandleReceiveClick = (fundId, index) => {
    setIsLoading(true);
    const url =
      "http://" + window._env_.API_CLIENT_URL + ":" + api.port + "/api/fund";

    axios
      .put(url, null, { params: { fundId } })
      .then((response) => {
        setIsLoading(false);
        refreshTableCallback();
        toggleDetails(index);
      })
      .catch((err) => console.log(err));
  };

  return (
    <CDataTable
      items={funds}
      fields={fields}
      columnFilter
      tableFilter
      itemsPerPageSelect
      itemsPerPage={5}
      clickableRows
      hover
      sorter
      pagination
      scopedSlots={{
        originParty: (item) => (
          <td>{toCountryByIsoFromX500(item.originParty)}</td>
        ),
        receivingParty: (item) => (
          <td>{toCountryByIsoFromX500(item.receivingParty)}</td>
        ),
        amount: (item) => <td>{toCurrency(item.amount, item.currency)}</td>,
        balance: (item) => <td>{toCurrency(item.balance, item.currency)}</td>,
        maxWithdrawalAmount: (item) => (
          <td>{toCurrency(item.maxWithdrawalAmount, item.currency)}</td>
        ),
        createdDateTime: (item) => (
          <td>
            {moment
              .tz(item.createdDateTime, "UTC")
              .format(Constants.DATE_FORMAT)}
          </td>
        ),
        updatedDateTime: (item) => (
          <td>
            {moment
              .tz(item.updateDateTime, "UTC")
              .format(Constants.DATE_FORMAT)}
          </td>
        ),
        status: (item) => (
          <td>
            <CBadge color={getStatusBadge(item.status)}>{item.status}</CBadge>
          </td>
        ),
        show_details: (item, index) => {
          return (
            <td>
              <CButton
                color="primary"
                variant="outline"
                shape="square"
                size="sm"
                onClick={() => {
                  toggleDetails(index);
                }}
              >
                {details.includes(index) ? "Hide" : "Show"}
              </CButton>
            </td>
          );
        },
        details: (item, index) => {
          return (
            <CCollapse show={details.includes(index)}>
              <CCard className="m-3">
                <CCardHeader>
                  Fund Details
                  {getReceivedButton(item.status, item.linearId, index)}
                </CCardHeader>
                <CCardBody>
                  {item.status === Constants.FUND_RECEIVED ? (
                    <CRow className="mb-3">
                      <CCol>
                        <p className="text-muted">Total Assets Repatriated:</p>
                        <CProgress
                          value={(item.balance / item.amount) * 100}
                          showPercentage
                          striped
                          color="success"
                          precision={2}
                        />
                      </CCol>
                    </CRow>
                  ) : null}
                  <CRow>
                    <CCol xl="4" sm="3">
                      <CTooltip content={item.linearId} placement="right-end">
                        <CCallout color="info" className={"bg-light"}>
                          <p className="text-muted mb-0">State ID</p>
                          <strong className="p">{item.linearId}</strong>
                        </CCallout>
                      </CTooltip>
                      <CCallout color="info" className={"bg-light"}>
                        <p className="text-muted mb-0">Origin Country</p>
                        <strong className="p">{toCountryByIsoFromX500(item.originParty)}</strong>
                      </CCallout>
                      <CCallout color="info" className={"bg-light"}>
                        <p className="text-muted mb-0">Receiving Country</p>
                        <strong className="p">{toCountryByIsoFromX500(item.receivingParty)}</strong>
                      </CCallout>
                    </CCol>
                    <CCol xl="4" sm="3">
                      <CCallout color="info" className={"bg-light"}>
                        <p className="text-muted mb-0">Amount</p>
                        <strong className="p">
                          {toCurrency(item.amount, item.currency)}
                        </strong>
                      </CCallout>
                      <CCallout color="info" className={"bg-light"}>
                        <p className="text-muted mb-0">Balance</p>
                        <strong className="p">
                          {toCurrency(item.balance, item.currency)}
                        </strong>
                      </CCallout>
                      <CCallout color="info" className={"bg-light"}>
                        <p className="text-muted mb-0">Max Withdrawal Amount</p>
                        <strong className="p">
                          {toCurrency(item.maxWithdrawalAmount, item.currency)}
                        </strong>
                      </CCallout>
                    </CCol>
                    <CCol xl="4" sm="3">
                      <CCallout
                        color={item.status === "ISSUED" ? "warning" : "success"}
                        className={"bg-light"}
                      >
                        <p className="text-muted mb-0">Status</p>
                        <strong className="p">{item.status}</strong>
                      </CCallout>
                      <CTooltip content={item.txId} placement="right-end">
                        <CCallout color="info" className={"bg-light"}>
                          <p className="text-muted mb-0">Transaction ID</p>
                          <strong className="p">
                            <EllipsesText text={item.txId} length={40} />
                          </strong>
                        </CCallout>
                      </CTooltip>
                      <CCallout color="info" className={"bg-light"}>
                        <p className="text-muted mb-0">Created Date/Time</p>
                        <strong className="p">
                          {moment
                            .tz(item.createdDateTime, "UTC")
                            .format(Constants.DATETIME_FORMAT)}
                        </strong>
                      </CCallout>
                      {item.createdDateTime !== item.updatedDateTime ?? (
                        <CCallout color="info" className={"bg-light"}>
                          <p className="text-muted mb-0">Updated Date/Time</p>
                          <strong className="p">
                            {moment
                              .tz(item.updateDateTime, "UTC")
                              .format(Constants.DATETIME_FORMAT)}
                          </strong>
                        </CCallout>
                      )}
                    </CCol>
                  </CRow>
                </CCardBody>
              </CCard>
            </CCollapse>
          );
        },
      }}
    />
  );
};

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
  CModal,
  CModalHeader,
  CModalTitle,
  CModalBody,
  CModalFooter,
} from "@coreui/react";
import moment from "moment-timezone";
import axios from "axios";
import * as Constants from "../../../constants";
import { APIContext } from "../../../providers/APIProvider";
import { toCountryByIsoFromX500, toCurrency } from "../../../utilities";
import getRequestsByFundId from "../../../data/GetRequestsByFundId";
import { RequestsSnapshotTable } from "../withdrawals/RequestsSnapshotTable";
import cogoToast from "cogo-toast";
import { useAuth } from "auth-hook";

export const FundsTable = ({ funds, isReceiver, refreshTableCallback }) => {
  const auth = useAuth();
  const [api] = useContext(APIContext);
  const [details, setDetails] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [show, setShow] = useState(false);
  const [showRequests, setShowRequests] = useState(false);
  const [currentItem, setCurrentItem] = useState({});
  const [currentAction, setCurrentAction] = useState("");
  const [requestsFromFundId, setRequestsFromFundId] = useState([]);

  const handleShow = (item) => {
    setCurrentItem(item);
    setShow(true);
  };

  const handleClose = () => {
    setShow(false);
  };

  const handleRequestsShow = (fundId) => {
    const data = getRequestsByFundId(api.port, fundId);
    data.then((response) => {
      setRequestsFromFundId(response);
    });
    setShowRequests(true);
  };

  const handleRequestsClose = () => {
    setShowRequests(false);
  };

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
    { key: "originParty", label: "Origin Country" },
    { key: "receivingParty", label: "Receiving Country" },
    { key: "amount" },
    { key: "balance" },
    { key: "maxWithdrawalAmount" },
    { key: "createdDateTime", label: "Created Date" },
    { key: "status", _style: { width: "20%" } },
    { key: "actions", _style: { width: "20%" }, sorter: false, filter: false },
    {
      key: "show_details",
      label: "Details",
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

  const getActionButton = (item, index) => {
    if (item.status === "ISSUED" && isReceiver) {
      return (
        <CButton
          className={"float-left mb-0"}
          color="success"
          variant="outline"
          shape="square"
          size="sm"
          onClick={() => {
            setCurrentAction("receive");
            handleShow(item);
          }}
        >
          Receive
        </CButton>
      );
    }
  };

  const getRequestsButton = (item) => {
    if (item.status !== "ISSUED") {
      return (
        <CButton
          className={"float-left mb-0"}
          color="dark"
          variant="outline"
          shape="square"
          size="sm"
          onClick={() => handleRequestsShow(item.linearId)}
        >
          View Requests
        </CButton>
      );
    }
  };

  const responseMessage = (message) => {
    return (
      <div>
        <strong>Fund ID:</strong> {message.data.entity.data.linearId.id}
        <br />
        <strong>Status:</strong>{" "}
        <CBadge color="success">{message.data.entity.data.status}</CBadge>
        <br />
        <strong>Amount:</strong>{" "}
        {toCurrency(message.data.entity.data.amount, "USD")}
      </div>
    );
  };

  const onHandleConfirmationClick = (fundId, receivedByUsername) => {
    setIsLoading(true);
    const url =
      "http://" + window._env_.API_CLIENT_URL + ":" + api.port + "/api/fund";

    axios
      .put(url, null, { params: { fundId, receivedByUsername } })
      .then((response) => {
        setIsLoading(false);
        refreshTableCallback();
        handleClose();
        const { hide } = cogoToast.success(responseMessage(response), {
          heading: "Repatriated Funds Received",
          position: "top-right",
          hideAfter: 8,
          onClick: () => {
            hide();
          },
        });
      })
      .catch((err) => {
        const { hide } = cogoToast.error(err.message, {
          heading: "Error Receiving Funds",
          position: "top-right",
          hideAfter: 8,
          onClick: () => {
            hide();
          },
        });
      });
  };

  return (
    <>
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
          actions: (item, index) => {
            return (
              <td>
                {getActionButton(item, index)}
                {getRequestsButton(item)}
              </td>
            );
          },
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
                  <CCardHeader>Fund Details</CCardHeader>
                  <CCardBody>
                    {item.status === Constants.FUND_RECEIVED ? (
                      <CRow className="mb-3">
                        <CCol>
                          <p className="text-muted">
                            Total Assets Repatriated:
                          </p>
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
                          <strong className="p">
                            {toCountryByIsoFromX500(item.originParty)}
                          </strong>
                        </CCallout>
                        <CCallout color="info" className={"bg-light"}>
                          <p className="text-muted mb-0">Receiving Country</p>
                          <strong className="p">
                            {toCountryByIsoFromX500(item.receivingParty)}
                          </strong>
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
                          <p className="text-muted mb-0">
                            Max Withdrawal Amount
                          </p>
                          <strong className="p">
                            {toCurrency(
                              item.maxWithdrawalAmount,
                              item.currency
                            )}
                          </strong>
                        </CCallout>
                      </CCol>
                      <CCol xl="4" sm="3">
                        <CCallout
                          color={
                            item.status === "ISSUED" ? "warning" : "success"
                          }
                          className={"bg-light"}
                        >
                          <p className="text-muted mb-0">Status</p>
                          <strong className="p">{item.status}</strong>
                        </CCallout>
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
      <CModal
        color="success"
        show={show}
        onClose={handleClose}
        closeOnBackdrop={false}
      >
        <CModalHeader closeButton>
          <CModalTitle>
            {currentAction.charAt(0).toUpperCase() + currentAction.slice(1)}{" "}
            Confirmation
          </CModalTitle>
        </CModalHeader>
        <CModalBody>
          <CCallout color="success" className={"bg-light"}>
            <p className="text-muted mb-0">Request State ID</p>
            <strong className="p">{currentItem.linearId}</strong>
          </CCallout>
          <CCallout color="success" className={"bg-light"}>
            <p className="text-muted mb-0">Origin Country</p>
            <strong className="p">
              {currentItem.originParty
                ? toCountryByIsoFromX500(currentItem.originParty)
                : null}
            </strong>
          </CCallout>
          <CCallout color="success" className={"bg-light"}>
            <p className="text-muted mb-0">Amount</p>
            <strong className="p">
              {toCurrency(currentItem.amount, "USD")}
            </strong>
          </CCallout>
        </CModalBody>
        <CModalFooter>
          <CButton
            color="success"
            onClick={() => onHandleConfirmationClick(currentItem.linearId, auth.user.fullName)}
          >
            {isLoading ? (
              <CSpinner
                className="spinner-border spinner-border-sm mr-1"
                role="status"
                aria-hidden="true"
              />
            ) : null}
            {currentAction.charAt(0).toUpperCase() + currentAction.slice(1)}{" "}
            Funds
          </CButton>
          <CButton color="secondary" onClick={handleClose}>
            Cancel
          </CButton>
        </CModalFooter>
      </CModal>
      <CModal show={showRequests} size="xl" closeOnBackdrop={false}>
        <CModalHeader>
          <CModalTitle>Withdrawal Requests</CModalTitle>
        </CModalHeader>
        <CModalBody>
          <RequestsSnapshotTable requests={requestsFromFundId} />
        </CModalBody>
        <CModalFooter>
          <CButton color="secondary" onClick={handleRequestsClose}>
            Close
          </CButton>
        </CModalFooter>
      </CModal>
    </>
  );
};

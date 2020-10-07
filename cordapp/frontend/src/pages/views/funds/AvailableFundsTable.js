import React, { useState, useContext } from "react";
import * as Constants from "../../../constants";
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
  CModal,
  CModalHeader,
  CModalTitle,
  CModalFooter,
  CModalBody,
} from "@coreui/react";
import Moment from "moment";
import { RequestForm } from "../withdrawals/RequestForm";
import { toCountryByIsoFromX500, toCurrency } from "../../../utilities";
import getRequestsByFundId from "../../../data/GetRequestsByFundId";
import { RequestsSnapshotTable } from "../withdrawals/RequestsSnapshotTable";
import { APIContext } from "../../../providers/APIProvider";
import cogoToast from "cogo-toast";

export const AvailableFundsTable = ({
  funds,
  refreshFundsTableCallback,
  refreshRequestsTableCallback,
  isRequestor,
}) => {
  const [api] = useContext(APIContext);
  const [details, setDetails] = useState([]);
  const [show, setShow] = useState(false);
  const [request, setRequest] = useState({});
  const [showRequests, setShowRequests] = useState(false);
  const [requestsFromFundId, setRequestsFromFundId] = useState([]);

  const handleShow = (item, index) => {
    setRequest(item);
    setShow(true);
  };
  const handleClose = () => setShow(false);

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

  const responseMessage = (message) => {
    return (
      <div>
        <strong>Request ID:</strong> {message.entity.data.linearId.id}
        <br />
        <strong>Status:</strong>{" "}
        <CBadge color="warning">{message.entity.data.status}</CBadge>
        <br />
        <strong>Amount:</strong>{" "}
        {toCurrency(message.entity.data.amount, "USD")}
      </div>
    );
  };

  const onFormSubmit = (response) => {
    handleClose();
    if (response.status === 200) {
      const { hide } = cogoToast.success(responseMessage(response), {
        heading: "Withdrawal Request Created",
        position: "top-right",
        hideAfter: 8,
        onClick: () => {
          hide();
        },
      });
    } else {
      const { hide } = cogoToast.error(response.entity.message, {
        heading: "Error Receiving Funds",
        position: "top-right",
        hideAfter: 8,
        onClick: () => {
          hide();
        },
      });
    }
    refreshFundsTableCallback();
    refreshRequestsTableCallback();
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
    { key: "balance", label: "Balance Available" },
    { key: "updatedDateTime", label: "Updated Date" },
    { key: "maxWithdrawalAmount" },
    { key: "status", _style: { width: "10%" }, filter: false },
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
      case "RECEIVED":
        return "success";
      case "ISSUED":
        return "warning";
      default:
        return "primary";
    }
  };

  const getRequestButton = (item, index) => {
    if (isRequestor) {
      return (
        <CButton
          className={"float-left mb-0 mr-2"}
          color="success"
          variant="outline"
          shape="square"
          size="sm"
          onClick={() => handleShow(item, index)}
        >
          Request Money
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

  return (
    <>
      <CDataTable
        items={funds.data.filter(
          (fund) => fund.status === Constants.FUND_RECEIVED && fund.balance > 0
        )}
        fields={fields}
        columnFilter
        tableFilter
        itemsPerPageSelect
        itemsPerPage={5}
        hover
        sorter
        pagination
        scopedSlots={{
          originParty: (item) => (
            <td>{toCountryByIsoFromX500(item.originParty)}</td>
          ),
          amount: (item) => <td>{toCurrency(item.amount, item.currency)}</td>,
          balance: (item) => <td>{toCurrency(item.balance, item.currency)}</td>,
          maxWithdrawalAmount: (item) => (
            <td>{toCurrency(item.maxWithdrawalAmount, item.currency)}</td>
          ),
          createdDateTime: (item) => (
            <td>{Moment(item.createdDateTime).format("DD/MMM/yyyy")}</td>
          ),
          updatedDateTime: (item) => (
            <td>{Moment(item.updatedDateTime).format("DD/MMM/yyyy")}</td>
          ),
          status: (item) => (
            <td>
              <CBadge color={getStatusBadge(item.status)}>{item.status}</CBadge>
            </td>
          ),
          actions: (item, index) => {
            return (
              <td>
                {getRequestButton(item, index)}
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
                        <CCallout color="info" className={"bg-light"}>
                          <p className="text-muted mb-0">State ID</p>
                          <strong className="p">{item.linearId}</strong>
                        </CCallout>
                        <CCallout color="info" className={"bg-light"}>
                          <p className="text-muted mb-0">Created Date/Time</p>
                          <strong className="p">
                            {Moment(item.createdDateTime).format(
                              "DD/MMM/YYYY HH:mm:ss"
                            )}
                          </strong>
                        </CCallout>
                        <CCallout color="info" className={"bg-light"}>
                          <p className="text-muted mb-0">Updated Date/Time</p>
                          <strong className="p">
                            {Moment(item.updatedDateTime).format(
                              "DD/MMM/YYYY HH:mm:ss"
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
                      </CCol>
                    </CRow>
                  </CCardBody>
                </CCard>
              </CCollapse>
            );
          },
        }}
      />
      <RequestForm
        show={show}
        onSubmit={onFormSubmit}
        request={request}
        handleClose={handleClose}
      />
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

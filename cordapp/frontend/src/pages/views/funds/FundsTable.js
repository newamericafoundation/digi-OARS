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
  CAlert
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
import { FundHistory } from "./FundHistory";

export const FundsTable = ({ funds, isReceiver, refreshTableCallback }) => {
  const auth = useAuth();
  const [api] = useContext(APIContext);
  const [details, setDetails] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [show, setShow] = useState(false);
  const [showHistory, setShowHistory] = useState(false);
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

  const handleShowHistory = (item) => {
    setCurrentItem(item);
    setShowHistory(true);
  };

  const handleCloseHistory = () => {
    setShowHistory(false);
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
    { key: "accountId", label: "Account ID" },
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
          View Assigned Requests
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
        <br />
        <strong>Account ID:</strong> {message.data.entity.data.accountId}
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
          accountId: (item) => (<td>{item.accountId}</td>),
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
                  <CCardHeader>
                    Return Details
                    <CButton
                      className="float-right mt-1 mb-1"
                      color="secondary"
                      onClick={() => handleShowHistory(item)}
                    >
                      Show Return History
                    </CButton>
                  </CCardHeader>
                  <CCardBody>
                    {item.status === Constants.FUND_RECEIVED ? (
                      <CRow className="mb-3">
                        <CCol>
                          <p className="text-muted">Total Available Balance:</p>
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
                            <p className="text-muted mb-0">Ledger ID</p>
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
                        <CTooltip style="background-color: #f00" content="Receiving country bank account number" placement="right-end">
                        <CCallout color="info" className={"bg-light"}>
                          <p className="text-muted mb-0">Account ID</p>
                          <strong className="p">
                            { item.accountId }
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
                      </CCol>
                      <CCol xl="4" sm="3">
                        <CCallout color="info" className={"bg-light"}>
                          <p className="text-muted mb-0">Updated Date/Time</p>
                          <strong className="p">
                            {moment
                              .tz(item.updatedDateTime, "UTC")
                              .format(Constants.DATETIME_FORMAT)}
                          </strong>
                        </CCallout>
                        <CCallout
                          color={
                            item.status === "ISSUED" ? "warning" : "success"
                          }
                          className={"bg-light"}
                        >
                          <p className="text-muted mb-0">Status</p>
                          <strong className="p">
                            {item.status}
                            {item.status === "RECEIVED"
                              ? " by " + item.receivedByUsername
                              : null}
                          </strong>
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
            <p className="text-muted mb-0">Request Ledger ID</p>
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
          <CCallout color="success" className={"bg-light"}>
              <p className="text-muted mb-0">Account ID</p>
            <strong className="p">
              { currentItem.accountId }
            </strong>
          </CCallout>
        </CModalBody>
        <CModalFooter>
          <CButton
            color="success"
            onClick={() =>
              onHandleConfirmationClick(
                currentItem.linearId,
                auth.user.fullName
              )
            }
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
          <CModalTitle>Assigned Requests</CModalTitle>
        </CModalHeader>
        <CModalBody>
          <CAlert color="info">
            This view represents <strong>Requests</strong> that have been{" "}
            <CBadge color="success">{Constants.REQUEST_APPROVED}</CBadge> and
            matched to this <strong>Return</strong> by the <strong>Catan Ministry of Justice</strong>.{" "}
            <strong>Requests</strong> that are in{" "}
            <CBadge color="secondary">
              {Constants.REQUEST_TRANSFERRED}
            </CBadge>{" "}
            status represent a transfer of money from the <strong>Catan Treasury</strong> to the destination account.
          </CAlert>
          <RequestsSnapshotTable requests={requestsFromFundId} />
        </CModalBody>
        <CModalFooter>
          <CButton color="secondary" onClick={handleRequestsClose}>
            Close
          </CButton>
        </CModalFooter>
      </CModal>
      <FundHistory
        show={showHistory}
        fundId={currentItem.linearId ? currentItem.linearId : null}
        handleClose={handleCloseHistory}
      />
    </>
  );
};

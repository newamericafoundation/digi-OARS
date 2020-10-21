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
  CCallout,
  CSpinner,
  CButtonGroup,
  CModal,
  CModalHeader,
  CModalTitle,
  CModalBody,
  CModalFooter,
  CAlert,
  CFormGroup,
  CLabel,
  CSelect,
  CTooltip,
  CTextarea,
} from "@coreui/react";
import moment from "moment-timezone";
import { useAuth } from "auth-hook";
import axios from "axios";
import { APIContext } from "../../../providers/APIProvider";
import { toCountryByIsoFromX500, toCurrency } from "../../../utilities";
import cogoToast from "cogo-toast";
import { FundsContext } from "../../../providers/FundsProvider";
import { RequestHistory } from "./RequestHistory";

export const RequestsTable = ({
  filterStatus,
  requests,
  refreshFundsTableCallback,
  refreshRequestsTableCallback,
  isRequestor,
  isApprover,
  isIssuer,
  isReceiver,
  isTransferer,
  isObserver,
  isPartialRequestViewer,
}) => {
  const auth = useAuth();
  const [api] = useContext(APIContext);
  const [details, setDetails] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [show, setShow] = useState(false);
  const [showHistory, setShowHistory] = useState(false);
  const [currentItem, setCurrentItem] = useState({});
  const [, setCurrentItemIndex] = useState();
  const [currentRequestAction, setCurrentRequestAction] = useState("");
  const [fundsState] = useContext(FundsContext);
  const [fundStateLinearId, setFundStateLinearId] = useState("");
  const [rejectReason, setRejectReason] = useState("");

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

  const getFields = () => {
    if (isPartialRequestViewer) {
      return [
        { key: "authorizedUserDept", label: "Department" },
        { key: "amount" },
        { key: "createDateTime", label: "Created Date" },
        { key: "status", _style: { width: "20%" } },
        {
          key: "actions",
          _style: { width: "15%" },
          sorter: false,
          filter: false,
        },
        {
          key: "show_details",
          label: "",
          _style: { width: "1%" },
          sorter: false,
          filter: false,
        },
      ];
    }

    return [
      { key: "authorizedUserUsername", label: "Requestor" },
      { key: "authorizedUserDept", label: "Department" },
      { key: "amount" },
      { key: "createDateTime", label: "Created Date" },
      { key: "status", _style: { width: "20%" } },
      {
        key: "actions",
        _style: { width: "15%" },
        sorter: false,
        filter: false,
      },
      {
        key: "show_details",
        label: "",
        _style: { width: "1%" },
        sorter: false,
        filter: false,
      },
    ];
  };

  const getCurrentActionColor = (action) => {
    switch (action) {
      case "transfer":
        return "info";
      case "approve":
        return "success";
      case "reject":
        return "danger";
      default:
        return "primary";
    }
  };

  const getStatusBadge = (status) => {
    switch (status) {
      case "TRANSFERRED":
        return "secondary";
      case "APPROVED":
        return "success";
      case "PENDING":
        return "warning";
      case "REJECTED":
        return "danger";
      case "FLAGGED":
        return "info";
      default:
        return "primary";
    }
  };

  const getUrl = () => {
    if (currentRequestAction !== "transfer") {
      return (
        "http://" +
        window._env_.API_CLIENT_URL +
        ":" +
        api.port +
        "/api/request/" +
        currentRequestAction
      );
    }
    return (
      "http://" +
      window._env_.API_CLIENT_URL +
      ":" +
      api.port +
      "/api/" +
      currentRequestAction
    );
  };

  const responseMessage = (message) => {
    return (
      <div>
        <strong>Request ID:</strong> {message.data.entity.data.linearId.id}
        <br />
        <strong>Status:</strong>{" "}
        <CBadge
          color={
            message.data.entity.data.status
              ? getStatusBadge(message.data.entity.data.status)
              : getStatusBadge("TRANSFERRED")
          }
        >
          {message.data.entity.data.status
            ? message.data.entity.data.status
            : "TRANSFERRED"}
        </CBadge>
        <br />
        <strong>Amount:</strong>{" "}
        {toCurrency(message.data.entity.data.amount, "USD")}
      </div>
    );
  };

  const responseTransferMessage = (message) => {
    return (
      <div>
        <strong>Request ID:</strong> {message.data.entity.linearId.id}
        <br />
        <strong>Status:</strong>{" "}
        <CBadge
          color={
            message.data.entity.status
              ? getStatusBadge(message.data.entity.status)
              : getStatusBadge("TRANSFERRED")
          }
        >
          {message.data.entity.status
            ? message.data.entity.status
            : "TRANSFERRED"}
        </CBadge>
        <br />
        <strong>Amount:</strong> {toCurrency(message.data.entity.amount, "USD")}
      </div>
    );
  };

  const onHandleConfirmationClick = (
    requestStateLinearId,
    authorizerUserUsername,
    authorizerUserDept
  ) => {
    setIsLoading(true);

    if (currentRequestAction !== "transfer") {
      axios
        .put(
          getUrl(),
          null,
          currentRequestAction === "approve"
            ? {
                params: {
                  requestStateLinearId,
                  authorizerUserUsername,
                  authorizerUserDept,
                  fundStateLinearId,
                },
              }
            : {
                params: {
                  requestStateLinearId,
                  authorizerUserUsername,
                  authorizerUserDept,
                  rejectReason,
                },
              }
        )
        .then((response) => {
          setIsLoading(false);
          handleClose();
          refreshFundsTableCallback();
          refreshRequestsTableCallback();
          if (currentRequestAction === "approve") {
            const { hide } = cogoToast.success(responseMessage(response), {
              heading: "Request Approved",
              position: "top-right",
              hideAfter: 8,
              onClick: () => {
                hide();
              },
            });
          }
          if (currentRequestAction === "reject") {
            const { hide } = cogoToast.warn(responseMessage(response), {
              heading: "Request Rejected",
              position: "top-right",
              hideAfter: 8,
              onClick: () => {
                hide();
              },
            });
          }
        })
        .catch((err) => {
          const { hide } = cogoToast.error(err.message, {
            heading: "Error Accepting/Rejecting Request",
            position: "top-right",
            hideAfter: 8,
            onClick: () => {
              hide();
            },
          });
        });
    } else {
      axios
        .post(getUrl(), null, {
          params: {
            transferUsername: auth.user.fullName,
            requestId: requestStateLinearId,
          },
        })
        .then((response) => {
          setIsLoading(false);
          refreshFundsTableCallback();
          refreshRequestsTableCallback();
          handleClose();
          const { hide } = cogoToast.info(responseTransferMessage(response), {
            heading: "Request Transferred",
            position: "top-right",
            hideAfter: 8,
            onClick: () => {
              hide();
            },
          });
        })
        .catch((err) => {
          console.log(err);
          const { hide } = cogoToast.error(err.message, {
            heading: "Error Transferring Request",
            position: "top-right",
            hideAfter: 8,
            onClick: () => {
              hide();
            },
          });
        });
    }
  };

  const getData = () => {
    if (isApprover || isIssuer || isTransferer || isReceiver || isObserver) {
      if (filterStatus === "ALL") {
        return requests.data;
      }
      return requests.data.filter((request) => request.status === filterStatus);
    }

    if (filterStatus === "ALL") {
      if (isPartialRequestViewer) {
        return requests.data
      }
      return requests.data.filter(
        (request) =>
          request.authorizedUserDept ===
          auth.meta.keycloak.tokenParsed.groups[0]
      );
    }

    if (isRequestor && filterStatus === "PENDING") {
      return requests.data.filter(
        (request) =>
          (request.status === filterStatus || request.status === "FLAGGED") &&
          request.authorizedUserDept ===
            auth.meta.keycloak.tokenParsed.groups[0]
      );
    }

    if (isPartialRequestViewer && filterStatus === "PENDING") {
      return requests.data.filter(
        (request) =>
          (request.status === filterStatus || request.status === "FLAGGED")
      );
    } 
    
    if (isPartialRequestViewer) {
      return requests.data.filter((request) => (request.status === filterStatus))
    }

    return requests.data.filter(
      (request) =>
        request.status === filterStatus &&
        request.authorizedUserDept === auth.meta.keycloak.tokenParsed.groups[0]
    );
  };

  const getActionButton = (item) => {
    if (
      (item.status === Constants.REQUEST_PENDING ||
        item.status === Constants.REQUEST_FLAGGED) &&
      isApprover
    ) {
      return (
        <div className="float-left mb-0">
          <CButtonGroup className="mb-0 mr-2">
            <CButton
              color="success"
              variant="outline"
              shape="square"
              size="sm"
              onClick={() => {
                setCurrentRequestAction("approve");
                handleShow(item);
              }}
            >
              Approve
            </CButton>
          </CButtonGroup>
          <CButtonGroup className="mb-0">
            <CButton
              color="danger"
              variant="outline"
              shape="square"
              size="sm"
              onClick={() => {
                setCurrentRequestAction("reject");
                handleShow(item);
              }}
            >
              Reject
            </CButton>
          </CButtonGroup>
        </div>
      );
    }
    if (item.status === Constants.REQUEST_APPROVED && isTransferer) {
      return (
        <CButton
          className={"float-left mb-0"}
          color="info"
          variant="outline"
          shape="square"
          size="sm"
          onClick={() => {
            setCurrentRequestAction("transfer");
            handleShow(item);
          }}
        >
          Transfer
        </CButton>
      );
    }
  };

  return (
    <>
      <CDataTable
        items={getData()}
        fields={getFields()}
        columnFilter
        tableFilter
        itemsPerPageSelect
        itemsPerPage={5}
        hover
        sorter
        pagination
        scopedSlots={{
          amount: (item) => <td>{toCurrency(item.amount, item.currency)}</td>,
          balance: (item) => <td>{toCurrency(item.balance, item.currency)}</td>,
          maxWithdrawalAmount: (item) => (
            <td>{toCurrency(item.maxWithdrawalAmount, item.currency)}</td>
          ),
          createDateTime: (item) => (
            <td>
              {moment
                .tz(item.createDateTime, "UTC")
                .format(Constants.DATE_FORMAT)}
            </td>
          ),
          updateDateTime: (item) => (
            <td>
              {moment
                .tz(item.updateDateTime, "UTC")
                .format(Constants.DATE_FORMAT)}
            </td>
          ),
          status: (item) => (
            <td>
              <CBadge
                color={
                  (isRequestor || isPartialRequestViewer) &&
                  item.status === "FLAGGED"
                    ? "warning"
                    : getStatusBadge(item.status)
                }
              >
                {(isRequestor || isPartialRequestViewer) &&
                item.status === "FLAGGED"
                  ? "PENDING"
                  : item.status}
              </CBadge>
            </td>
          ),
          actions: (item, index) => {
            return <td>{getActionButton(item, index)}</td>;
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
                    setCurrentItemIndex(index);
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
                    Request Details{" "}
                    {!isRequestor && !isPartialRequestViewer && (
                      <CButton
                        className="float-right mt-1 mb-1"
                        color="secondary"
                        onClick={() => handleShowHistory(item)}
                      >
                        Show Request History
                      </CButton>
                    )}
                  </CCardHeader>
                  <CCardBody>
                    <CRow>
                      <CCol xl="6" sm="4">
                        <CCallout color="info" className={"bg-light"}>
                          <p className="text-muted mb-0">Request ID</p>
                          <strong className="p">{item.linearId}</strong>
                        </CCallout>
                        {!isPartialRequestViewer ? (
                          <CCallout color="info" className={"bg-light"}>
                            <p className="text-muted mb-0">Requestor</p>
                            <strong className="p">
                              {item.authorizedUserUsername}
                            </strong>
                          </CCallout>
                        ) : null}
                        <CCallout color="info" className={"bg-light"}>
                          <p className="text-muted mb-0">Department</p>
                          <strong className="p">
                            {item.authorizedUserDept}
                          </strong>
                        </CCallout>
                        <CCallout color="info" className={"bg-light"}>
                          <p className="text-muted mb-0">Amount</p>
                          <strong className="p">
                            {toCurrency(item.amount, item.currency)}
                          </strong>
                        </CCallout>
                        {!isRequestor && !isPartialRequestViewer ? (
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
                        ) : null}
                      </CCol>
                      <CCol xl="6" sm="4">
                        <CCallout color="info" className={"bg-light"}>
                          <p className="text-muted mb-0">Created Date/Time</p>
                          <strong className="p">
                            {moment
                              .tz(item.createDateTime, "UTC")
                              .format(Constants.DATETIME_FORMAT)}
                          </strong>
                        </CCallout>
                        {!isRequestor ? (
                          <CCallout color="info" className={"bg-light"}>
                            <p className="text-muted mb-0">Updated Date/Time</p>
                            <strong className="p">
                              {moment
                                .tz(item.updateDateTime, "UTC")
                                .format(Constants.DATETIME_FORMAT)}
                            </strong>
                          </CCallout>
                        ) : null}
                        {!isPartialRequestViewer ? (
                          <CCallout color="info" className={"bg-light"}>
                            <p className="text-muted mb-0">Account ID</p>
                            <strong className="p">
                              {item.externalAccountId}
                            </strong>
                          </CCallout>
                        ) : null}
                        <CCallout
                          color={
                            (isRequestor || isPartialRequestViewer) &&
                            item.status === "FLAGGED"
                              ? "warning"
                              : getStatusBadge(item.status)
                          }
                          className={"bg-light"}
                        >
                          <p className="text-muted mb-0">Status</p>
                          <strong className="p">
                            {(isRequestor || isPartialRequestViewer) &&
                            item.status === Constants.REQUEST_FLAGGED
                              ? "PENDING"
                              : item.status}
                            {item.status === Constants.REQUEST_APPROVED && !isPartialRequestViewer
                              ? " by " +
                                Object.keys(
                                  item.authorizerUserDeptAndUsername
                                ).map(
                                  (key) =>
                                    item.authorizerUserDeptAndUsername[key] +
                                    " [" +
                                    key +
                                    "]"
                                )
                              : null}
                            {item.status === Constants.REQUEST_REJECTED && !isPartialRequestViewer
                              ? " by " +
                                Object.keys(
                                  item.authorizerUserDeptAndUsername
                                ).map(
                                  (key) =>
                                    item.authorizerUserDeptAndUsername[key] +
                                    " [" +
                                    key +
                                    "]"
                                )
                              : null}
                            {item.status === Constants.REQUEST_TRANSFERRED && !isPartialRequestViewer
                              ? " by " +
                                item.transferUsername +
                                " [Catan Treasury]"
                              : null}
                          </strong>
                          {!isRequestor &&
                          !isPartialRequestViewer &&
                          item.status === Constants.REQUEST_FLAGGED ? (
                            <CRow>
                              <CCol>
                                <CAlert className="mt-2 mb-2" color="warning">
                                  Request amount has breached the maximum
                                  withdrawal amount of the system by{" "}
                                  {toCurrency(
                                    parseFloat(item.amount) -
                                      parseFloat(item.maxWithdrawalAmount),
                                    "USD"
                                  )}
                                  .
                                </CAlert>
                              </CCol>
                            </CRow>
                          ) : null}
                          {!isRequestor && !isPartialRequestViewer &&
                          item.status === Constants.REQUEST_REJECTED ? (
                            <div>
                              <p className="text-muted mt-2 mb-0">
                                Reject Reason
                              </p>
                              <strong className="p">{item.rejectReason}</strong>
                            </div>
                          ) : null}
                        </CCallout>
                      </CCol>
                    </CRow>
                    <CRow>
                      <CCol>
                        <CCallout color="info" className={"bg-light"}>
                          <p className="text-muted mb-0">Purpose</p>
                          <strong className="p">{item.purpose}</strong>
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
        color={getCurrentActionColor(currentRequestAction)}
        show={show}
        onClose={handleClose}
        size="lg"
      >
        <CModalHeader closeButton>
          <CModalTitle>
            {currentRequestAction.charAt(0).toUpperCase() +
              currentRequestAction.slice(1)}{" "}
            Confirmation
          </CModalTitle>
        </CModalHeader>
        <CModalBody>
          <CCallout
            color={getCurrentActionColor(currentRequestAction)}
            className={"bg-light"}
          >
            <p className="text-muted mb-0">Request ID</p>
            <strong className="p">{currentItem.linearId}</strong>
          </CCallout>
          <CCallout
            color={getCurrentActionColor(currentRequestAction)}
            className={"bg-light"}
          >
            <p className="text-muted mb-0">Requestor</p>
            <strong className="p">{currentItem.authorizedUserUsername}</strong>
          </CCallout>
          <CCallout
            color={getCurrentActionColor(currentRequestAction)}
            className={"bg-light"}
          >
            <p className="text-muted mb-0">Department</p>
            <strong className="p">{currentItem.authorizedUserDept}</strong>
          </CCallout>
          <CCallout
            color={getCurrentActionColor(currentRequestAction)}
            className={"bg-light"}
          >
            <p className="text-muted mb-0">Amount</p>
            <strong className="p">
              {toCurrency(currentItem.amount, "USD")}
            </strong>
          </CCallout>
          <CCallout
            color={getCurrentActionColor(currentRequestAction)}
            className={"bg-light"}
          >
            <p className="text-muted mb-0">Account ID</p>
            <strong className="p">{currentItem.externalAccountId}</strong>
          </CCallout>
          {currentRequestAction === "transfer" ? (
            <CCallout
              color={getStatusBadge(currentItem.status)}
              className={"bg-light"}
            >
              <p className="text-muted mb-0">Status</p>
              <strong className="p">
                {currentItem.status + " by "}
                {Object.keys(currentItem.authorizerUserDeptAndUsername).map(
                  (key) =>
                    currentItem.authorizerUserDeptAndUsername[key] +
                    " [" +
                    key +
                    "]"
                )}
              </strong>
            </CCallout>
          ) : null}
          {currentRequestAction === "approve" ? (
            <CFormGroup>
              <CTooltip
                content="Assign a Return (in Received status) to the request."
                placement="left"
              >
                <CLabel htmlFor="fund">Assign Return</CLabel>
              </CTooltip>
              <CSelect
                custom
                name="fund"
                id="fund"
                onChange={(e) => setFundStateLinearId(e.currentTarget.value)}
              >
                <option placeholder={0}></option>
                {fundsState.received
                  ? fundsState.received.map((item) => (
                      <option
                        disabled={item.balance < currentItem.amount}
                        key={item.linearId}
                        label={
                          item.linearId +
                          " - " +
                          toCurrency(item.balance, "USD") +
                          " - " +
                          toCountryByIsoFromX500(item.originParty)
                        }
                        value={item.linearId}
                      />
                    ))
                  : null}
              </CSelect>
            </CFormGroup>
          ) : null}
          {currentRequestAction === "reject" ? (
            <CFormGroup>
              <CLabel htmlFor="rejectReason">Rejection Reason</CLabel>
              <CTextarea
                onChange={(e) => setRejectReason(e.currentTarget.value)}
              ></CTextarea>
            </CFormGroup>
          ) : null}
        </CModalBody>
        <CModalFooter>
          <CButton
            color={getCurrentActionColor(currentRequestAction)}
            disabled={
              currentRequestAction === "approve"
                ? fundStateLinearId === ""
                : currentRequestAction === "reject"
                ? rejectReason.length === 0
                : false
            }
            onClick={() =>
              onHandleConfirmationClick(
                currentItem.linearId,
                auth.user.fullName,
                auth.meta.keycloak.tokenParsed.groups[0]
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
            {currentRequestAction.charAt(0).toUpperCase() +
              currentRequestAction.slice(1)}{" "}
            Request
          </CButton>

          <CButton color="secondary" onClick={handleClose}>
            Cancel
          </CButton>
        </CModalFooter>
      </CModal>
      <RequestHistory
        auth={auth}
        show={showHistory}
        requestId={currentItem.linearId ? currentItem.linearId : null}
        handleClose={handleCloseHistory}
      />
    </>
  );
};

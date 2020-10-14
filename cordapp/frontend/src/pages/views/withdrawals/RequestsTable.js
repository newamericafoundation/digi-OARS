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
} from "@coreui/react";
import moment from "moment-timezone";
import { useAuth } from "auth-hook";
import axios from "axios";
import { APIContext } from "../../../providers/APIProvider";
import { toCountryByIsoFromX500, toCurrency } from "../../../utilities";
import cogoToast from "cogo-toast";
import { FundsContext } from "../../../providers/FundsProvider";

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
}) => {
  const auth = useAuth();
  const [api] = useContext(APIContext);
  const [details, setDetails] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [show, setShow] = useState(false);
  const [currentItem, setCurrentItem] = useState({});
  const [, setCurrentItemIndex] = useState();
  const [currentRequestAction, setCurrentRequestAction] = useState("");
  const [currentFundBalance, setCurrentFundBalance] = useState();
  const [fundsState] = useContext(FundsContext);
  const [fundStateLinearId, setFundStateLinearId] = useState("");

  const handleShow = (item) => {
    setCurrentItem(item);
    axios
      .get(
        "http://" +
          window._env_.API_CLIENT_URL +
          ":" +
          api.port +
          "/api/fund/id?fundId=" +
          item.fundStateLinearId
      )
      .then((response) => {
        setCurrentFundBalance(response.data.entity.balance);
      })
      .catch((err) => console.log(err));
    setShow(true);
  };

  const handleClose = () => {
    setShow(false);
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
    { key: "authorizedUserUsername", label: "Requestor" },
    { key: "authorizedUserDept", label: "Department" },
    { key: "amount" },
    { key: "createDateTime", label: "Created Date" },
    { key: "status", _style: { width: "20%" } },
    { key: "actions", _style: { width: "15%" }, sorter: false, filter: false },
    {
      key: "show_details",
      label: "",
      _style: { width: "1%" },
      sorter: false,
      filter: false,
    },
  ];

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
              heading: "Withdrawal Request Approved",
              position: "top-right",
              hideAfter: 8,
              onClick: () => {
                hide();
              },
            });
          }
          if (currentRequestAction === "reject") {
            const { hide } = cogoToast.warn(responseMessage(response), {
              heading: "Withdrawal Request Rejected",
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
            requestId: requestStateLinearId,
          },
        })
        .then((response) => {
          setIsLoading(false);
          refreshFundsTableCallback();
          refreshRequestsTableCallback();
          handleClose();
          const { hide } = cogoToast.info(responseTransferMessage(response), {
            heading: "Withdrawal Request Transferred",
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
      return requests.data.filter(
        (request) =>
          request.authorizedUserDept ===
          auth.meta.keycloak.tokenParsed.groups[0]
      );
    }

    if (isRequestor && filterStatus === "PENDING") {
      return requests.data.filter(
        (request) =>
          (request.status === filterStatus ||
          request.status === "FLAGGED") &&
          request.authorizedUserDept ===
            auth.meta.keycloak.tokenParsed.groups[0]
      );
    }

    return requests.data.filter(
      (request) =>
        request.status === filterStatus &&
        request.authorizedUserDept === auth.meta.keycloak.tokenParsed.groups[0]
    );
  };

  const getActionButton = (item) => {
    if ((item.status === Constants.REQUEST_PENDING || item.status === Constants.REQUEST_FLAGGED) && isApprover) {
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
        fields={fields}
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
                  isRequestor && item.status === "FLAGGED"
                    ? "warning"
                    : getStatusBadge(item.status)
                }
              >
                {isRequestor && item.status === "FLAGGED"
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
                  <CCardHeader>Request Details</CCardHeader>
                  <CCardBody>
                    <CRow>
                      <CCol xl="6" sm="4">
                        <CCallout color="info" className={"bg-light"}>
                          <p className="text-muted mb-0">Requestor</p>
                          <strong className="p">
                            {item.authorizedUserUsername}
                          </strong>
                        </CCallout>
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
                        <CCallout color="info" className={"bg-light"}>
                          <p className="text-muted mb-0">Created Date/Time</p>
                          <strong className="p">
                            {moment
                              .tz(item.createDateTime, "UTC")
                              .format(Constants.DATETIME_FORMAT)}
                          </strong>
                        </CCallout>
                      </CCol>
                      <CCol xl="6" sm="4">
                        <CCallout color="info" className={"bg-light"}>
                          <p className="text-muted mb-0">State ID</p>
                          <strong className="p">{item.linearId}</strong>
                        </CCallout>
                        <CCallout color="info" className={"bg-light"}>
                          <p className="text-muted mb-0">Account ID</p>
                          <strong className="p">
                            {item.externalAccountId}
                          </strong>
                        </CCallout>
                        <CCallout
                          color={(isRequestor && item.status === "FLAGGED") ? "warning" : getStatusBadge(item.status)}
                          className={"bg-light"}
                        >
                          <p className="text-muted mb-0">Status</p>
                          <strong className="p">
                            {(isRequestor && item.status === "FLAGGED") ? "PENDING" : item.status}
                            {item.status === Constants.REQUEST_APPROVED
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
                            {item.status === Constants.REQUEST_REJECTED
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
                          </strong>
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
            color={
              isApprover
                ? parseFloat(currentFundBalance) <
                  parseFloat(currentItem.amount)
                  ? "warning"
                  : getCurrentActionColor(currentRequestAction)
                : getCurrentActionColor(currentRequestAction)
            }
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
          {isApprover &&
          parseFloat(currentFundBalance) < parseFloat(currentItem.amount) ? (
            <CAlert color="warning">
              <p>Insufficient Return balance available.</p>
              <dl className="row">
                <dt className="col-sm-6">Return Balance Available:</dt>
                <dd className="col-sm-6">
                  {toCurrency(currentFundBalance, "USD")}
                </dd>
                <dt className="col-sm-6">Request Amount:</dt>
                <dd className="col-sm-6">
                  {toCurrency(currentFundBalance, "USD")}
                </dd>
                <dt className="col-sm-6">Difference:</dt>
                <dd className="col-sm-6">
                  {toCurrency(currentFundBalance - currentItem.amount, "USD")}
                </dd>
              </dl>
              <p>You will not be able to approve this request.</p>
            </CAlert>
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
        </CModalBody>
        <CModalFooter>
          <CButton
            color={getCurrentActionColor(currentRequestAction)}
            disabled={
              currentRequestAction === "approve"
                ? parseFloat(currentFundBalance) <
                    parseFloat(currentItem.amount) || fundStateLinearId === ""
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
    </>
  );
};

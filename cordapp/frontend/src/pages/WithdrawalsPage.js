import React, { useContext, useEffect, useState } from "react";
import {
  CCard,
  CCardBody,
  CCardHeader,
  CWidgetProgressIcon,
  CRow,
  CCol,
  CCallout,
  CButton,
  CButtonGroup,
} from "@coreui/react";
import CIcon from "@coreui/icons-react";
import { AvailableFundsTable } from "./views/funds/AvailableFundsTable";
import { RequestsTable } from "./views//withdrawals/RequestsTable";
import { FundsContext } from "../providers/FundsProvider";
import { RequestsContext } from "../providers/RequestsProvider";
import * as Constants from "../constants";
import { useAuth } from "auth-hook";
import { toCurrency } from "../utilities";
import useInterval from "../interval-hook";

const WithdrawalsPage = () => {
  const auth = useAuth();
  const [fundsState, fundsCallback] = useContext(FundsContext);
  const [requestsState, requestsCallback] = useContext(RequestsContext);
  const [isFundsIssuer, setIsFundsIssuer] = useState(false);
  const [isFundsReceiver, setIsFundsReceiver] = useState(false);
  const [isFundsRequestor, setIsFundsRequestor] = useState(false);
  const [isRequestApprover, setIsRequestApprover] = useState(false);
  const [isRequestTransferer, setIsRequestTransferer] = useState(false);
  const [isObserver, setIsObserver] = useState(false);
  const [requestsFilterStatus, setRequestsFilterStatus] = useState("ALL");

  const handleTableFilter = (filterValue) => {
    setRequestsFilterStatus(filterValue);
  };

  useEffect(() => {
    if (auth.isAuthenticated) {
      setIsFundsIssuer(auth.meta.keycloak.hasResourceRole("funds_issuer"));
      setIsFundsReceiver(auth.meta.keycloak.hasResourceRole("funds_receiver"));
      setIsFundsRequestor(
        auth.meta.keycloak.hasResourceRole("funds_requestor")
      );
      setIsRequestApprover(
        auth.meta.keycloak.hasResourceRole("request_approver")
      );
      setIsRequestTransferer(
        auth.meta.keycloak.hasResourceRole("request_transferer")
      );
      setIsObserver(auth.meta.keycloak.hasResourceRole("observer"));
    }
  }, [auth]);

  useInterval(() => {
    if (auth.isAuthenticated) {
      fundsCallback();
      requestsCallback();
    }
  }, Constants.REFRESH_INTERVAL_MS);

  const getCalloutColor = () => {
    switch (requestsFilterStatus) {
      case "ALL":
        return "secondary";
      case "PENDING":
        return "warning";
      case "APPROVED":
        return "success";
      case "TRANSFERRED":
        return "dark";
      case "REJECTED":
        return "danger";
      case "FLAGGED":
        return "info";
      default:
        return "warning";
    }
  };

  return (
    <>
      <CRow>
        <CCol xs="12" sm="6" lg="3">
          <CWidgetProgressIcon
            inverse
            header={toCurrency(requestsState.approvedAmount, "USD").toString()}
            text="Approved Withdrawal Requests"
            color="gradient-success"
            value={
              (requestsState.approvedAmount / requestsState.totalAmount) * 100
            }
          >
            <CIcon name="cil-check-circle" height="36" />
          </CWidgetProgressIcon>
        </CCol>
        <CCol xs="12" sm="6" lg="3">
          <CWidgetProgressIcon
            inverse
            header={toCurrency(requestsState.pendingAmount, "USD").toString()}
            text="Pending Withdrawal Requests"
            color="gradient-warning"
            value={
              (requestsState.pendingAmount / requestsState.totalAmount) * 100
            }
          >
            <CIcon name="cil-av-timer" height="36" />
          </CWidgetProgressIcon>
        </CCol>
        <CCol xs="12" sm="6" lg="3">
          <CWidgetProgressIcon
            inverse
            header={toCurrency(requestsState.rejectedAmount, "USD").toString()}
            text="Rejected Withdrawal Requests"
            color="gradient-danger"
            value={
              (requestsState.rejectedAmount / requestsState.totalAmount) * 100
            }
          >
            <CIcon name="cil-x-circle" height="36" />
          </CWidgetProgressIcon>
        </CCol>
        <CCol xs="12" sm="6" lg="3">
          <CWidgetProgressIcon
            inverse
            header={toCurrency(requestsState.flaggedAmount, "USD").toString()}
            text="Flagged Withdrawal Requests"
            color="gradient-info"
            value={
              (requestsState.flaggedAmount / requestsState.totalAmount) * 100
            }
          >
            <CIcon name="cil-flag-alt" height="36" />
          </CWidgetProgressIcon>
        </CCol>
      </CRow>
      {isFundsRequestor ? (
        <CRow>
          <CCol>
            <CCard>
              <CCardHeader>
                <div className="mb-0">
                  <CCallout className="float-left mt-1 mb-1">
                    <h4 className="mt-1">Available Money</h4>
                  </CCallout>
                </div>
              </CCardHeader>
              <CCardBody>
                <AvailableFundsTable
                  funds={fundsState}
                  refreshFundsTableCallback={fundsCallback}
                  refreshRequestsTableCallback={requestsCallback}
                  isRequestor={isFundsRequestor}
                />
              </CCardBody>
            </CCard>
          </CCol>
        </CRow>
      ) : null}
      <CRow>
        <CCol>
          <CCard>
            <CCardHeader>
              <div className="mb-0">
                <CCallout
                  className="float-left mt-1 mb-1"
                  color={getCalloutColor()}
                >
                  <h4 className="mt-1 text-dark">
                    {requestsFilterStatus.charAt(0).toUpperCase() +
                      requestsFilterStatus.slice(1).toLowerCase()}{" "}
                    Withdrawal Requests
                  </h4>
                </CCallout>
                <CButtonGroup className="float-right mr-3 mt-1 mb-1">
                  <CButton
                    color="outline-dark"
                    className="mx-0"
                    key="All"
                    active={"ALL" === requestsFilterStatus}
                    onClick={() => handleTableFilter("ALL")}
                  >
                    All
                  </CButton>
                  <CButton
                    color="outline-dark"
                    className="mx-0"
                    key="Pending"
                    active={"PENDING" === requestsFilterStatus}
                    onClick={() => handleTableFilter("PENDING")}
                  >
                    Pending
                  </CButton>
                  <CButton
                    color="outline-dark"
                    className="mx-0"
                    key="Approved"
                    active={"APPROVED" === requestsFilterStatus}
                    onClick={() => handleTableFilter("APPROVED")}
                  >
                    Approved
                  </CButton>
                  <CButton
                    color="outline-dark"
                    className="mx-0"
                    key="Transferred"
                    active={"TRANSFERRED" === requestsFilterStatus}
                    onClick={() => handleTableFilter("TRANSFERRED")}
                  >
                    Transferred
                  </CButton>
                  <CButton
                    color="outline-dark"
                    className="mx-0"
                    key="Rejected"
                    active={"REJECTED" === requestsFilterStatus}
                    onClick={() => handleTableFilter("REJECTED")}
                  >
                    Rejected
                  </CButton>
                  <CButton
                    color="outline-dark"
                    className="mx-0"
                    key="Flagged"
                    active={"FLAGGED" === requestsFilterStatus}
                    onClick={() => handleTableFilter("FLAGGED")}
                  >
                    Flagged
                  </CButton>
                </CButtonGroup>
              </div>
            </CCardHeader>{" "}
            <CCardBody>
              <RequestsTable
                filterStatus={requestsFilterStatus}
                requests={requestsState}
                refreshFundsTableCallback={fundsCallback}
                refreshRequestsTableCallback={requestsCallback}
                isApprover={isRequestApprover}
                isIssuer={isFundsIssuer}
                isReceiver={isFundsReceiver}
                isTransferer={isRequestTransferer}
                isObserver={isObserver}
              />
            </CCardBody>
          </CCard>
        </CCol>
      </CRow>
    </>
  );
};

export default WithdrawalsPage;

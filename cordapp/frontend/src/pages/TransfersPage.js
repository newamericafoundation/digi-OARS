import React, { useContext, useEffect, useState } from "react";
import {
  CCard,
  CCardBody,
  CCardHeader,
  // CWidgetProgressIcon,
  CRow,
  CCol,
  CCallout,
  CButtonGroup,
  CButton,
} from "@coreui/react";
// import CIcon from "@coreui/icons-react";
import { RequestsTable } from "./views/withdrawals/RequestsTable";
import { FundsContext } from "../providers/FundsProvider";
import { RequestsContext } from "../providers/RequestsProvider";
import * as Constants from "../constants";
import { useAuth } from "auth-hook";
import { TransfersContext } from "../providers/TransfersProvider";
// import { toCurrency } from "../utilities";
import useInterval from "../interval-hook";

const TransfersPage = () => {
  const auth = useAuth();
  const [, fundsCallback] = useContext(FundsContext);
  const [requestsState, requestsCallback] = useContext(RequestsContext);
  const [, transfersCallback] = useContext(TransfersContext);
  const [isFundsIssuer, setIsFundsIssuer] = useState(false);
  const [isFundsReceiver, setIsFundsReceiver] = useState(false);
  const [isRequestApprover, setIsRequestApprover] = useState(false);
  const [isRequestTransferer, setIsRequestTransferer] = useState(false);
  const [isObserver, setIsObserver] = useState(false);
  const [requestsFilterStatus, setRequestsFilterStatus] = useState("APPROVED");

  const handleTableFilter = (filterValue) => {
    setRequestsFilterStatus(filterValue);
  };

  useEffect(() => {
    if (auth.isAuthenticated) {
      setIsFundsIssuer(auth.meta.keycloak.hasResourceRole("funds_issuer"));
      setIsFundsReceiver(auth.meta.keycloak.hasResourceRole("funds_receiver"));
      setIsRequestApprover(
        auth.meta.keycloak.hasResourceRole("request_approver")
      );
      setIsRequestTransferer(
        auth.meta.keycloak.hasResourceRole("request_transferer")
      );
      setIsObserver(
        auth.meta.keycloak.hasResourceRole("observer")
      );
    }
  }, [auth]);

  useInterval(() => {
    if (auth.isAuthenticated) {
      fundsCallback();
      requestsCallback();
      transfersCallback();
    }
  }, Constants.REFRESH_INTERVAL_MS);

  const getCalloutColor = () => {
    switch (requestsFilterStatus) {
      case "APPROVED":
        return "success";
      case "TRANSFERRED":
        return "dark";
      default:
        return "warning";
    }
  };

  return (
    <>
      {/* <CRow>
        <CCol xs="12" sm="6" lg="4">
          <CWidgetProgressIcon
            inverse
            header={toCurrency(
              requestsState.transferredAmount,
              "USD"
            ).toString()}
            text="Transferred Requests"
            color="gradient-info"
            value={
              (requestsState.transferredAmount /
                (requestsState.pendingAmount +
                  requestsState.approvedAmount +
                  requestsState.transferredAmount)) *
              100
            }
          >
            <CIcon name="cil-chevron-right" height="36" />
          </CWidgetProgressIcon>
        </CCol>
        <CCol xs="12" sm="6" lg="4">
          <CWidgetProgressIcon
            inverse
            header={toCurrency(requestsState.approvedAmount, "USD").toString()}
            text="Approved Requests"
            color="gradient-warning"
            value={
              (requestsState.approvedAmount /
                (requestsState.pendingAmount +
                  requestsState.approvedAmount +
                  requestsState.transferredAmount)) *
              100
            }
          >
            <CIcon name="cil-av-timer" height="36" />
          </CWidgetProgressIcon>
        </CCol>
      </CRow> */}
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
                    Requests
                  </h4>
                </CCallout>
                <CButtonGroup className="float-right mr-3 mt-1 mb-1">
                  <CButton
                    color="outline-dark"
                    className="mx-0"
                    key="APPROVED"
                    active={"APPROVED" === requestsFilterStatus}
                    onClick={() => handleTableFilter("APPROVED")}
                  >
                    Approved
                  </CButton>
                  <CButton
                    color="outline-dark"
                    className="mx-0"
                    key="TRANSFERRED"
                    active={"TRANSFERRED" === requestsFilterStatus}
                    onClick={() => handleTableFilter("TRANSFERRED")}
                  >
                    Transferred
                  </CButton>
                </CButtonGroup>
              </div>
            </CCardHeader>
            <CCardBody>
              <RequestsTable
                filterStatus={requestsFilterStatus}
                requests={requestsState}
                refreshFundsTableCallback={fundsCallback}
                refreshRequestsTableCallback={requestsCallback}
                refreshTransfersTableCallback={transfersCallback}
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

export default TransfersPage;

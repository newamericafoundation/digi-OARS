import React, { useContext, useEffect, useState } from "react";
import {
  CCard,
  CCardBody,
  CCardHeader,
  CWidgetProgressIcon,
  CRow,
  CCol,
  CCallout,
} from "@coreui/react";
import CIcon from "@coreui/icons-react";
import { AvailableFundsTable } from "./views/funds/AvailableFundsTable";
import { RequestsTable } from "./views//withdrawals/RequestsTable";
import { FundsContext } from "../providers/FundsProvider";
import { RequestsContext } from "../providers/RequestsProvider";
import * as Constants from "../constants";
import { useAuth } from "auth-hook";
import { TransfersContext } from "../providers/TransfersProvider";
import { toCurrency } from "../utilities";
import useInterval from "../interval-hook";

const WithdrawalsPage = () => {
  const auth = useAuth();
  const [fundsState, fundsCallback] = useContext(FundsContext);
  const [requestsState, requestsCallback] = useContext(RequestsContext);
  const [, transfersCallback] = useContext(TransfersContext);
  const [isFundsIssuer, setIsFundsIssuer] = useState(false);
  const [isFundsReceiver, setIsFundsReceiver] = useState(false);
  const [isFundsRequestor, setIsFundsRequestor] = useState(false);
  const [isRequestApprover, setIsRequestApprover] = useState(false);
  const [isRequestTransferer, setIsRequestTransferer] = useState(false);

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
    }
  }, [auth]);

  useInterval(() => {
    if (auth.isAuthenticated) {
      fundsCallback();
      requestsCallback();
      transfersCallback();
    }
  }, Constants.REFRESH_INTERVAL_MS);

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
      <CRow>
        <CCol>
          <CCard>
            <CCardHeader>
              <div className="mb-0">
                <CCallout className="float-left mt-1 mb-1">
                  <h4 className="mt-1">Available Funds</h4>
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
      <CRow>
        <CCol>
          <CCard>
            <CCardHeader>
              <div className="mb-0">
                <CCallout className="float-left mt-1 mb-1">
                  <h4 className="mt-1">Pending Withdrawal Requests</h4>
                </CCallout>
              </div>
            </CCardHeader>{" "}
            <CCardBody>
              <RequestsTable
                filterStatus={Constants.REQUEST_PENDING}
                requests={requestsState}
                refreshFundsTableCallback={fundsCallback}
                refreshRequestsTableCallback={requestsCallback}
                refreshTransfersTableCallback={transfersCallback}
                isApprover={isRequestApprover}
                isIssuer={isFundsIssuer}
                isReceiver={isFundsReceiver}
                isTransferer={isRequestTransferer}
              />
            </CCardBody>
          </CCard>
        </CCol>
        <CCol>
          <CCard>
            <CCardHeader>
              <div className="mb-0">
                <CCallout className="float-left mt-1 mb-1">
                  <h4 className="mt-1">
                    Approved Withdrawal Requests (Awaiting Transfer)
                  </h4>
                </CCallout>
              </div>
            </CCardHeader>
            <CCardBody>
              <RequestsTable
                filterStatus={Constants.REQUEST_APPROVED}
                requests={requestsState}
                refreshFundsTableCallback={fundsCallback}
                refreshRequestsTableCallback={requestsCallback}
                refreshTransfersTableCallback={transfersCallback}
                isApprover={isRequestApprover}
                isIssuer={isFundsIssuer}
                isReceiver={isFundsReceiver}
                isTransferer={isRequestTransferer}
              />
            </CCardBody>
          </CCard>
        </CCol>
      </CRow>
      <CRow>
        <CCol>
          <CCard>
            <CCardHeader>
              <div className="mb-0">
                <CCallout className="float-left mt-1 mb-1">
                  <h4 className="mt-1">Rejected Withdrawal Requests</h4>
                </CCallout>
              </div>
            </CCardHeader>
            <CCardBody>
              <RequestsTable
                filterStatus={Constants.REQUEST_REJECTED}
                requests={requestsState}
                refreshFundsTableCallback={fundsCallback}
                refreshRequestsTableCallback={requestsCallback}
                refreshTransfersTableCallback={transfersCallback}
                isApprover={isRequestApprover}
                isIssuer={isFundsIssuer}
                isReceiver={isFundsReceiver}
                isTransferer={isRequestTransferer}
              />
            </CCardBody>
          </CCard>
        </CCol>
        <CCol>
          <CCard>
            <CCardHeader>
              <div className="mb-0">
                <CCallout className="float-left mt-1 mb-1">
                  <h4 className="mt-1">Flagged Withdrawal Requests</h4>
                </CCallout>
              </div>
            </CCardHeader>
            <CCardBody>
              <RequestsTable
                filterStatus={Constants.REQUEST_FLAGGED}
                requests={requestsState}
                refreshFundsTableCallback={fundsCallback}
                refreshRequestsTableCallback={requestsCallback}
                refreshTransfersTableCallback={transfersCallback}
                isApprover={isRequestApprover}
                isIssuer={isFundsIssuer}
                isReceiver={isFundsReceiver}
                isTransferer={isRequestTransferer}
              />
            </CCardBody>
          </CCard>
        </CCol>
      </CRow>
    </>
  );
};

export default WithdrawalsPage;

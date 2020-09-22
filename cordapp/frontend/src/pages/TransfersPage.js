import React, { useContext, useEffect, useState } from "react";
import {
  CCard,
  CCardBody,
  CCardHeader,
  CWidgetProgressIcon,
  CRow,
  CCol,
} from "@coreui/react";
import CIcon from "@coreui/icons-react";
import { RequestsTable } from "./views/withdrawals/RequestsTable";
import { FundsContext } from "../providers/FundsProvider";
import { RequestsContext } from "../providers/RequestsProvider";
import * as Constants from "../constants";
import { useAuth } from "auth-hook";
import { TransfersContext } from "../providers/TransfersProvider";
import { toCurrency } from "../utilities";

const TransfersPage = () => {
  const auth = useAuth();
  const [, fundsCallback] = useContext(FundsContext);
  const [requestsState, requestsCallback] = useContext(RequestsContext);
  const [, transfersCallback] = useContext(TransfersContext);
  const [isFundsIssuer, setIsFundsIssuer] = useState(false);
  const [isFundsReceiver, setIsFundsReceiver] = useState(false);
  const [isRequestApprover, setIsRequestApprover] = useState(false);
  const [isRequestTransferer, setIsRequestTransferer] = useState(false);

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
    }
  }, [auth]);

  return (
    <>
      <CRow>
        <CCol xs="12" sm="6" lg="4">
          <CWidgetProgressIcon
            inverse
            header={toCurrency(requestsState.transferredAmount, "USD").toString()}
            text="Transferred Withdrawal Requests"
            color="gradient-info"
            value={(requestsState.transferredAmount / (requestsState.pendingAmount +
              requestsState.approvedAmount +
              requestsState.transferredAmount)) * 100}
          >
            <CIcon name="cil-chevron-right" height="36" />
          </CWidgetProgressIcon>
        </CCol>
        <CCol xs="12" sm="6" lg="4">
          <CWidgetProgressIcon
            inverse
            header={toCurrency(
              requestsState.approvedAmount,
              "USD"
            ).toString()}
            text="Withdrawal Requests Awaiting Transfer"
            color="gradient-warning"
            value={
              (requestsState.approvedAmount / (requestsState.pendingAmount +
                requestsState.approvedAmount +
                requestsState.transferredAmount)) * 100
            }
          >
            <CIcon name="cil-av-timer" height="36" />
          </CWidgetProgressIcon>
        </CCol>
      </CRow>
      <CRow>
        <CCol>
          <CCard>
            <CCardHeader>Approved Withdrawal Requests</CCardHeader>
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
          <CCard>
            <CCardHeader>Transferred Withdrawal Requests</CCardHeader>
            <CCardBody>
              <RequestsTable
                filterStatus={Constants.REQUEST_TRANSFERRED}
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

export default TransfersPage;

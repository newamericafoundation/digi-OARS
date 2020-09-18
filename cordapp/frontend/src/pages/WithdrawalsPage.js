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
import { AvailableFundsTable } from "./views/funds/AvailableFundsTable";
import { RequestsTable } from "./views//withdrawals/RequestsTable";
import { FundsContext } from "../providers/FundsProvider";
import { RequestsContext } from "../providers/RequestsProvider";
import * as Constants from "../constants";
import { useAuth } from "auth-hook";

const WithdrawalsPage = () => {
  const auth = useAuth();
  const [fundsState, fundsCallback] = useContext(FundsContext);
  const [requestsState, requestsCallback] = useContext(RequestsContext);
  const [isFundsIssuer, setIsFundsIssuer] = useState(false);
  const [isFundsReceiver, setIsFundsReceiver] = useState(false);
  const [isFundsRequestor, setIsFundsRequestor] = useState(false);
  const [isRequestApprover, setIsRequestApprover] = useState(false);
  const [isRequestTransferer, setIsRequestTransferer] = useState(false);

  useEffect(() => {
    if (auth.isAuthenticated) {
      setIsFundsIssuer(
        auth.meta.keycloak.hasResourceRole("funds_issuer")
      );
      setIsFundsReceiver(
        auth.meta.keycloak.hasResourceRole("funds_receiver")
      );
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

  const toCurrency = (number, currency) => {
    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: currency,
    }).format(number);
  };

  const requestsTotal = requestsState.data.reduce(
    (total, request) =>
      total + parseFloat(request.amount),
    0
  );

  const requestsPendingTotal = requestsState.data
    .filter((request) => request.status === Constants.REQUEST_PENDING)
    .reduce(
      (total, request) =>
      total + parseFloat(request.amount),
      0
    );

  return (
    <>
      <CRow>
        <CCol xs="12" sm="6" lg="3">
          <CWidgetProgressIcon
            inverse
            header={toCurrency(
              requestsTotal - requestsPendingTotal,
              "USD"
            ).toString()}
            text="Approved Withdrawal Requests"
            color="gradient-success"
            value={
              ((requestsTotal - requestsPendingTotal) / requestsTotal) * 100
            }
          >
            <CIcon name="cil-check-circle" height="36" />
          </CWidgetProgressIcon>
        </CCol>
        <CCol xs="12" sm="6" lg="3">
          <CWidgetProgressIcon
            inverse
            header={toCurrency(requestsPendingTotal, "USD").toString()}
            text="Pending Withdrawal Requests"
            color="gradient-warning"
            value={(requestsPendingTotal / requestsTotal) * 100}
          >
            <CIcon name="cil-av-timer" height="36" />
          </CWidgetProgressIcon>
        </CCol>
      </CRow>
      <CRow>
        <CCol>
          <CCard>
            <CCardHeader>Available Funds</CCardHeader>
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
            <CCardHeader>Pending Withdrawal Requests</CCardHeader>
            <CCardBody>
              <RequestsTable
                filterStatus={Constants.REQUEST_PENDING}
                requests={requestsState}
                refreshFundsTableCallback={fundsCallback}
                refreshRequestsTableCallback={requestsCallback}
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
            <CCardHeader>Approved Withdrawal Requests (Awaiting Transfer)</CCardHeader>
            <CCardBody>
              <RequestsTable
                filterStatus={Constants.REQUEST_APPROVED}
                requests={requestsState}
                refreshFundsTableCallback={fundsCallback}
                refreshRequestsTableCallback={requestsCallback}
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

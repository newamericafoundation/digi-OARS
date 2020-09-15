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

const TransfersPage = () => {
  const auth = useAuth();
  const [, fundsCallback] = useContext(FundsContext);
  const [requestsState, requestsCallback] = useContext(RequestsContext);
  const [isRequestApprover, setIsRequestApprover] = useState(false);

  useEffect(() => {
    if (auth.isAuthenticated) {
      setIsRequestApprover(
        auth.meta.keycloak.hasResourceRole("request_approver")
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
    (totalRequestsAmount, request) =>
      totalRequestsAmount + parseFloat(request.amount),
    0
  );

  const requestsPendingTotal = requestsState.data
    .filter((request) => request.status === Constants.REQUEST_PENDING)
    .reduce(
      (totalRequestsPendingAmount, request) =>
        totalRequestsPendingAmount + parseFloat(request.amount),
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
            text="Transferred Withdrawal Requests"
            color="gradient-info"
            value={(requestsPendingTotal / requestsTotal) * 100}
          >
            <CIcon name="cil-chevron-right" height="36" />
          </CWidgetProgressIcon>
        </CCol>
      </CRow>
      <CRow>
        <CCol>
          <CCard>
            <CCardHeader>Requests Approved</CCardHeader>
            <CCardBody>
              <RequestsTable
                filterStatus={Constants.REQUEST_APPROVED}
                requests={requestsState}
                refreshFundsTableCallback={fundsCallback}
                refreshRequestsTableCallback={requestsCallback}
                isApprover={isRequestApprover}
              />
            </CCardBody>
          </CCard>
        </CCol>
      </CRow>
    </>
  );
};

export default TransfersPage;

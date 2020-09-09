import React, { useContext } from "react";
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
import { RequestData } from "../data/Requests";
import { FundsContext } from "../providers/FundsProvider";
import { RequestsContext } from "../providers/RequestsProvider";
import * as Constants from "../constants";

const WithdrawalsPage = () => {
  const [fundsState, fundsCallback] = useContext(FundsContext);
  const [requestsState, requestsCallback] = useContext(RequestsContext);

  const toCurrency = (number, currency) => {
    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: currency,
    }).format(number);
  };

  const requestsTotal = requestsState.data.reduce(
    (totalRequestsAmount, request) => totalRequestsAmount + parseFloat(request.amount), 0
  );

  const requestsPendingTotal = requestsState.data
    .filter((request) => request.status === Constants.REQUEST_PENDING)
    .reduce(
      (totalRequestsPendingAmount, request) => totalRequestsPendingAmount + parseFloat(request.amount), 0);

  return (
    <>
      <CRow>
        <CCol xs="12" sm="6" lg="3">
          <CWidgetProgressIcon
            inverse
            header={toCurrency((requestsTotal - requestsPendingTotal), "USD").toString()}
            text="Approved Withdrawal Requests"
            color="gradient-success"
            value={((requestsTotal - requestsPendingTotal) / requestsTotal) * 100}
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
                refreshTableCallback={requestsCallback}
              />
            </CCardBody>
          </CCard>
        </CCol>
        <CCol>
          <CCard>
            <CCardHeader>Approved Withdrawal Requests</CCardHeader>
            <CCardBody>
              <RequestsTable
                filterStatus={Constants.REQUEST_APPROVED}
                requests={requestsState}
                refreshTableCallback={requestsCallback}
              />
            </CCardBody>
          </CCard>
        </CCol>
      </CRow>
    </>
  );
};

export default WithdrawalsPage;

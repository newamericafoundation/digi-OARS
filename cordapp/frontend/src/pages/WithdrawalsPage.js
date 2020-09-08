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

const WithdrawalsPage = () => {
  const [fundsState, fundsCallback] = useContext(FundsContext);

  const toCurrency = (number, currency) => {
    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: currency,
    }).format(number);
  };

  const total = RequestData.reduce(
    (totalFunds, request) => totalFunds + request.amount,
    0
  );

  const pending = RequestData.filter(
    (request) => request.status === "PENDING"
  ).reduce((totalFunds, request) => totalFunds + request.amount, 0);

  const approved = total - pending;

  return (
    <>
      <CRow>
        <CCol xs="12" sm="6" lg="3">
          <CWidgetProgressIcon
            inverse
            header={toCurrency(approved, "USD").toString()}
            text="Approved Withdrawal Requests"
            color="gradient-success"
            value={(approved / total) * 100}
          >
            <CIcon name="cil-check-circle" height="36" />
          </CWidgetProgressIcon>
        </CCol>
        <CCol xs="12" sm="6" lg="3">
          <CWidgetProgressIcon
            inverse
            header={toCurrency(pending, "USD").toString()}
            text="Pending Withdrawal Requests"
            color="gradient-warning"
            value={(pending / total) * 100}
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
              <AvailableFundsTable funds={fundsState} />
            </CCardBody>
          </CCard>
        </CCol>
      </CRow>
      <CRow>
        <CCol>
          <CCard>
            <CCardHeader>Pending Withdrawal Requests</CCardHeader>
            <CCardBody>
              <RequestsTable status="PENDING" />
            </CCardBody>
          </CCard>
        </CCol>
        <CCol>
          <CCard>
            <CCardHeader>Approved Withdrawal Requests</CCardHeader>
            <CCardBody>
              <RequestsTable status="APPROVED" />
            </CCardBody>
          </CCard>
        </CCol>
      </CRow>
    </>
  );
};

export default WithdrawalsPage;

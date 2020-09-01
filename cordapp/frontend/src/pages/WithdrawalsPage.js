import React, { useState } from "react";
import {
  CCard,
  CCardBody,
  CCardHeader,
  CModal,
  CModalHeader,
  CModalBody,
  CModalTitle,
  CRow,
  CCol,
  CWidgetProgress,
} from "@coreui/react";
import { AvailableFundsTable } from "./views/funds/AvailableFundsTable";
import { RequestsTable } from "./views//withdrawals/RequestsTable";
import { RequestData } from "../data/Requests";
// import { FundsForm } from "./views/funds/FundsForm";
// import NetworkProvider from "../providers/NetworkProvider";

const WithdrawalsPage = () => {
  const [show, setShow] = useState(false);

  const handleShow = () => setShow(true);
  const handleClose = () => setShow(false);

  const onFormSubmit = (e) => {
    // e.preventDefault();
    handleClose();
  };

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
        <CCol xs="12" sm="6" lg="6">
          <CWidgetProgress
            inverse
            color="success"
            header="Approved Withdrawal Requests"
            text={toCurrency(approved, "USD").toString()}
            value={(approved / total) * 100}
          />
        </CCol>
        <CCol xs="12" sm="6" lg="6">
          <CWidgetProgress
            inverse
            color="warning"
            header="Pending Withdrawal Requests"
            text={toCurrency(pending, "USD").toString()}
            value={(pending / total) * 100}
          />
        </CCol>
      </CRow>
      <CRow>
        <CCol>
          <CCard>
            <CCardHeader>Available Funds</CCardHeader>
            <CCardBody>
              <AvailableFundsTable />
            </CCardBody>
          </CCard>
        </CCol>
      </CRow>
      <CRow>
        <CCol>
          <CCard>
            <CCardHeader>PendingWithdrawal Requests</CCardHeader>
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

      <CModal show={show} onClose={handleClose}>
        <CModalHeader closeButton>
          <CModalTitle>Withdrawal Request Form</CModalTitle>
        </CModalHeader>
        <CModalBody>
          {/* <NetworkProvider>
            <FundsForm onSubmit={onFormSubmit} />
          </NetworkProvider> */}
        </CModalBody>
      </CModal>
    </>
  );
};

export default WithdrawalsPage;

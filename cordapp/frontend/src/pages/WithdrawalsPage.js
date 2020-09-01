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
} from "@coreui/react";
import { AvailableFundsTable } from "./views/funds/AvailableFundsTable";
import { RequestsTable } from "./views//withdrawals/RequestsTable";
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

  return (
    <>
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
            <CCardHeader>Withdrawal Requests</CCardHeader>
            <CCardBody><RequestsTable status="PENDING"/></CCardBody>
          </CCard>
        </CCol>
        <CCol>
          <CCard>
            <CCardHeader>Approved Withdrawal Requests</CCardHeader>
            <CCardBody><RequestsTable status="APPROVED"/></CCardBody>
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

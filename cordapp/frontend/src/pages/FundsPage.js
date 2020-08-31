import React, { useState } from "react";
import {
  CCard,
  CCardBody,
  CCardHeader,
  CButton,
  CModal,
  CModalHeader,
  CModalBody,
  CModalTitle,
} from "@coreui/react";
import { FundsTable } from "./views/funds/FundsTable";
import { FundsForm } from "./views/funds/FundsForm";

const FundsPage = () => {
  const [show, setShow] = useState(false);

  const handleShow = () => setShow(true);
  const handleClose = () => setShow(false);

  const onFormSubmit = (e) => {
    e.preventDefault();
    handleClose();
  };

  return (
    <>
      <CCard>
        <CCardHeader>
          Funds
          <div className="card-header-actions">
            <CButton
              className={"float-right mb-0"}
              color={"primary"}
              tabIndex="0"
              onClick={handleShow}
            >
              Issue Funds
            </CButton>
          </div>
        </CCardHeader>
        <CCardBody>
          <FundsTable />
        </CCardBody>
      </CCard>
      <CModal show={show} onClose={handleClose}>
        <CModalHeader closeButton>
          <CModalTitle>Funds Form</CModalTitle>
        </CModalHeader>
        <CModalBody>
          <FundsForm onSubmit={onFormSubmit} />
        </CModalBody>
      </CModal>
    </>
  );
};

export default FundsPage;

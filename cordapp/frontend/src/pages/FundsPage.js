import React, { useState, useContext } from "react";
import {
  CCard,
  CCardBody,
  CCardHeader,
  CButton,
  CModal,
  CModalHeader,
  CModalBody,
  CModalTitle
} from "@coreui/react";
import { FundsTable } from "./views/funds/FundsTable";
import { FundsForm } from "./views/funds/FundsForm";
import NetworkProvider from "../providers/NetworkProvider";
import ReactNotification from "react-notifications-component";
import UseToaster from "../notification/Toaster";
import { FundsContext } from "../providers/FundsProvider";
import EllipsesText from 'react-ellipsis-text';


const FundsPage = () => {
  const [fundsState, fundsCallback] = useContext(FundsContext);

  const [show, setShow] = useState(false);
  const handleShow = () => setShow(true);
  const handleClose = () => setShow(false);

  const responseMessage = (message) => {
    return (
      <div>
        {message.entity.message}
        <br />
        <strong>State ID:</strong> <EllipsesText text={message.entity.data.linearId.id} length={25}/>
        <br />
        <strong>Status:</strong> {message.entity.data.status}
      </div>
    );
  };

  const onFormSubmit = (response) => {
    handleClose();
    response.status === 200
      ? UseToaster("Success", responseMessage(response), "success")
      : UseToaster("Error", response.entity.message, "danger");

    fundsCallback();
  };

  return (
    <>
      <ReactNotification />
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
          <FundsTable funds={fundsState} />
        </CCardBody>
      </CCard>
      <CModal show={show} onClose={handleClose}>
        <CModalHeader closeButton>
          <CModalTitle>Funds Form</CModalTitle>
        </CModalHeader>
        <CModalBody>
          <NetworkProvider>
            <FundsForm onSubmit={onFormSubmit} />
          </NetworkProvider>
        </CModalBody>
      </CModal>
    </>
  );
};

export default FundsPage;

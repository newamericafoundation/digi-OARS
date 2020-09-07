import React, { useState, useContext, useEffect } from "react";
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
import NetworkProvider from "../providers/NetworkProvider";
import ReactNotification from "react-notifications-component";
import UseToaster from "../notification/Toaster";
import { FundsContext } from "../providers/FundsProvider";
import EllipsesText from "react-ellipsis-text";
import { useAuth } from "../auth-hook";
import axios from "axios";

const FundsPage = () => {
  const auth = useAuth();
  const [fundsState, fundsCallback] = useContext(FundsContext);
  const [isFundsIssuer, setIsFundsIssuer] = useState(false);
  const [isFundsReceiver, setIsFundsReceiver] = useState(false);

  const [show, setShow] = useState(false);
  const handleShow = () => setShow(true);
  const handleClose = () => setShow(false);

  useEffect(() => {
    if (auth.isAuthenticated) {
      setIsFundsReceiver(auth.meta.keycloak.hasResourceRole("funds_receiver"));
      setIsFundsIssuer(auth.meta.keycloak.hasResourceRole("funds_issuer"));
    }
  }, [auth]);

  const responseMessage = (message) => {
    return (
      <div>
        {message.entity.message}
        <br />
        <strong>State ID:</strong>{" "}
        <EllipsesText text={message.entity.data.linearId.id} length={25} />
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

  // const onHandleReceive = (fundId) => {
  //   const url =
  //     "http://" +
  //     window._env_.API_CLIENT_URL +
  //     ":" +
  //     window._env_.API_CLIENT_PORT +
  //     "/api/fund";

  //   axios
  //     .put(url, null, { params: { fundId } })
  //     .then((response) => {
  //       fundsCallback();
  //     })
  //     .catch((err) => console.log(err));
  // };

  return (
    <>
      <ReactNotification />
      <CCard>
        <CCardHeader>
          Funds
          {auth.isAuthenticated && isFundsIssuer ? (
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
          ) : null}
        </CCardHeader>
        <CCardBody>
          <FundsTable
            funds={fundsState}
            isReceiver={isFundsReceiver}
            refreshTableCallback={fundsCallback}
          />
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

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
  CRow,
  CWidgetProgressIcon,
  CCol,
  CCallout,
} from "@coreui/react";
import CIcon from "@coreui/icons-react";
import { FundsTable } from "./views/funds/FundsTable";
import { FundsForm } from "./views/funds/FundsForm";
import NetworkProvider from "../providers/NetworkProvider";
import UseToaster from "../notification/Toaster";
import { FundsContext } from "../providers/FundsProvider";
import EllipsesText from "react-ellipsis-text";
import { useAuth } from "../auth-hook";
import useInterval from "../interval-hook";
import * as Constants from "../constants";

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

  useInterval(() => {
    if (auth.isAuthenticated) {
      fundsCallback();
    }
  }, Constants.REFRESH_INTERVAL_MS);

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

  const toCurrency = (number, currency) => {
    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: currency,
    }).format(number);
  };

  return (
    <>
      <CRow>
        <CCol xs="12" sm="6" lg="4">
          <CWidgetProgressIcon
            inverse
            header={toCurrency(fundsState.paidAmount, "USD").toString()}
            text="Paid Funds"
            color="gradient-dark"
            value={
              (fundsState.paidAmount /
                (fundsState.issuedAmount +
                  fundsState.receivedAmount +
                  fundsState.paidAmount)) *
              100
            }
          >
            <CIcon name="cil-money" height="36" />
          </CWidgetProgressIcon>
        </CCol>
        <CCol xs="12" sm="6" lg="4">
          <CWidgetProgressIcon
            inverse
            header={toCurrency(fundsState.receivedAmount, "USD").toString()}
            text="Received Funds"
            color="gradient-success"
            value={
              (fundsState.receivedAmount /
                (fundsState.issuedAmount +
                  fundsState.receivedAmount +
                  fundsState.paidAmount)) *
              100
            }
          >
            <CIcon name="cil-check-circle" height="36" />
          </CWidgetProgressIcon>
        </CCol>
        <CCol xs="12" sm="6" lg="4">
          <CWidgetProgressIcon
            inverse
            header={toCurrency(fundsState.issuedAmount, "USD").toString()}
            text="Issued Funds"
            color="gradient-warning"
            value={
              (fundsState.issuedAmount /
                (fundsState.issuedAmount +
                  fundsState.receivedAmount +
                  fundsState.paidAmount)) *
              100
            }
          >
            <CIcon name="cil-av-timer" height="36" />
          </CWidgetProgressIcon>
        </CCol>
      </CRow>
      <CRow>
        <CCol>
          <CCard>
            <CCardHeader>
              <div className="mb-0">
                <CCallout className="float-left mt-1 mb-1">
                  <h4 className="mt-1">Funds Issued</h4>
                </CCallout>
                {auth.isAuthenticated && isFundsIssuer ? (
                  <CButton
                    className={"float-right"}
                    color={"primary"}
                    tabIndex="0"
                    onClick={handleShow}
                  >
                    Issue Funds
                  </CButton>
                ) : null}
              </div>
            </CCardHeader>
            <CCardBody>
              <FundsTable
                funds={fundsState.issued}
                isReceiver={isFundsReceiver}
                refreshTableCallback={fundsCallback}
              />
            </CCardBody>
          </CCard>
        </CCol>
      </CRow>
      <CRow>
        <CCol>
          <CCard>
            <CCardHeader>
              <div className="mb-0">
                <CCallout className="float-left mt-1 mb-1">
                  <h4 className="mt-1">Funds Received</h4>
                </CCallout>
              </div>
            </CCardHeader>
            <CCardBody>
              <FundsTable
                funds={fundsState.received}
                isReceiver={isFundsReceiver}
                refreshTableCallback={fundsCallback}
              />
            </CCardBody>
          </CCard>
        </CCol>
        <CCol>
          <CCard>
            <CCardHeader>
              <div className="mb-0">
                <CCallout className="float-left mt-1 mb-1">
                  <h4 className="mt-1">Funds Paid</h4>
                </CCallout>
              </div>
            </CCardHeader>
            <CCardBody>
              <FundsTable
                funds={fundsState.paid}
                isReceiver={isFundsReceiver}
                refreshTableCallback={fundsCallback}
              />
            </CCardBody>
          </CCard>
        </CCol>
      </CRow>

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

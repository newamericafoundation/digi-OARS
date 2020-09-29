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
  CButtonGroup,
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
  const [fundsFilterStatus, setFundsFilterStatus] = useState("ISSUED");

  const [show, setShow] = useState(false);
  const handleShow = () => setShow(true);
  const handleClose = () => setShow(false);

  const handleTableFilter = (filterValue) => {
    setFundsFilterStatus(filterValue);
  };

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
    if (response.status === 200) {
      UseToaster("Success", responseMessage(response), "success");
      fundsCallback();
    } else {
      UseToaster("Error", response.entity.message, "danger");
    }
  };

  const toCurrency = (number, currency) => {
    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: currency,
    }).format(number);
  };

  const getCalloutColor = () => {
    switch (fundsFilterStatus) {
      case "ALL":
        return "secondary";
      case "ISSUED":
        return "warning";
      case "RECEIVED":
        return "success";
      case "PAID":
        return "dark";
      default:
        return "warning";
    }
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
                <CCallout
                  className="float-left mt-1 mb-1"
                  color={getCalloutColor()}
                >
                  <h4 className="mt-1 text-dark">
                    {fundsFilterStatus.charAt(0).toUpperCase() +
                      fundsFilterStatus.slice(1).toLowerCase()}{" "}
                    Funds
                  </h4>
                </CCallout>
                {auth.isAuthenticated && isFundsIssuer ? (
                  <CButton
                    className="float-right mt-1 mb-1"
                    color={"primary"}
                    tabIndex="0"
                    onClick={handleShow}
                  >
                    Issue Funds
                  </CButton>
                ) : null}
                <CButtonGroup className="float-right mr-3 mt-1 mb-1">
                  <CButton
                    color="outline-dark"
                    className="mx-0"
                    key="All"
                    active={"ALL" === fundsFilterStatus}
                    onClick={() => handleTableFilter("ALL")}
                  >
                    All
                  </CButton>
                  <CButton
                    color="outline-dark"
                    className="mx-0"
                    key="Issued"
                    active={"ISSUED" === fundsFilterStatus}
                    onClick={() => handleTableFilter("ISSUED")}
                  >
                    Issued
                  </CButton>
                  <CButton
                    color="outline-dark"
                    className="mx-0"
                    key="Received"
                    active={"RECEIVED" === fundsFilterStatus}
                    onClick={() => handleTableFilter("RECEIVED")}
                  >
                    Received
                  </CButton>
                  <CButton
                    color="outline-dark"
                    className="mx-0"
                    key="Paid"
                    active={"PAID" === fundsFilterStatus}
                    onClick={() => handleTableFilter("PAID")}
                  >
                    Paid
                  </CButton>
                </CButtonGroup>
              </div>
            </CCardHeader>
            <CCardBody>
              <FundsTable
                funds={fundsState.data.filter(
                  (fund) => fund.status === fundsFilterStatus
                )}
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

import React, { useState, useContext, useEffect } from "react";
import {
  CCard,
  CCardBody,
  CCardHeader,
  CButton,
  CRow,
  CCol,
  CCallout,
  CButtonGroup,
  CBadge,
} from "@coreui/react";
import { FundsTable } from "./views/funds/FundsTable";
import { FundsContext } from "../providers/FundsProvider";
import { useAuth } from "../auth-hook";
import useInterval from "../interval-hook";
import * as Constants from "../constants";
import { CreateFundButton } from "../buttons/funds";

const FundsPage = () => {
  const auth = useAuth();
  const [fundsState, fundsCallback] = useContext(FundsContext);
  const [isFundsIssuer, setIsFundsIssuer] = useState(false);
  const [isFundsReceiver, setIsFundsReceiver] = useState(false);
  const [fundsFilterStatus, setFundsFilterStatus] = useState("ALL");

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
      {/* <CRow>
        <CCol xs="12" sm="6" lg="4">
          <CWidgetProgressIcon
            inverse
            header={toCurrency(fundsState.issuedAmount, "USD").toString()}
            text="Returns Issued"
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
        <CCol xs="12" sm="6" lg="4">
          <CWidgetProgressIcon
            inverse
            header={toCurrency(fundsState.receivedAmount, "USD").toString()}
            text="Returns Received"
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
            header={toCurrency(fundsState.paidAmount, "USD").toString()}
            text="Returns Paid"
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
      </CRow> */}
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
                    Returns
                  </h4>
                </CCallout>
                {auth.isAuthenticated && isFundsIssuer ? (
                  <CButtonGroup className="float-right mt-2 mb-1">
                    <CreateFundButton size="sm" />
                  </CButtonGroup>
                ) : null}
                <CButtonGroup className="float-right mr-3 mt-1 mb-1">
                  <CButton
                    color="outline-dark"
                    className="mx-0"
                    key="All"
                    active={"ALL" === fundsFilterStatus}
                    onClick={() => handleTableFilter("ALL")}
                  >
                    All{" "}
                    <CBadge color="secondary" shape="pill">
                      {fundsState.data.length}
                    </CBadge>
                  </CButton>
                  <CButton
                    color="outline-dark"
                    className="mx-0"
                    key="Issued"
                    active={"ISSUED" === fundsFilterStatus}
                    onClick={() => handleTableFilter("ISSUED")}
                  >
                    Issued{" "}
                    <CBadge color="warning" shape="pill">
                      {fundsState.issued.length}
                    </CBadge>
                  </CButton>
                  <CButton
                    color="outline-dark"
                    className="mx-0"
                    key="Received"
                    active={"RECEIVED" === fundsFilterStatus}
                    onClick={() => handleTableFilter("RECEIVED")}
                  >
                    Received{" "}
                    <CBadge color="success" shape="pill">
                      {fundsState.received.length}
                    </CBadge>
                  </CButton>
                  <CButton
                    color="outline-dark"
                    className="mx-0"
                    key="Paid"
                    active={"PAID" === fundsFilterStatus}
                    onClick={() => handleTableFilter("PAID")}
                  >
                    Paid{" "}
                    <CBadge color="dark" shape="pill">
                      {fundsState.paid.length}
                    </CBadge>
                  </CButton>
                </CButtonGroup>
              </div>
            </CCardHeader>
            <CCardBody>
              <FundsTable
                funds={
                  fundsFilterStatus === "ALL"
                    ? fundsState.data
                    : fundsState.data.filter(
                        (fund) => fund.status === fundsFilterStatus
                      )
                }
                isReceiver={isFundsReceiver}
                refreshTableCallback={fundsCallback}
              />
            </CCardBody>
          </CCard>
        </CCol>
      </CRow>
    </>
  );
};

export default FundsPage;

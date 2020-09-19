import React, { useContext } from "react";
import { CCol, CRow, CWidgetBrand } from "@coreui/react";
import CIcon from "@coreui/icons-react";
import { FundsContext } from "../../../providers/FundsProvider";
import { toCurrency } from "../../../utilities";
import { RequestsContext } from "../../../providers/RequestsProvider";
import { addAmounts } from "../../../utilities";

export const Widgets = ({ auth }) => {
  const [fundsState] = useContext(FundsContext);
  const [requestsState] = useContext(RequestsContext);

  const getRequestorData = (data) => {
    return data.filter(
      (request) =>
        request.authorizedUserDept === auth.meta.keycloak.tokenParsed.groups[0]
    );
  };

  return (
    <>
      {auth.isAuthenticated && (
        <CRow>
          <CCol xs="12" sm="4" lg="4">
            <CWidgetBrand
              color="gradient-dark"
              bodySlot={
                <div className="text-center">
                  <h6 className="text-uppercase text-muted mt-2 mb-2">Funds</h6>
                  {!auth.meta.keycloak.hasResourceRole("funds_requestor") && (
                    <CRow className="mb-3">
                      <CCol>
                        <div className="text-value-lg mb-2">
                          {fundsState.received.length.toString()}
                        </div>
                        <div className="text-uppercase text-muted small">
                          Received
                        </div>
                        <div className="text-value-md mt-2 mb-0">
                          {toCurrency(fundsState.receivedAmount, "USD")}
                        </div>
                      </CCol>
                      <div className="c-vr"></div>
                      <CCol>
                        <div className="text-value-lg mb-2">
                          {fundsState.issued.length.toString()}
                        </div>
                        <div className="text-uppercase text-muted small">
                          Issued
                        </div>
                        <div className="text-value-md mt-2 mb-0">
                          {toCurrency(fundsState.issuedAmount, "USD")}
                        </div>
                      </CCol>
                    </CRow>
                  )}
                  {auth.meta.keycloak.hasResourceRole("funds_requestor") && (
                    <CRow className="mb-3">
                      <CCol>
                        <div className="text-value-lg mb-2">
                          {fundsState.received.length.toString()}
                        </div>
                        <div className="text-uppercase text-muted small">
                          Total Available
                        </div>
                        <div className="text-value-md mt-2 mb-0">
                          {toCurrency(fundsState.receivedAmount, "USD")}
                        </div>
                      </CCol>
                    </CRow>
                  )}
                </div>
              }
            >
              <CIcon name="cil-bank" height="56" className="my-4" />
            </CWidgetBrand>
          </CCol>
          <CCol xs="12" sm="4" lg="4">
            <CWidgetBrand
              color="gradient-dark"
              bodySlot={
                <div className="text-center">
                  <h6 className="text-uppercase text-muted mt-2 mb-2">
                    {!auth.meta.keycloak.hasResourceRole("funds_requestor")
                      ? "Withdrawals"
                      : "Department Withdrawals"}
                  </h6>
                  {!auth.meta.keycloak.hasResourceRole("funds_requestor") && (
                    <CRow className="mb-3">
                      <CCol>
                        <div className="text-value-lg mb-2">
                          {requestsState.approved.length.toString()}
                        </div>
                        <div className="text-uppercase text-muted small">
                          Approved
                        </div>
                        <div className="text-value-md mt-2 mb-0">
                          {toCurrency(requestsState.approvedAmount, "USD")}
                        </div>
                      </CCol>
                      <div className="c-vr"></div>
                      <CCol>
                        <div className="text-value-lg mb-2">
                          {requestsState.pending.length.toString()}
                        </div>
                        <div className="text-uppercase text-muted small">
                          Pending
                        </div>
                        <div className="text-value-md mt-2 mb-0">
                          {toCurrency(requestsState.pendingAmount, "USD")}
                        </div>
                      </CCol>
                    </CRow>
                  )}
                  {auth.meta.keycloak.hasResourceRole("funds_requestor") && (
                    <CRow className="mb-3">
                      <CCol>
                        <div className="text-value-lg mb-2">
                          {getRequestorData(
                            requestsState.approved
                          ).length.toString()}
                          {/* {requestsState.approved.length.toString()} */}
                        </div>
                        <div className="text-uppercase text-muted small">
                          Approved
                        </div>
                        <div className="text-value-md mt-2 mb-0">
                          {toCurrency(
                            addAmounts(
                              getRequestorData(requestsState.approved)
                            ),
                            "USD"
                          )}
                        </div>
                      </CCol>
                      <div className="c-vr"></div>
                      <CCol>
                        <div className="text-value-lg mb-2">
                          {getRequestorData(
                            requestsState.pending
                          ).length.toString()}
                          {/* {requestsState.pending.length.toString()} */}
                        </div>
                        <div className="text-uppercase text-muted small">
                          Pending
                        </div>
                        <div className="text-value-md mt-2 mb-0">
                          {toCurrency(
                            addAmounts(getRequestorData(requestsState.pending)),
                            "USD"
                          )}
                        </div>
                      </CCol>
                    </CRow>
                  )}
                </div>
              }
            >
              <CIcon name="cil-wallet" height="56" className="my-4" />
            </CWidgetBrand>
          </CCol>
        </CRow>
      )}
    </>
  );
};

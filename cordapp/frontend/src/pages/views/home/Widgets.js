import React, { useContext } from "react";
import { CCol, CRow, CWidgetBrand, CSpinner } from "@coreui/react";
import CIcon from "@coreui/icons-react";
import { FundsContext } from "../../../providers/FundsProvider";
import { toCurrency } from "../../../utilities";
import { RequestsContext } from "../../../providers/RequestsProvider";
import { addAmounts } from "../../../utilities";
import { TransfersContext } from "../../../providers/TransfersProvider";
import { PartialRequestsContext } from "../../../providers/PartialRequestsProvider";
import useInterval from "../../../interval-hook";
import * as Constants from "../../../constants";

export const Widgets = ({ auth }) => {
  const [fundsState, fundsCallback] = useContext(FundsContext);
  const [requestsState, requestsCallback] = useContext(RequestsContext);
  const [partialRequestsState, partialRequestsCallback] = useContext(
    PartialRequestsContext
  );
  const [transfersState, transfersCallback] = useContext(TransfersContext);

  const getRequestorData = (data) => {
    return data.filter(
      (request) =>
        request.authorizedUserDept === auth.meta.keycloak.tokenParsed.groups[0]
    );
  };

  useInterval(() => {
    if (auth.isAuthenticated) {
      fundsCallback();
      requestsCallback();
      partialRequestsCallback();
      transfersCallback();
    }
  }, Constants.REFRESH_INTERVAL_MS);

  const getSpinner = () => {
    return (
      <div className="d-flex justify-content-center">
        <CSpinner
          className="spinner-border spinner-border-sm text-dark"
          role="status"
          aria-hidden="true"
        />
      </div>
    );
  };

  return (
    <>
      {auth.isAuthenticated && (
        <CRow>
          {!auth.meta.keycloak.hasResourceRole("partial_request_viewer") && (
            <CCol xs="12" sm="4">
              <CWidgetBrand
                color="gradient-dark"
                bodySlot={
                  <div className="text-center">
                    <h6 className="text-uppercase text-muted mt-2 mb-2">
                      Funds
                    </h6>
                    {!auth.meta.keycloak.hasResourceRole("funds_requestor") && (
                      <CRow className="mb-3">
                        <CCol>
                          <div className="text-value-lg mb-2">
                            {fundsState.loading
                              ? getSpinner()
                              : fundsState.received.length.toString()}
                          </div>
                          <div className="text-uppercase text-muted small">
                            Received
                          </div>
                          <div className="text-value-md mt-2 mb-0">
                            {fundsState.loading
                              ? getSpinner()
                              : toCurrency(fundsState.receivedAmount, "USD")}
                          </div>
                        </CCol>
                        <div className="c-vr"></div>
                        <CCol>
                          <div className="text-value-lg mb-2">
                            {fundsState.loading
                              ? getSpinner()
                              : fundsState.issued.length.toString()}
                          </div>
                          <div className="text-uppercase text-muted small">
                            Issued
                          </div>
                          <div className="text-value-md mt-2 mb-0">
                            {fundsState.loading
                              ? getSpinner()
                              : toCurrency(fundsState.issuedAmount, "USD")}
                          </div>
                        </CCol>
                      </CRow>
                    )}
                    {auth.meta.keycloak.hasResourceRole("funds_requestor") && (
                      <CRow className="mb-3">
                        <CCol>
                          <div className="text-value-lg mb-2">
                            {fundsState.loading
                              ? getSpinner()
                              : fundsState.received.length.toString()}
                          </div>
                          <div className="text-uppercase text-muted small">
                            Total Available
                          </div>
                          <div className="text-value-md mt-2 mb-0">
                            {fundsState.loading
                              ? getSpinner()
                              : toCurrency(fundsState.receivedAmount, "USD")}
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
          )}
          <CCol xs="12" sm="4">
            <CWidgetBrand
              color="gradient-dark"
              bodySlot={
                <div className="text-center">
                  <h6 className="text-uppercase text-muted mt-2 mb-2">
                    {!auth.meta.keycloak.hasResourceRole("funds_requestor") &&
                    !auth.meta.keycloak.hasResourceRole(
                      "partial_request_viewer"
                    )
                      ? "Requests"
                      : "Department Requests"}
                  </h6>
                  {!auth.meta.keycloak.hasResourceRole("funds_requestor") &&
                    !auth.meta.keycloak.hasResourceRole(
                      "partial_request_viewer"
                    ) && (
                      <CRow className="mb-3">
                        <CCol>
                          <div className="text-value-lg mb-2">
                            {requestsState.loading
                              ? getSpinner()
                              : requestsState.approved.length.toString()}
                          </div>
                          <div className="text-uppercase text-muted small">
                            Approved
                          </div>
                          <div className="text-value-md mt-2 mb-0">
                            {requestsState.loading
                              ? getSpinner()
                              : toCurrency(requestsState.approvedAmount, "USD")}
                          </div>
                        </CCol>
                        <div className="c-vr"></div>
                        <CCol>
                          <div className="text-value-lg mb-2">
                            {requestsState.loading
                              ? getSpinner()
                              : requestsState.pending.length.toString()}
                          </div>
                          <div className="text-uppercase text-muted small">
                            Pending
                          </div>
                          <div className="text-value-md mt-2 mb-0">
                            {requestsState.loading
                              ? getSpinner()
                              : toCurrency(requestsState.pendingAmount, "USD")}
                          </div>
                        </CCol>
                      </CRow>
                    )}
                  {auth.meta.keycloak.hasResourceRole("funds_requestor") && (
                    <CRow className="mb-3">
                      <CCol>
                        <div className="text-value-lg mb-2">
                          {requestsState.loading
                            ? getSpinner()
                            : getRequestorData(
                                requestsState.approved
                              ).length.toString()}
                          {/* {requestsState.approved.length.toString()} */}
                        </div>
                        <div className="text-uppercase text-muted small">
                          Approved
                        </div>
                        <div className="text-value-md mt-2 mb-0">
                          {requestsState.loading
                            ? getSpinner()
                            : toCurrency(
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
                          {requestsState.loading
                            ? getSpinner()
                            : getRequestorData(
                                requestsState.pending
                              ).length.toString()}
                          {/* {requestsState.pending.length.toString()} */}
                        </div>
                        <div className="text-uppercase text-muted small">
                          Pending
                        </div>
                        <div className="text-value-md mt-2 mb-0">
                          {requestsState.loading
                            ? getSpinner()
                            : toCurrency(
                                addAmounts(
                                  getRequestorData(requestsState.pending)
                                ),
                                "USD"
                              )}
                        </div>
                      </CCol>
                    </CRow>
                  )}
                  {auth.meta.keycloak.hasResourceRole(
                    "partial_request_viewer"
                  ) && (
                    <CRow className="mb-3">
                      <CCol>
                        <div className="text-value-lg mb-2">
                          {partialRequestsState.loading
                            ? getSpinner()
                            : partialRequestsState.data.length.toString()}
                        </div>
                        <div className="text-uppercase text-muted small">
                          Total Available
                        </div>
                        <div className="text-value-md mt-2 mb-0">
                          {partialRequestsState.loading
                            ? getSpinner()
                            : toCurrency(
                                partialRequestsState.totalAmount,
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
          {!auth.meta.keycloak.hasResourceRole("partial_request_viewer") && (
            <CCol xs="12" sm="4">
              <CWidgetBrand
                color="gradient-dark"
                bodySlot={
                  <div className="text-center">
                    <h6 className="text-uppercase text-muted mt-2 mb-2">
                      Transfers
                    </h6>
                    <CRow className="mb-3">
                      <CCol>
                        <div className="text-value-lg mb-2">
                          {transfersState.loading
                            ? getSpinner()
                            : transfersState.data.length.toString()}
                        </div>
                        <div className="text-uppercase text-muted small">
                          Completed
                        </div>
                        <div className="text-value-md mt-2 mb-0">
                          {transfersState.loading
                            ? getSpinner()
                            : toCurrency(transfersState.amount, "USD")}
                        </div>
                      </CCol>
                    </CRow>
                  </div>
                }
              >
                <CIcon name="cil-chevron-right" height="56" className="my-4" />
              </CWidgetBrand>
            </CCol>
          )}
        </CRow>
      )}
    </>
  );
};

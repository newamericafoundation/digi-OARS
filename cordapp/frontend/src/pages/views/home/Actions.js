import React, { useContext } from "react";
import {
  CCol,
  CRow,
  CCallout,
  CCard,
  CCardHeader,
  CCardBody,
  CListGroup,
  CListGroupItem,
  CBadge,
  CLink,
  CButton, CAlert
} from "@coreui/react";
import { RequestsContext } from "../../../providers/RequestsProvider";
import { FundsContext } from "../../../providers/FundsProvider";
import CIcon from "@coreui/icons-react";
import { toCountryByIsoFromX500, toCurrency } from "../../../utilities";
import moment from "moment-timezone";
import * as Constants from "../../../constants";

export const Actions = ({ auth }) => {
  const [requestsState] = useContext(RequestsContext);
  const [fundsState] = useContext(FundsContext);

  return (
    <CRow>
      <CCol xl="8" xs="12">
        <CCard>
          <CCardHeader>
            <CCallout className="float-left mt-1 mb-1" color="secondary">
              <h4 className="mt-1 text-dark">Pending Actions</h4>
            </CCallout>
          </CCardHeader>
          <CCardBody>
            {auth.meta.keycloak.hasResourceRole("funds_receiver") ? (
              (requestsState.approved.length === 0 && fundsState.issued.length === 0) ? (
                <CAlert color="success">
                  No new pending actions!
                </CAlert>
              ) : (
              <CRow>
                <CCol xs="12" xl="6">
                  <h4>
                    Funds to Receive{" "}
                    <CBadge
                      shape="pill"
                      color="warning"
                      className="float-right"
                    >
                      {fundsState.issued ? fundsState.issued.length : null}
                    </CBadge>
                  </h4>
                  <CListGroup accent={true}>
                    {fundsState.issued
                      ? fundsState.issued.slice(0, 5).map((item) => (
                          <CListGroupItem key={item.linearId} accent="warning">
                            <div className="float-right">
                              <CBadge color="warning">{item.status}</CBadge>
                            </div>
                            <div>
                              <CIcon name="cil-bank" />{" "}
                              {toCurrency(item.amount, "USD")}
                            </div>
                            <small className="text-muted mr-3">
                              <CIcon name="cil-location-pin" />{" "}
                              {toCountryByIsoFromX500(item.originParty)}
                            </small>
                            <small className="text-muted mr-3">
                              <CIcon name="cil-av-timer" />{" "}
                              {moment
                                .tz(item.createdDateTime, "UTC")
                                .format(Constants.DATETIME_FORMAT)}
                            </small>
                          </CListGroupItem>
                        ))
                      : null}
                    <CListGroupItem className="font-weight-bold text-muted c-small text-right">
                      <CLink to="/funds">
                        <CButton color="secondary">
                          View{" "}
                          {fundsState.issued.length > 5
                            ? fundsState.issued.length - 5 + " more..."
                            : " all..."}
                        </CButton>
                      </CLink>
                    </CListGroupItem>
                  </CListGroup>
                </CCol>
                <CCol xs="12" xl="6">
                  <h4>
                    Requests to Transfer{" "}
                    <CBadge shape="pill" color="info" className="float-right">
                      {requestsState.approved
                        ? requestsState.approved.length
                        : null}
                    </CBadge>
                  </h4>
                  <CListGroup accent={true}>
                    {requestsState.approved
                      ? requestsState.approved.slice(0, 5).map((item) => (
                          <CListGroupItem key={item.linearId} accent="info">
                            <div className="float-right">
                              <CBadge color="success">{item.status}</CBadge>
                            </div>
                            <div>
                              <CIcon name="cil-chevron-right" />{" "}
                              {toCurrency(item.amount, "USD")}
                            </div>
                            <small className="text-muted mr-3">
                              <CIcon name="cil-user" />{" "}
                              {item.authorizedUserUsername} [
                              {item.authorizedUserDept}]
                            </small>
                            <small className="text-muted mr-3">
                              <CIcon name="cil-av-timer" />{" "}
                              {moment
                                .tz(item.createDateTime, "UTC")
                                .format(Constants.DATETIME_FORMAT)}
                            </small>
                          </CListGroupItem>
                        ))
                      : null}
                    <CListGroupItem className="font-weight-bold text-muted c-small text-right">
                      <CLink to="/transfers/approvals">
                        <CButton color="secondary">
                          View{" "}
                          {requestsState.approved.length > 5
                            ? requestsState.approved.length - 5 + " more..."
                            : " all..."}
                        </CButton>
                      </CLink>
                    </CListGroupItem>
                  </CListGroup>
                </CCol>
              </CRow>)
            ) : null}
            {auth.meta.keycloak.hasResourceRole("request_approver") ? (
              (requestsState.pending.length === 0) ? (
                <CAlert color="success">
                  No new pending actions!
                </CAlert>
              ) : (
              <CRow>
                <CCol xl="8" xs="12">
                <h4>
                    Requests to Approve/Reject{" "}
                    <CBadge
                      shape="pill"
                      color="warning"
                      className="float-right"
                    >
                      {requestsState.pending ? requestsState.pending.length : null}
                    </CBadge>
                  </h4>
                  <CListGroup accent={true}>
                    {requestsState.pending
                      ? requestsState.pending.slice(0, 5).map((item) => (
                          <CListGroupItem key={item.linearId} accent="warning">
                            <div className="float-right">
                              <CBadge color="warning">{item.status}</CBadge>
                            </div>
                            <div>
                              <CIcon name="cil-chevron-right" />{" "}
                              {toCurrency(item.amount, "USD")}
                            </div>
                            <small className="text-muted mr-3">
                              <CIcon name="cil-user" />{" "}
                              {item.authorizedUserUsername} [
                              {item.authorizedUserDept}]
                            </small>
                            <small className="text-muted mr-3">
                              <CIcon name="cil-av-timer" />{" "}
                              {moment
                                .tz(item.createDateTime, "UTC")
                                .format(Constants.DATETIME_FORMAT)}
                            </small>
                          </CListGroupItem>
                        ))
                      : null}
                    <CListGroupItem className="font-weight-bold text-muted c-small text-right">
                      <CLink to="/withdrawals">
                        <CButton color="secondary">
                          View{" "}
                          {requestsState.pending.length > 5
                            ? requestsState.pending.length - 5 + " more..."
                            : " all..."}
                        </CButton>
                      </CLink>
                    </CListGroupItem>
                  </CListGroup>
                </CCol>
              </CRow>)
            ) : null }
          </CCardBody>
        </CCard>
      </CCol>
    </CRow>
  );
};

import React, { useContext, useEffect, useState } from "react";
import { useSelector, useDispatch } from "react-redux";
import CIcon from "@coreui/icons-react";
import {
  CSidebar,
  CSidebarClose,
  CNavItem,
  CNav,
  CTabContent,
  CListGroup,
  CListGroupItem,
  CTabs,
  CNavLink,
  CTabPane,
  CBadge,
  CButton,
  CImg,
  CLink,
} from "@coreui/react";
import { RequestsContext } from "../providers/RequestsProvider";
import { FundsContext } from "../providers/FundsProvider";
import { useAuth } from "../auth-hook";
import axios from "axios";
import { APIContext } from "../providers/APIProvider";
import { toCountryByIsoFromX500, toCurrency } from "../utilities";

const SidebarRight = () => {
  const auth = useAuth();
  const [api] = useContext(APIContext);
  const [requestsState] = useContext(RequestsContext);
  const [fundsState] = useContext(FundsContext);
  const dispatch = useDispatch();
  const show = useSelector((state) => state.sidebarRightShow);
  const [nodeName, setNodeName] = useState("");
  const [nodeVersion, setNodeVersion] = useState("");

  useEffect(() => {
    if (api.port) {
      axios
        .get(
          "http://" + window._env_.API_CLIENT_URL + ":" + api.port + "/api/me"
        )
        .then((response) => setNodeName(response.data));
      axios
        .get(
          "http://" +
            window._env_.API_CLIENT_URL +
            ":" +
            api.port +
            "/api/version"
        )
        .then((response) => setNodeVersion(response.data));
    }
  }, [api.port]);

  return (
    <CSidebar
      id="aside"
      className="c-sidebar-right"
      show={show}
      onShowChange={(val) => dispatch({ type: "set", sidebarRightShow: val })}
      overlaid={true}
      size="lg"
      colorScheme="light"
    >
      <CSidebarClose
        onClick={() => dispatch({ type: "set", sidebarRightShow: false })}
      />
      <CTabs
        className="nav"
        activeTab={
          (auth.meta.keycloak.hasResourceRole("funds_receiver") || auth.meta.keycloak.hasResourceRole("request_approver"))
            ? "notifications"
            : "user"
        }
      >
        <CNav variant="tabs" className="nav-underline nav-underline-primary">
          {(auth.meta.keycloak.hasResourceRole("funds_receiver") || auth.meta.keycloak.hasResourceRole("request_approver")) ? (
            <CNavItem>
              <CNavLink data-tab="notifications">
                <CIcon name="cil-bell" />
              </CNavLink>
            </CNavItem>
          ) : null}
          <CNavItem>
            <CNavLink data-tab="user">
              <CIcon name="cil-user" />
            </CNavLink>
          </CNavItem>
        </CNav>
        <CTabContent className="c-sidebar-nav">
        {auth.meta.keycloak.hasResourceRole("request_approver") ? (
          <CTabPane data-tab="notifications">
          {requestsState.pending.length > 0 ? (
            <CListGroup accent={true}>
              <CListGroupItem
                accent="secondary"
                className="bg-light font-weight-bold text-muted text-uppercase c-small"
              >
                Requests To Approve/Reject{" "}
                <CBadge shape="pill" color="warning" className="float-right">
                  {requestsState.pending
                    ? requestsState.pending.length
                    : null}
                </CBadge>
              </CListGroupItem>
              {requestsState.pending
                ? requestsState.pending.slice(0, 5).map((item) => (
                    <CListGroupItem key={item.linearId} accent="warning">
                      <div className="float-right">
                        <CBadge color="warning">{item.status}</CBadge>
                      </div>
                      <div>{toCurrency(item.amount, "USD")}</div>
                      <small className="text-muted mr-3">
                        <CIcon name="cil-user" /> {item.authorizedUserUsername} [{item.authorizedUserDept}]
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
          ) : null}
          {requestsState.pending.length === 0 ? (
            <CListGroup accent={true}>
              <CListGroupItem accent="secondary">
                No new notifications... you're all set, grab a coffee!{" "}
                <CIcon
                  name="cil-coffee"
                  className="mr-2 text-dark mfs-auto"
                />
              </CListGroupItem>
            </CListGroup>
          ) : null}
        </CTabPane>
        ) : null }
          {auth.meta.keycloak.hasResourceRole("funds_receiver") ? (
            <CTabPane data-tab="notifications">
              {requestsState.approved.length > 0 ? (
                <CListGroup accent={true}>
                  <CListGroupItem
                    accent="secondary"
                    className="bg-light font-weight-bold text-muted text-uppercase c-small"
                  >
                    Requests To Transfer{" "}
                    <CBadge shape="pill" color="info" className="float-right">
                      {requestsState.approved
                        ? requestsState.approved.length
                        : null}
                    </CBadge>
                  </CListGroupItem>
                  {requestsState.approved
                    ? requestsState.approved.slice(0, 5).map((item) => (
                        <CListGroupItem key={item.linearId} accent="info">
                          <div className="float-right">
                            <CBadge color="success">{item.status}</CBadge>
                          </div>
                          <div>{toCurrency(item.amount, "USD")}</div>
                          <small className="text-muted mr-3">
                            <CIcon name="cil-user" /> {item.authorizedUserUsername} [{item.authorizedUserDept}]
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
              ) : null}
              {fundsState.issued.length > 0 ? (
                <CListGroup accent={true}>
                  <CListGroupItem
                    accent="secondary"
                    className="bg-light font-weight-bold text-muted text-uppercase c-small"
                  >
                    Funds to Receive{" "}
                    <CBadge
                      shape="pill"
                      color="warning"
                      className="float-right"
                    >
                      {fundsState.issued ? fundsState.issued.length : null}
                    </CBadge>
                  </CListGroupItem>
                  {fundsState.issued
                    ? fundsState.issued.slice(0, 5).map((item) => (
                        <CListGroupItem key={item.linearId} accent="warning">
                          <div className="float-right">
                            <CBadge color="warning">{item.status}</CBadge>
                          </div>
                          <div>{toCurrency(item.amount, "USD")}</div>
                          <small className="text-muted mr-3">
                            <CIcon name="cil-location-pin" />{" "}
                            {toCountryByIsoFromX500(item.originParty)}
                          </small>
                        </CListGroupItem>
                      ))
                    : null}
                  <CListGroupItem className="font-weight-bold text-muted c-small text-right">
                    <CLink to="/returns">
                      <CButton color="secondary">
                        View{" "}
                        {fundsState.issued.length > 5
                          ? fundsState.issued.length - 5 + " more..."
                          : " all..."}
                      </CButton>
                    </CLink>
                  </CListGroupItem>
                </CListGroup>
              ) : null}

              {requestsState.approved.length === 0 &&
              fundsState.issued.length === 0 ? (
                <CListGroup accent={true}>
                  <CListGroupItem accent="secondary">
                    No new notifications... you're all set, grab a coffee!{" "}
                    <CIcon
                      name="cil-coffee"
                      className="mr-2 text-dark mfs-auto"
                    />
                  </CListGroupItem>
                </CListGroup>
              ) : null}
            </CTabPane>
          ) : null}
          <CTabPane data-tab="user">
            <CListGroup accent={true}>
              {auth.meta.keycloak.token && auth.user.attributes ? (
                <CListGroupItem accent="secondary">
                  <div className="text-center">
                    <CImg
                      src={"avatars/" + auth.user.attributes.avatar[0]}
                      shape="rounded-circle"
                      height={200}
                      width={200}
                      alt={auth.user.email}
                    />
                  </div>
                  <div className="text-center mt-4">
                    <h3 className="text-uppercase">{auth.user.fullName}</h3>
                    <p>{auth.meta.keycloak.tokenParsed.groups[0]}</p>
                  </div>
                  <div>
                    <h5>Assigned Roles:</h5>
                    <CListGroup>
                      {auth.meta.keycloak.resourceAccess[
                        "oars-client"
                      ].roles.map((role) => (
                        <CListGroupItem key={role}>{role}</CListGroupItem>
                      ))}
                    </CListGroup>
                  </div>
                  <div>
                    <h5 className="mt-4">
                      <CIcon name="cordaLogo" className="mr-2" />
                      Node Information:
                    </h5>
                    <dl className="row">
                      <dt className="col-sm-3">X500</dt>
                      <dd className="col-sm-9">{nodeName}</dd>
                      <dt className="col-sm-3">Version</dt>
                      <dd className="col-sm-9">{nodeVersion}</dd>
                    </dl>
                  </div>
                </CListGroupItem>
              ) : null}
            </CListGroup>
          </CTabPane>
        </CTabContent>
      </CTabs>
    </CSidebar>
  );
};

export default SidebarRight;

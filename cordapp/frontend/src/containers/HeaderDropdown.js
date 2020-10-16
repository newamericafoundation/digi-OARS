import React, { useContext, useEffect, useState } from "react";
import {
  CDropdown,
  CDropdownItem,
  CDropdownMenu,
  CDropdownToggle,
  CImg,
  CBadge,
} from "@coreui/react";
import CIcon from "@coreui/icons-react";
import { useAuth } from "../auth-hook";
import { FundsContext } from "../providers/FundsProvider";
import { RequestsContext } from "../providers/RequestsProvider";

const HeaderDropdown = () => {
  const auth = useAuth();
  const [fundsState] = useContext(FundsContext);
  const [requestsState] = useContext(RequestsContext);

  const [isFundsReceiver, setIsFundsReceiver] = useState(false);
  const [isRequestApprover, setIsRequestApprover] = useState(false);

  const [totalNotificationsCount, setTotalNotificationsCount] = useState(0);

  useEffect(() => {
    if (auth.isAuthenticated) {
      setIsFundsReceiver(auth.meta.keycloak.hasResourceRole("funds_receiver"));
      setIsRequestApprover(
        auth.meta.keycloak.hasResourceRole("request_approver")
      );
    }
  }, [auth]);

  useEffect(() => {
    if (isFundsReceiver) {
      setTotalNotificationsCount(
        fundsState.issued.length + requestsState.approved.length
      );
    }
    if (isRequestApprover) {
      setTotalNotificationsCount(
        requestsState.pending.length + requestsState.flagged.length
      );
    }
  }, [
    fundsState.issued.length,
    isFundsReceiver,
    isRequestApprover,
    requestsState.approved.length,
    requestsState.flagged.length,
    requestsState.pending.length,
  ]);

  function DropdownItems({ isAuthenticated }) {
    if (isAuthenticated) {
      return (
        <React.Fragment>
          <CDropdownItem>
            <CIcon name="cil-user" className="mfe-2" />
            {auth.user.fullName}
          </CDropdownItem>
          <CDropdownItem onClick={() => auth.logout()}>
            <CIcon name="cil-exit-to-app" className="mfe-2" />
            Logout
          </CDropdownItem>
        </React.Fragment>
      );
    }
    return (
      <CDropdownItem onClick={() => auth.login()}>
        <CIcon name="cil-lock-locked" className="mfe-2" />
        Login
      </CDropdownItem>
    );
  }

  return (
    <React.Fragment>
      {auth.isAuthenticated && (isFundsReceiver || isRequestApprover) ? (
        <CDropdown inNav className="c-header-nav-item mx-2 mr-4">
          <CDropdownToggle className="c-header-nav-link" caret={false}>
            <CIcon name="cil-bell" />
            {totalNotificationsCount > 0 ? (
              <CBadge shape="pill" color="danger">
                {totalNotificationsCount}
              </CBadge>
            ) : null}
          </CDropdownToggle>
          <CDropdownMenu placement="bottom-end" className="pt-0">
            <CDropdownItem
              header
              tag="div"
              className="text-center"
              color="light"
            >
              {totalNotificationsCount > 0 ? (
                <strong>
                  You have {totalNotificationsCount} new notifications.
                </strong>
              ) : (
                <strong>
                  You're all set, grab a coffee!
                  <CIcon
                    name="cil-coffee"
                    className="mr-2 text-dark mfs-auto"
                  />
                </strong>
              )}
            </CDropdownItem>
            {isFundsReceiver ? (
              <CDropdownItem to="/funds">
                <CIcon
                  name="cil-arrow-circle-right"
                  className="mr-2 text-dark float-left"
                />
                View Funds
                {fundsState.issued.length > 0 ? (
                  <CBadge shape="pill" color="warning" className="mfs-auto">
                    {fundsState.issued.length}
                  </CBadge>
                ) : null}
              </CDropdownItem>
            ) : null}
            {isFundsReceiver || isRequestApprover ? (
              <CDropdownItem to="/requests">
                <CIcon
                  name="cil-arrow-circle-right"
                  className="mr-2 text-dark float-left"
                />
                View Requests
                {isFundsReceiver && requestsState.approved.length > 0 ? (
                  <CBadge shape="pill" color="danger" className="mfs-auto">
                    {requestsState.approved.length}
                  </CBadge>
                ) : null}
                {isRequestApprover ? (
                  <div className="mfs-auto">
                    {requestsState.pending.length > 0 ? (
                      <CBadge shape="pill" color="warning">
                        {requestsState.pending.length}
                      </CBadge>
                    ) : null}
                    {requestsState.flagged.length > 0 ? (
                      <CBadge shape="pill" color="info" className="ml-2">
                        {requestsState.flagged.length}
                      </CBadge>
                    ) : null}
                  </div>
                ) : null}
              </CDropdownItem>
            ) : null}
          </CDropdownMenu>
        </CDropdown>
      ) : null}
      <CDropdown inNav className="c-header-nav-items mr-3">
        {auth.user.attributes ? (
          <CIcon name={"cif" + auth.user.attributes.country[0]} height={20} />
        ) : null}
      </CDropdown>
      <CDropdown inNav className="c-header-nav-items">
        <strong>{auth.user.fullName}</strong>
        <br />
        {auth.meta.keycloak.token
          ? auth.meta.keycloak.tokenParsed.groups[0]
          : null}
      </CDropdown>
      <CDropdown inNav className="c-header-nav-items" direction="down">
        <CDropdownToggle className="c-header-nav-link" caret={true}>
          {auth.isAuthenticated && (
            <div>
              <div className="c-avatar">
                {auth.user.attributes ? (
                  <CImg
                    src={"avatars/" + auth.user.attributes.avatar[0]}
                    className="c-avatar-img"
                    alt={auth.user.email}
                  />
                ) : null}
              </div>
            </div>
          )}
          {!auth.isAuthenticated && (
            <div className="c-avatar">
              <CIcon name="cil-user" />
            </div>
          )}
        </CDropdownToggle>
        <CDropdownMenu className="pt-0" placement="bottom-end">
          <CDropdownItem header tag="div" color="light" className="text-center">
            <strong>Account</strong>
          </CDropdownItem>
          <DropdownItems isAuthenticated={auth.isAuthenticated} />
        </CDropdownMenu>
      </CDropdown>
    </React.Fragment>
  );
};

export default HeaderDropdown;

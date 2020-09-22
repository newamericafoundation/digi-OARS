import React from "react";
import { useSelector, useDispatch } from "react-redux";
import CIcon from "@coreui/icons-react";
import {
  CCreateElement,
  CSidebar,
  CSidebarBrand,
  CSidebarNav,
  CSidebarNavDivider,
  CSidebarNavTitle,
  CSidebarMinimizer,
  CSidebarNavDropdown,
  CSidebarNavItem,
} from "@coreui/react";
import { useAuth } from "../auth-hook";

import nav from "./_nav";
import nav_secure from "./_nav_secure";
import nav_partial_request_viewer from "./_nav_partial_request_viewer";
import nav_admin from "./_nav_admin";

const Sidebar = () => {
  const dispatch = useDispatch();
  const show = useSelector((state) => state.sidebarShow);
  const auth = useAuth();

  return (
    <CSidebar
      show={show}
      onShowChange={(val) => dispatch({ type: "set", sidebarShow: val })}
    >
      <CSidebarBrand className="d-md-down-none" to="/">
        <CIcon
          className="c-sidebar-brand-full"
          name="logo-negative"
          height={35}
        />
        <CIcon
          className="c-sidebar-brand-minimized"
          name="sygnet"
          height={35}
        />
      </CSidebarBrand>
      <CSidebarNav>
        {/* Publicly visible navigation */}
        <CCreateElement
          items={nav}
          components={{
            CSidebarNavDivider,
            CSidebarNavDropdown,
            CSidebarNavItem,
            CSidebarNavTitle,
          }}
        />
        {/* Privately visible navigation */}
        {auth.meta.keycloak.hasResourceRole("user") && (
          <CCreateElement
            items={nav_secure}
            components={{
              CSidebarNavDivider,
              CSidebarNavDropdown,
              CSidebarNavItem,
              CSidebarNavTitle,
            }}
          />
        )}
        {auth.meta.keycloak.hasResourceRole("partial_request_viewer") && (
          <CCreateElement
            items={nav_partial_request_viewer}
            components={{
              CSidebarNavDivider,
              CSidebarNavDropdown,
              CSidebarNavItem,
              CSidebarNavTitle,
            }}
          />
        )}
        {auth.meta.keycloak.hasResourceRole("admin") && (
          <CCreateElement
            items={nav_admin}
            components={{
              CSidebarNavDivider,
              CSidebarNavDropdown,
              CSidebarNavItem,
              CSidebarNavTitle,
            }}
          />
        )}
      </CSidebarNav>
      <CSidebarMinimizer className="c-d-md-down-none" />
    </CSidebar>
  );
};

export default Sidebar;

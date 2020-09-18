import React, { useState, useEffect, useContext } from "react";
import {
  CButton,
  CCol,
  CRow,
  CJumbotron,
  CWidgetSimple,
} from "@coreui/react";
import CIcon from "@coreui/icons-react";
import { useAuth } from "../auth-hook";
import { FundsContext } from "providers/FundsProvider";

const HomePage = () => {
  const auth = useAuth();
  const [hour, setHour] = useState();
  const [greeting, setGreeting] = useState();
  const [fundsState] = useContext(FundsContext);

  useEffect(() => {
    const fetchGreeting = async () => {
      const date = new Date();
      setHour(date.getHours());

      if (hour) {
        switch (true) {
          case hour < 12:
            setGreeting("Good morning, ");
            break;
          case hour > 12 && hour < 17:
            setGreeting("Good afternoon, ");
            break;
          case hour >= 17:
            setGreeting("Good evening, ");
            break;
          default:
            setGreeting("Hello, ");
            break;
        }
      }
    };
    if (auth.isAuthenticated) {
      fetchGreeting();
    }
  }, [hour, greeting, auth]);

  return (
    <>
      {!auth.isAuthenticated ? (
        <CRow>
          <CCol>
            <CJumbotron>
              <h1 className="display-3">Welcome to OARS!</h1>
              <p className="lead">
                The Open Asset Repatriation System increases transparency and
                accountability in the asset return process.
              </p>
              <CButton color="primary" onClick={() => auth.login()}>
                Login
              </CButton>
            </CJumbotron>
          </CCol>
        </CRow>
      ) : (
        <CRow>
          <CCol>
            <CJumbotron>
              <h1 className="display-3">
                {greeting}
                {auth.user.firstName}!
              </h1>
              <p className="lead">
                The Open Asset Repatriation System increases transparency and
                accountability in the asset return process.
              </p>
              <CButton color="light" onClick={() => auth.logout()}>
                Logout
              </CButton>
            </CJumbotron>
          </CCol>
        </CRow>
      )}
      {/* <CCol>
          <CJumbotron>
            {!auth.isAuthenticated ? (
              <h1 className="display-3">Welcome to OARS!</h1>
            ) : (
              <h1 className="display-3">
                {greeting}
                {auth.user.firstName}!
              </h1>
            )}
            <p className="lead">
              The Open Asset Repatriation System increases transparency and
              accountability in the asset return process.
            </p>
            <p>For more information visit website</p>
            {!auth.isAuthenticated && (
              <CButton color="primary" onClick={() => auth.login()}>
                Login
              </CButton>
            )}
            {auth.isAuthenticated && (
              <CButton color="light" onClick={() => auth.logout()}>
                Logout
              </CButton>
            )}
          </CJumbotron>
        </CCol> */}

      {(auth.isAuthenticated && auth.meta.keycloak.hasResourceRole("funds_issuer")) && (
        <CCol xl="2">
          <CWidgetSimple header="# Funds Issued" text={fundsState.data.length.toString()}></CWidgetSimple>
        </CCol>
        
      )}
    </>
  );
};

export default HomePage;

import React, { useState, useEffect } from "react";
import { CCol, CRow, CJumbotron, CButton, CCallout } from "@coreui/react";

export const Jumbotron = ({ auth }) => {
  const [hour, setHour] = useState();
  const [greeting, setGreeting] = useState();

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
    <CRow>
      <CCol>
        {!auth.isAuthenticated && (
          <CJumbotron>
            <CRow>
              <CCol>
                <h1 className="display-3">Welcome to OARS!</h1>
                <p className="lead">
                  The Open Asset Repatriation System increases transparency and
                  accountability in the asset return process.
                </p>
                <hr className="my-4" />
                <CButton color="primary" onClick={() => auth.login()}>
                  Login
                </CButton>
              </CCol>
            </CRow>
          </CJumbotron>
        )}
        {auth.isAuthenticated && (
          <CCallout color="dark" className="bg-light mt-0">
            <h1 className="text-muted">
              {greeting}
              {auth.user.firstName}!
            </h1>
            <h6>Your daily summary:</h6>
          </CCallout>
        )}
      </CCol>
    </CRow>
  );
};

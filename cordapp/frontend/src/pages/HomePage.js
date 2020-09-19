import React, { useState, useEffect } from "react";
import {
  CButton,
  CCol,
  CRow,
  CJumbotron,
} from "@coreui/react";
import { useAuth } from "../auth-hook";
import { Widgets } from "../pages/views/home/Widgets";

const HomePage = () => {
  const auth = useAuth();
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
      <Widgets auth={auth}/>
    </>
  );
};

export default HomePage;

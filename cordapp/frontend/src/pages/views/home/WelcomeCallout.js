import React, { useState, useEffect } from "react";
import { CCol, CRow, CCallout } from "@coreui/react";

export const WelcomeCallout = ({ auth }) => {
  const [hour, setHour] = useState();
  const [greeting, setGreeting] = useState();

  useEffect(() => {
    const fetchGreeting = async () => {
      const date = new Date();
      setHour(date.getHours());

      if (hour) {
        switch (true) {
          case hour < 12:
            setGreeting("Good morning, " + auth.user.firstName + "!");
            break;
          case hour >= 12 && hour < 17:
            setGreeting("Good afternoon, " + auth.user.firstName + "!");
            break;
          case hour >= 17:
            setGreeting("Good evening, " + auth.user.firstName + "!");
            break;
          default:
            setGreeting("Hello, " + auth.user.firstName + "!");
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
        {auth.isAuthenticated && (
          <CCallout color="dark" className="bg-light mt-0 mb-4">
            <h1 className="text-muted">
              {greeting}
            </h1>
            <h6>Your daily summary:</h6>
          </CCallout>
        )}
      </CCol>
    </CRow>
  );
};

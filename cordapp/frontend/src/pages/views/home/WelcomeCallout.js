import React, { useState, useEffect } from "react";
import { CCol, CRow, CCallout } from "@coreui/react";

export const WelcomeCallout = ({ auth }) => {
  const [hour, setHour] = useState();
  const [greeting, setGreeting] = useState("");

  useEffect(() => {
    const fetchGreeting = () => {
      const date = new Date();
      setHour(date.getHours());

      if (hour) {
        switch (true) {
          case hour < 12:
            setGreeting("Good morning, ");
            break;
          case hour >= 12 && hour < 17:
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

    fetchGreeting();
  }, [hour]);

  return (
    <CRow>
      <CCol>
        {(auth.isAuthenticated && auth.user.firstName !== undefined) && (
          <CCallout color="dark" className="bg-light mt-0 mb-4">
            <h1 className="text-muted">{greeting}{auth.user.firstName}!</h1>
          </CCallout>
        )}
      </CCol>
    </CRow>
  );
};

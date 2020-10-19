import React, { useState, useEffect } from "react";
import {
  CCol,
  CRow,
  CJumbotron,
  CButton,
  CLink,
  CButtonGroup,
} from "@coreui/react";
import { CreateRequestButton } from "../../../buttons/requests";
import { CreateFundButton } from "../../../buttons/funds";

export const Jumbotron = ({ auth }) => {
  const [hour, setHour] = useState();
  const [greeting, setGreeting] = useState("");
  const [isFundsIssuer, setIsFundsIssuer] = useState(false);
  const [isFundsRequestor, setIsFundsRequestor] = useState(false);
  const [isPartialRequestViewer, setIsPartialRequestViewer] = useState(false);

  useEffect(() => {
    if (auth.isAuthenticated) {
      setIsFundsIssuer(auth.meta.keycloak.hasResourceRole("funds_issuer"));
      setIsFundsRequestor(
        auth.meta.keycloak.hasResourceRole("funds_requestor")
      );
      setIsPartialRequestViewer(auth.meta.keycloak.hasResourceRole("partial_request_viewer"));
    }
  }, [auth]);

  const getQuickActions = () => {
    return (
      (isFundsRequestor && (
        <CButtonGroup className="mr-2">
          <CreateRequestButton size="md" />
        </CButtonGroup>
      )) ||
      (isFundsIssuer && (
        <CButtonGroup className="mr-2">
          <CreateFundButton size="md" />
        </CButtonGroup>
      ))
    );
  };

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

    if (auth.isAuthenticated) {
      fetchGreeting();
    }
  }, [auth.isAuthenticated, hour]);

  return (
    <CRow>
      <CCol>
        {!auth.isAuthenticated && (
          <CJumbotron>
            <CRow>
              <CCol>
                <h2 className="display-3">Welcome to OARS!</h2>
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
          <CJumbotron>
            <CRow>
              <CCol>
                <h2 className="display-3">
                  {greeting}
                  {auth.isAuthenticated &&
                    auth.user.firstName !== undefined &&
                    auth.user.firstName}
                  !
                </h2>
                <p className="lead">
                  The Open Asset Repatriation System increases transparency and
                  accountability in the asset return process.
                </p>
                <hr className="my-4" />
                {getQuickActions()}
                <CButtonGroup className="mr-2">
                  <CLink to={!isPartialRequestViewer ? "/requests" : "/requests/partial"}>
                    <CButton color="primary" shape="square" variant="outline">
                      View Requests
                    </CButton>
                  </CLink>
                </CButtonGroup>
                {(!isFundsRequestor && !isPartialRequestViewer) && (
                  <>
                <CButtonGroup className="mr-2">
                  <CLink to="/returns">
                    <CButton color="primary" shape="square" variant="outline">
                      View Returns
                    </CButton>
                  </CLink>
                </CButtonGroup>
                <CButtonGroup className="mr-2">
                <CLink to="/transfers/approvals">
                  <CButton color="primary" shape="square" variant="outline">
                    View Transfers
                  </CButton>
                </CLink>
              </CButtonGroup></>)}
              </CCol>
            </CRow>
          </CJumbotron>
        )}
      </CCol>
    </CRow>
  );
};

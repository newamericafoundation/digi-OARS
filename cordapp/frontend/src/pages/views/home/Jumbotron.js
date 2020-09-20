import React from "react";
import { CCol, CRow, CJumbotron, CButton } from "@coreui/react";

export const Jumbotron = ({ auth }) => {
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
      </CCol>
    </CRow>
  );
};

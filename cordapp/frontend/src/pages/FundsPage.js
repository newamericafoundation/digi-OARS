import React from "react";
import { CCard, CCardBody, CCardHeader, CButton } from "@coreui/react";
import { FundsTable } from "./views/FundsTable";

const FundsPage = () => {
  return (
    <>
      <CCard>
        <CCardHeader>
          Funds
          <div className="card-header-actions">
            <CButton
              className={"float-right mb-0"}
              color={"primary"}
              tabIndex="0"
            >Issue Funds</CButton>
          </div>
        </CCardHeader>
        <CCardBody>
          <FundsTable />
        </CCardBody>
      </CCard>
    </>
  );
};

export default FundsPage;

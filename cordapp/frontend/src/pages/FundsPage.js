import React from "react";
import {
  CCard,
  CCardBody,
  CCardHeader,
} from "@coreui/react";
import { FundsTable } from "./views/FundsTable";

const FundsPage = () => {
  return (
    <>
      <CCard>
        <CCardHeader>Funds</CCardHeader>
        <CCardBody>
          <FundsTable />
        </CCardBody>
      </CCard>
    </>
  );
};

export default FundsPage;

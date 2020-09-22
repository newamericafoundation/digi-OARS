import React, { useContext } from "react";
import { CCard, CCardBody, CCardHeader, CRow, CCol } from "@coreui/react";
import { PartialRequestsTable } from "./views/withdrawals/PartialRequestsTable";
import { PartialRequestsContext } from "../providers/PartialRequestsProvider";

const PartialWithdrawalsPage = () => {
  const [partialRequestsState] = useContext(PartialRequestsContext);
  return (
    <>
      <CRow>
        <CCol>
          <CCard>
            <CCardHeader><h4 className="mb-0">Requests by Authorized Departments</h4></CCardHeader>
            <CCardBody>
              <PartialRequestsTable
                partialRequests={partialRequestsState.data}
              />
            </CCardBody>
          </CCard>
        </CCol>
      </CRow>
    </>
  );
};

export default PartialWithdrawalsPage;

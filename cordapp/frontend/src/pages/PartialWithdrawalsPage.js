import React, { useContext } from "react";
import { CCard, CCardBody, CCardHeader, CRow, CCol, CCallout } from "@coreui/react";
import { PartialRequestsTable } from "./views/withdrawals/PartialRequestsTable";
import { PartialRequestsContext } from "../providers/PartialRequestsProvider";

const PartialWithdrawalsPage = () => {
  const [partialRequestsState] = useContext(PartialRequestsContext);
  return (
    <>
      <CRow>
        <CCol>
          <CCard>
          <CCardHeader>
              <div className="mb-0">
                <CCallout className="float-left mt-1 mb-1">
                  <h4 className="mt-1">Withdrawal Requests by Authorized Departments</h4>
                </CCallout>
              </div>
            </CCardHeader>
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

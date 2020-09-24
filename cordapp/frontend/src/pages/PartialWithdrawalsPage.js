import React, { useContext } from "react";
import {
  CCard,
  CCardBody,
  CCardHeader,
  CRow,
  CCol,
  CCallout,
} from "@coreui/react";
import { PartialRequestsTable } from "./views/withdrawals/PartialRequestsTable";
import { PartialRequestsContext } from "../providers/PartialRequestsProvider";
import useInterval from "../interval-hook";
import { useAuth } from "../auth-hook";
import * as Constants from "../constants";

const PartialWithdrawalsPage = () => {
  const auth = useAuth();
  const [partialRequestsState, partialRequestsCallback] = useContext(
    PartialRequestsContext
  );

  useInterval(() => {
    if (auth.isAuthenticated) {
      partialRequestsCallback();
    }
  }, Constants.REFRESH_INTERVAL_MS);

  return (
    <>
      <CRow>
        <CCol>
          <CCard>
            <CCardHeader>
              <div className="mb-0">
                <CCallout className="float-left mt-1 mb-1">
                  <h4 className="mt-1">
                    Withdrawal Requests by Authorized Departments
                  </h4>
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

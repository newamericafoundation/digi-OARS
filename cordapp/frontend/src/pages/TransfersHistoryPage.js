import React, { useContext } from "react";
import {
  CCard,
  CCardBody,
  CCardHeader,
  CWidgetProgressIcon,
  CRow,
  CCol,
  CCallout,
} from "@coreui/react";
import CIcon from "@coreui/icons-react";
import { TransfersTable } from "./views/transfers/TransfersTable";
import { TransfersContext } from "../providers/TransfersProvider";
import { toCurrency } from "../utilities";
import useInterval from "../interval-hook";
import * as Constants from "../constants";
import { useAuth } from "../auth-hook";

const TransfersPage = () => {
  const auth = useAuth();
  const [transfersState, transfersCallback] = useContext(TransfersContext);

  useInterval(() => {
    if (auth.isAuthenticated) {
      transfersCallback();
    }
  }, Constants.REFRESH_INTERVAL_MS);

  return (
    <>
      <CRow>
        <CCol xs="12" sm="6" lg="4">
          <CWidgetProgressIcon
            inverse
            header={toCurrency(transfersState.amount, "USD").toString()}
            text="Transferred Requests"
            color="gradient-info"
            value={100}
          >
            <CIcon name="cil-chevron-right" height="36" />
          </CWidgetProgressIcon>
        </CCol>
      </CRow>
      <CRow>
        <CCol>
          <CCard>
            <CCardHeader>
              <div className="mb-0">
                <CCallout className="float-left mt-1 mb-1">
                  <h4 className="mt-1">Transfers History</h4>
                </CCallout>
              </div>
            </CCardHeader>
            <CCardBody>
              <TransfersTable transfers={transfersState} />
            </CCardBody>
          </CCard>
        </CCol>
      </CRow>
    </>
  );
};

export default TransfersPage;

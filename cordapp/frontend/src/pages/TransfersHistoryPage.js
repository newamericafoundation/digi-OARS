import React, { useContext } from "react";
import {
  CCard,
  CCardBody,
  CCardHeader,
  CWidgetProgressIcon,
  CRow,
  CCol,
} from "@coreui/react";
import CIcon from "@coreui/icons-react";
import { RequestsContext } from "../providers/RequestsProvider";
import { TransfersTable } from "./views/transfers/TransfersTable";
import { TransfersContext } from "../providers/TransfersProvider";
import { toCurrency } from "../utilities";

const TransfersPage = () => {
  const [requestsState] = useContext(RequestsContext);
  const [transfersState] = useContext(TransfersContext);

  return (
    <>
      <CRow>
        <CCol xs="12" sm="6" lg="4">
          <CWidgetProgressIcon
            inverse
            header={toCurrency(requestsState.pendingAmount, "USD").toString()}
            text="Transferred Withdrawal Requests"
            color="gradient-info"
            value={
              (requestsState.pendingAmount /
                (requestsState.pendingAmount +
                  requestsState.approvedAmount +
                  requestsState.transferredAmount)) *
              100
            }
          >
            <CIcon name="cil-chevron-right" height="36" />
          </CWidgetProgressIcon>
        </CCol>
      </CRow>
      <CRow>
        <CCol>
          <CCard>
            <CCardHeader>Transfers History</CCardHeader>
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

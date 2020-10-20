import React from "react";
import { CCallout, CCard, CCardBody, CCardHeader } from "@coreui/react";
import TransactionsProvider from "../providers/TransactionsProvider";
import { TransactionsTable } from "./views/explorer/TransactionsTable";

const TransactionsPage = () => {
  return (
    <>
      <TransactionsProvider>
        <CCard>
          <CCardHeader>
            <div className="mb-0">
              <CCallout className="float-left mt-1 mb-1" color="secondary">
                <h4 className="mt-1 text-dark">Node Transactions</h4>
              </CCallout>
            </div>
          </CCardHeader>
          <CCardBody>
              <TransactionsTable />
          </CCardBody>
        </CCard>
      </TransactionsProvider>
    </>
  );
};

export default TransactionsPage;

import React from "react";
import {
  CCallout,
  CCard,
  CCardBody,
  CCardHeader,
} from "@coreui/react";
import { UsersTable } from "./views/admin/UsersTable";

const UsersPage = () => {
  return (
    <>
      <CCard>
        <CCardHeader>
          <div className="mb-0">
            <CCallout className="float-left mt-1 mb-1" color="secondary">
              <h4 className="mt-1 text-dark">Users</h4>
            </CCallout>
          </div>
          {/* <div className="card-header-actions">
            <CButton
              className={"float-right mb-0"}
              color={"primary"}
              tabIndex="0"
            >
              Create New User
            </CButton>
            <CButton
              className={"float-right mb-0 mr-2"}
              color={"light"}
              tabIndex="0"
            >
              Disable User
            </CButton>
            <CButton
              className={"float-right mb-0 mr-2"}
              color={"danger"}
              tabIndex="0"
            >
              Remove User
            </CButton>
          </div> */}
        </CCardHeader>
        <CCardBody>
          <UsersTable />
        </CCardBody>
      </CCard>
    </>
  );
};

export default UsersPage;

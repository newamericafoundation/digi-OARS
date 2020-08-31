import React from "react";
import {
  CButton,
  CCard,
  CCardBody,
  CCardHeader,
} from "@coreui/react";
import { UsersTable } from './views/admin/UsersTable';

const AdminPage = () => {
  return (
    <>
    <CCard>
        <CCardHeader>
          Users
          <div className="card-header-actions">
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
          </div>
        </CCardHeader>
        <CCardBody>
          <UsersTable />
        </CCardBody>
      </CCard>
    </>
  );
};

export default AdminPage;

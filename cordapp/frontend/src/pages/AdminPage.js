import React from "react";
import {
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
          {/* <div className="card-header-actions">
            <CButton
              className={"float-right mb-0"}
              color={"primary"}
              tabIndex="0"
            >
              Create New User
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

export default AdminPage;

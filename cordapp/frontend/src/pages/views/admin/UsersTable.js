import React, { useState, useEffect } from "react";
import {
  CButton,
  CCardBody,
  CDataTable,
  CCollapse,
} from "@coreui/react";
import { useAuth } from "../../../auth-hook";
import axios from "axios";

export const UsersTable = () => {
  const auth = useAuth();
  const [users, setUsers] = useState();
  const [details, setDetails] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      const url =
        "http://" +
        window._env_.KEYCLOAK_URL +
        ":" +
        window._env_.KEYCLOAK_PORT +
        "/auth/admin/realms/" +
        window._env_.KEYCLOAK_REALM +
        "/users";

      const authString = "Bearer ".concat(auth.token);
      const result = await axios.get(url, {
        headers: { Authorization: authString },
      });
      setUsers(result.data);
    };

    if (auth.isAuthenticated) {
      fetchData();
    }
  }, []);

  const toggleDetails = (index) => {
    const position = details.indexOf(index);
    let newDetails = details.slice();
    if (position !== -1) {
      newDetails.splice(position, 1);
    } else {
      newDetails = [...details, index];
    }
    setDetails(newDetails);
  };

  const fields = [
    { key: "username" },
    { key: "firstName" },
    { key: "lastName" },
    { key: "email" },
    { key: "enabled" },
    {
      key: "show_details",
      label: "",
      _style: { width: "1%" },
      sorter: false,
      filter: false,
    },
  ];

  return (
    <>
      {console.log(users)}
      <CDataTable
        items={users}
        fields={fields}
        columnFilter
        footer
        itemsPerPageSelect
        itemsPerPage={5}
        hover
        sorter
        pagination
        scopedSlots={{
          show_details: (item, index) => {
            return (
              <td className="py-2">
                <CButton
                  color="primary"
                  variant="outline"
                  shape="square"
                  size="sm"
                  onClick={() => {
                    toggleDetails(index);
                  }}
                >
                  {details.includes(index) ? "Hide" : "Show"}
                </CButton>
              </td>
            );
          },
          details: (item, index) => {
            return (
              <CCollapse show={details.includes(index)}>
                <CCardBody>
                  <p>User ID: {item.id}</p>
                </CCardBody>
              </CCollapse>
            );
          },
        }}
      />
    </>
  );
};
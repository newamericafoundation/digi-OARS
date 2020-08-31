import React, { useState, useEffect } from "react";
import { CButton, CCardBody, CDataTable, CCollapse } from "@coreui/react";
import { useAuth } from "../../../auth-hook";
import axios from "axios";
import Page503 from '../../error/Page503';

export const UsersTable = () => {
  const auth = useAuth();
  const [users, setUsers] = useState();
  const [details, setDetails] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isError, setIsError] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      setIsLoading(true);
      const url =
        "http://" +
        window._env_.KEYCLOAK_URL +
        ":" +
        window._env_.KEYCLOAK_PORT +
        "/auth/admin/realms/" +
        window._env_.KEYCLOAK_REALM +
        "/users";

      const authString = "Bearer ".concat(auth.token);
      try {
        const result = await axios.get(url, {
          headers: { Authorization: authString },
        });
        setUsers(result.data);
      } catch (error) {
          setIsError(true);
      }
      setIsLoading(false);
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
      {isError && <Page503 />}
      {isLoading ? (
        <div>Loading...</div>
      ) : (
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
      )}
    </>
  );
};

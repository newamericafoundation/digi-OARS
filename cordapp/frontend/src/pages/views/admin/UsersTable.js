import React, { useState, useEffect } from "react";
import { CDataTable } from "@coreui/react";
import { useAuth } from "../../../auth-hook";
import axios from "axios";
// import Page503 from "../../error/Page503";

export const UsersTable = () => {
  const auth = useAuth();
  const [users, setUsers] = useState();
  // const [sessionHistory, setSessionHistory] = useState();
  // const [details, setDetails] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  // const [isError, setIsError] = useState(false);

  const getUsers = () => {
    setIsLoading(true);
    const usersUrl =
      "http://" +
      window._env_.KEYCLOAK_URL +
      ":" +
      window._env_.KEYCLOAK_PORT +
      "/auth/admin/realms/" +
      window._env_.KEYCLOAK_REALM +
      "/users";

    // const sessionsUrl =
    //   "http://" +
    //   window._env_.KEYCLOAK_URL +
    //   ":" +
    //   window._env_.KEYCLOAK_PORT +
    //   "/auth/admin/realms/" +
    //   window._env_.KEYCLOAK_REALM +
    //   "/events?type=LOGIN";

    const authString = "Bearer ".concat(auth.token);

    axios
      .get(usersUrl, {
        headers: { Authorization: authString },
      })
      .then((response) => {
        setUsers(response.data);
        setIsLoading(false);
      });

    // axios
    //   .get(sessionsUrl, {
    //     headers: { Authorization: authString },
    //   })
    //   .then((response) => {
    //     setSessionHistory(response.data);
    //   });
  };

  // const getLastLoggedIn = () => {
  //   const url =
  //     "http://" +
  //     window._env_.KEYCLOAK_URL +
  //     ":" +
  //     window._env_.KEYCLOAK_PORT +
  //     "/auth/admin/realms/" +
  //     window._env_.KEYCLOAK_REALM +
  //     "/events?type=LOGIN";

  //   const authString = "Bearer ".concat(auth.token);

  // };

  useEffect(() => {
    if (auth.isAuthenticated) {
      getUsers();
      // getLastLoggedIn()
    }
    // eslint-disable-next-line
  }, [auth.isAuthenticated]);

  // const toggleDetails = (index) => {
  //   const position = details.indexOf(index);
  //   let newDetails = details.slice();
  //   if (position !== -1) {
  //     newDetails.splice(position, 1);
  //   } else {
  //     newDetails = [...details, index];
  //   }
  //   setDetails(newDetails);
  // };

  const fields = [
    { key: "id", label: "ID" },
    { key: "username" },
    { key: "firstName" },
    { key: "lastName" },
    { key: "email" },
    { key: "enabled" },
  ];

  // const getStuff = () => {
  //   if (users && sessionHistory) {
  //     const sessionHistoryMap = sessionHistory.reduce(
  //       (map, item) => map.set(item.userId, item.time),
  //       new Map()
  //     );
  //     console.log(sessionHistoryMap);

  //     const result = users.map((item) =>
  //       Object.assign(
  //         {
  //           lastLoginLong: sessionHistoryMap.get(item.id),
  //           lastLogin: new Intl.DateTimeFormat('en-US', {
  //             hour: 'numeric',
  //             minute: 'numeric',
  //             second: 'numeric'
  //           }).format(sessionHistoryMap.get(item.id)),
  //         },
  //         item
  //       )
  //     );

  //     console.log(result);
  //   }
  // };

  return (
    <>
      {/* {isError && <Page503 />} */}
      {isLoading ? (
        <div>Loading...</div>
      ) : (
        <CDataTable
          items={users}
          fields={fields}
          columnFilter
          footer
          itemsPerPageSelect
          itemsPerPage={10}
          hover
          sorter
          pagination
        />
      )}
    </>
  );
};

import React from "react";
import * as Constants from "../../../constants";
import { CBadge, CDataTable } from "@coreui/react";
import moment from "moment-timezone";
import { toCurrency } from "../../../utilities";
import { useAuth } from "../../../auth-hook";

export const RequestsSnapshotTable = ({ requests }) => {
  const auth = useAuth();
  const fields = [
    { key: "status", _style: { width: "10%" } },
    { key: "authorizedUserUsername", label: "Requestor" },
    { key: "authorizedUserDept", label: "Department" },
    { key: "amount" },
    { key: "createDateTime", label: "Created Date" },
    { key: "purpose" },
  ];
  const getStatusBadge = (status) => {
    switch (status) {
      case "TRANSFERRED":
        return "secondary";
      case "APPROVED":
        return "success";
      case "PENDING":
        return "warning";
      case "REJECTED":
        return "danger";
      case "FLAGGED":
        return "info";
      default:
        return "primary";
    }
  };

  return (
    <>
      <CDataTable
        items={
          auth.isAuthenticated &&
          auth.meta.keycloak.hasResourceRole("funds_requestor")
            ? requests.filter(
                (request) =>
                  request.authorizedUserDept ===
                  auth.meta.keycloak.tokenParsed.groups[0]
              )
            : requests
        }
        fields={fields}
        columnFilter
        itemsPerPage={5}
        hover
        sorter
        pagination
        scopedSlots={{
          amount: (item) => <td>{toCurrency(item.amount, item.currency)}</td>,
          createDateTime: (item) => (
            <td>
              {moment
                .tz(item.createDateTime, "UTC")
                .format(Constants.DATE_FORMAT)}
            </td>
          ),
          status: (item) => (
            <td>
              <CBadge color={getStatusBadge(item.status)}>{item.status}</CBadge>
            </td>
          ),
        }}
      />
    </>
  );
};

import React, { useState, useContext } from "react";
import * as Constants from "../../../constants";
import {
  CCard,
  CCardHeader,
  CBadge,
  CButton,
  CCardBody,
  CDataTable,
  CCollapse,
  CCol,
  CRow,
  CCallout,
  CSpinner,
} from "@coreui/react";
import Moment from "moment";
import { useAuth } from "auth-hook";
import axios from "axios";
import { APIContext } from "../../../providers/APIProvider";

export const RequestsTable = ({
  filterStatus,
  requests,
  refreshFundsTableCallback,
  refreshRequestsTableCallback,
  isApprover,
}) => {
  const auth = useAuth();
  const [api] = useContext(APIContext);
  const [details, setDetails] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

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
    { key: "authorizedUserUsername", label: "Requestor" },
    { key: "authorizedUserDept", label: "Department" },
    { key: "amount" },
    { key: "createdDateTime", label: "Created Date" },
    { key: "status", _style: { width: "20%" } },
    {
      key: "show_details",
      label: "",
      _style: { width: "1%" },
      sorter: false,
      filter: false,
    },
  ];

  const getStatusBadge = (status) => {
    switch (status) {
      case "APPROVED":
        return "success";
      case "PENDING":
        return "warning";
      default:
        return "primary";
    }
  };

  const toCurrency = (number, currency) => {
    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: currency,
    }).format(number);
  };

  const onHandleApproveClick = (requestId, authorizerUserUsername, index) => {
    setIsLoading(true);
    const url =
      "http://" + window._env_.API_CLIENT_URL + ":" + api.port + "/api/request";

    axios
      .put(url, null, { params: { requestId, authorizerUserUsername } })
      .then((response) => {
        setIsLoading(false);
        refreshFundsTableCallback();
        refreshRequestsTableCallback();
        toggleDetails(index);
      })
      .catch((err) => console.log(err));
  };

  return (
    <>
      <CDataTable
        items={
          isApprover
            ? requests.data.filter((request) => request.status === filterStatus)
            : requests.data.filter(
                (request) =>
                  request.status === filterStatus &&
                  request.authorizedUserDept ===
                    auth.meta.keycloak.tokenParsed.groups[0]
              )
        }
        fields={fields}
        columnFilter
        tableFilter
        footer
        itemsPerPageSelect
        itemsPerPage={5}
        hover
        sorter
        pagination
        scopedSlots={{
          amount: (item) => <td>{toCurrency(item.amount, item.currency)}</td>,
          balance: (item) => <td>{toCurrency(item.balance, item.currency)}</td>,
          maxWithdrawalAmount: (item) => (
            <td>{toCurrency(item.maxWithdrawalAmount, item.currency)}</td>
          ),
          createDateTime: (item) => (
            <td>{Moment(item.createDateTime).format(Constants.DATEFORMAT)}</td>
          ),
          updatedDateTime: (item) => (
            <td>{Moment(item.updatedDateTime).format(Constants.DATEFORMAT)}</td>
          ),
          status: (item) => (
            <td>
              <CBadge color={getStatusBadge(item.status)}>{item.status}</CBadge>
            </td>
          ),
          show_details: (item, index) => {
            return (
              <td>
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
                <CCard className="m-3">
                  <CCardHeader>
                    Request Details
                    {item.status !== Constants.REQUEST_APPROVED &&
                    isApprover ? (
                      <div className="card-header-actions">
                        <CButton
                          className={"float-right mb-0"}
                          color="success"
                          variant="outline"
                          shape="square"
                          size="sm"
                          onClick={() =>
                            onHandleApproveClick(
                              item.linearId,
                              auth.user.fullName,
                              index
                            )
                          }
                        >
                          {isLoading ? (
                            <CSpinner
                              className="spinner-border spinner-border-sm mr-1"
                              role="status"
                              aria-hidden="true"
                            />
                          ) : null}
                          Approve Request
                        </CButton>
                      </div>
                    ) : null}
                  </CCardHeader>
                  <CCardBody>
                    <CRow>
                      <CCol xl="6" sm="4">
                        <CCallout color="info" className={"bg-light"}>
                          <p className="text-muted mb-0">Requestor</p>
                          <strong className="p">
                            {item.authorizedUserUsername}
                          </strong>
                          <p className="text-muted mb-0">Department</p>
                          <strong className="p">
                            {item.authorizedUserDept}
                          </strong>
                        </CCallout>
                        <CCallout color="info" className={"bg-light"}>
                          <p className="text-muted mb-0">Amount</p>
                          <strong className="p">
                            {toCurrency(item.amount, item.currency)}
                          </strong>
                        </CCallout>
                        <CCallout color="info" className={"bg-light"}>
                          <p className="text-muted mb-0">Date/Time</p>
                          <strong className="p">
                            {Moment(item.dateTime).format(
                              "DD/MMM/YYYY HH:mm:ss"
                            )}
                          </strong>
                        </CCallout>
                      </CCol>
                      <CCol xl="6" sm="4">
                        <CCallout color="info" className={"bg-light"}>
                          <p className="text-muted mb-0">State ID</p>
                          <strong className="p">{item.linearId}</strong>
                        </CCallout>
                        <CCallout color="info" className={"bg-light"}>
                          <p className="text-muted mb-0">Transaction ID</p>
                          <strong className="p">{item.txId}</strong>
                        </CCallout>
                        <CCallout
                          color={
                            item.status === Constants.REQUEST_PENDING
                              ? "warning"
                              : "success"
                          }
                          className={"bg-light"}
                        >
                          <p className="text-muted mb-0">Status</p>
                          <strong className="p">
                            {item.status}
                            {item.status === Constants.REQUEST_APPROVED
                              ? " by " + item.authorizerUserUsername
                              : null}
                          </strong>
                        </CCallout>
                      </CCol>
                    </CRow>
                  </CCardBody>
                </CCard>
              </CCollapse>
            );
          },
        }}
      />
    </>
  );
};

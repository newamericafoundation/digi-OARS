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
  CProgress,
} from "@coreui/react";
import Moment from "moment";

export const RequestsTable = ({filterStatus, requests }) => {
  const [details, setDetails] = useState([]);

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
    { key: "authorizedUserUsername", label: "Requestor"},
    { key: "authorizedUserDept", label: "Department"},
    { key: "amount" },
    { key: "datetime", label: "Date" },
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

  return (
    <>
      <CDataTable
        items={requests.data.filter(
          (request) => request.status === filterStatus
        )}
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
          datetime: (item) => (
            <td>{Moment(item.datetime).format(Constants.DATEFORMAT)}</td>
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
                  <CCardHeader>Request Details</CCardHeader>
                  <CCardBody>
                    
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

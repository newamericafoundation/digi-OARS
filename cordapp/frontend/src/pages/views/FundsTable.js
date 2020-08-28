import React, { useState } from "react";
import {
  CBadge,
  CButton,
  CCardBody,
  CDataTable,
  CCollapse,
} from "@coreui/react";
// import CIcon from '@coreui/icons-react'
// import { useAuth } from '../auth-hook';
import { FundData } from "../../data/Funds";

export const FundsTable = () => {
  const fundData = FundData;

  const [details, setDetails] = useState([]);
  // const [items, setItems] = useState(usersData)

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
    { key: "linearId" },
    { key: "originCountry" },
    { key: "receivingCountry" },
    { key: "amount" },
    { key: "balance" },
    { key: "datetime" },
    { key: "maxWithdrawalAmount" },
    { key: "currency" },
    { key: "status", _style: { width: "20%" } },
    {
      key: "show_details",
      label: "",
      _style: { width: "1%" },
      sorter: false,
      filter: false,
    },
  ];

  const getBadge = (status) => {
    switch (status) {
      case "RECEIVED":
        return "success";
      case "ISSUED":
        return "warning";
      default:
        return "primary";
    }
  };

  return (
    <CDataTable
      items={fundData}
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
        status: (item) => (
          <td>
            <CBadge color={getBadge(item.status)}>{item.status}</CBadge>
          </td>
        ),
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
                <h4>Linear Id: {item.linearId}</h4>
                <p className="text-muted">
                    Origin Country: {item.originCountry}<br/>
                    Receiving Country: {item.receivingCountry}<br/>
                    Amount ({item.currency}): {item.amount}<br/>
                    Balance ({item.currency}): {item.balance}<br/>
                    Max Withdrawal Amount ({item.currency}): {item.maxWithdrawalAmount}<br/>
                </p>
              </CCardBody>
            </CCollapse>
          );
        },
      }}
    />
  );
};

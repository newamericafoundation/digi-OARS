import React, { useState } from "react";
import * as Constants from "../../../constants";
import {
  CCard,
  CCardHeader,
  CButton,
  CCardBody,
  CDataTable,
  CCollapse,
  CCol,
  CRow,
  CCallout,
} from "@coreui/react";
import Moment from "moment";
import EllipsesText from "react-ellipsis-text";

export const TransfersTable = ({ transfers }) => {
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
    { key: "receivingDept", label: "Department" },
    { key: "authorizedUserUsername", label: "Requestor" },
    { key: "amount" },
    { key: "createDateTime", label: "Created Date" },
    {
      key: "show_details",
      label: "",
      _style: { width: "1%" },
      sorter: false,
      filter: false,
    },
  ];

  const toCurrency = (number, currency) => {
    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: currency,
    }).format(number);
  };

  const getData = () => {
    return transfers.data;
  };

  return (
    <>
      <CDataTable
        items={getData()}
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
          createDateTime: (item) => (
            <td>{Moment(item.createDateTime).format(Constants.DATEFORMAT)}</td>
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
                  <CCardHeader>Transfer Details</CCardHeader>
                  <CCardBody>
                    <CRow>
                      <CCol xl="6" sm="4">
                        <CCallout color="info" className={"bg-light"}>
                          <p className="text-muted mb-0">Requestor</p>
                          <strong className="p">
                            {item.authorizedUserUsername}
                          </strong>
                        </CCallout>
                        <CCallout color="info" className={"bg-light"}>
                          <p className="text-muted mb-0">Department</p>
                          <strong className="p">{item.receivingDept}</strong>
                        </CCallout>
                      </CCol>
                      <CCol xl="6" sm="4">
                        <CCallout color="info" className={"bg-light"}>
                          <p className="text-muted mb-0">Amount</p>
                          <strong className="p">
                            {toCurrency(item.amount, item.currency)}
                          </strong>
                        </CCallout>
                        <CCallout color="info" className={"bg-light"}>
                          <p className="text-muted mb-0">Created Date/Time</p>
                          <strong className="p">
                            {Moment(item.createDateTime).format(
                              "DD/MMM/YYYY HH:mm:ss"
                            )}
                          </strong>
                        </CCallout>
                      </CCol>
                    </CRow>
                    {/* <CRow>
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
                            {Moment(item.createDateTime).format(
                              "DD/MMM/YYYY HH:mm:ss"
                            )}
                          </strong>
                        </CCallout>
                      </CCol>
                      <CCol xl="6" sm="4">
                        <CTooltip content={item.linearId} placement="right-end">
                          <CCallout color="info" className={"bg-light"}>
                            <p className="text-muted mb-0">State ID</p>
                            <strong className="p">
                              <EllipsesText text={item.linearId} length={30} />
                            </strong>
                          </CCallout>
                        </CTooltip>
                        <CTooltip content={item.txId} placement="right-end">
                          <CCallout color="info" className={"bg-light"}>
                            <p className="text-muted mb-0">Transaction ID</p>
                            <strong className="p">
                              <EllipsesText text={item.txId} length={30} />
                            </strong>
                          </CCallout>
                        </CTooltip>
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
                    <CRow>
                      <CCol>
                        <CCallout color="info" className={"bg-light"}>
                          <p className="text-muted mb-0">Purpose</p>
                          <strong className="p">{item.purpose}</strong>
                        </CCallout>
                      </CCol>
                    </CRow> */}
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

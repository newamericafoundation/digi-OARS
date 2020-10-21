import React, { useState, useContext } from "react";
import {
  CButton,
  CDataTable,
  CCard,
  CCardHeader,
  CCardBody,
  CRow,
  CCol,
  CCollapse,
  CCardFooter,
  CAlert,
  CBadge,
} from "@coreui/react";
import { TransactionsContext } from "../../../providers/TransactionsProvider";
import CIcon from "@coreui/icons-react";
import * as Constants from "../../../constants";
import useInterval from "../../../interval-hook";
import { useAuth } from "../../../auth-hook";



export const TransactionsTable = () => {
  const auth = useAuth();
  const [transactions, transactionsCallback] = useContext(TransactionsContext);
  const [details, setDetails] = useState([]);

  const fields = [
    { key: "transactionId", label: "Tx ID", _style: { width: "5%" } },
    { key: "inputTypes", label: "Input State" },
    { key: "commands", label: "Contract Command" },
    { key: "outputTypes", label: "Output State" },
    { key: "consumed", label: "Output State Status" },
    {
      key: "show_details",
      label: "Details",
      _style: { width: "1%" },
      sorter: false,
      filter: false,
    },
  ];

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

  const flattenObject = function (ob) {
    var toReturn = {};

    for (var i in ob) {
      if (!ob.hasOwnProperty(i)) continue;

      if (typeof ob[i] == "object") {
        var flatObject = flattenObject(ob[i]);
        for (var x in flatObject) {
          if (!flatObject.hasOwnProperty(x)) continue;

          toReturn[i + "." + x] = flatObject[x];
        }
      } else {
        toReturn[i] = ob[i];
      }
    }
    return toReturn;
  };

  const checkIfConsumed = (txId) => {
    const txConsumed = transactions.data.filter((data) => data.inputs);
    return txConsumed.some((data) => data.inputs[0].stateRef.txhash === txId);
  };

  useInterval(() => {
    if (auth.isAuthenticated) {
      transactionsCallback();
    }
  }, Constants.REFRESH_INTERVAL_MS);

  return (
    <>
      {transactions.loading ? (
        <div>Loading...</div>
      ) : (
        <CDataTable
          items={transactions.data}
          fields={fields}
          columnFilter
          footer
          itemsPerPage={20}
          hover
          sorter
          pagination
          scopedSlots={{
            signers: (item) => (
              <td>{item.signers.map((signer) => signer.partyName)}</td>
            ),
            inputTypes: (item) => (
              <td>
                {item.inputTypes
                  ? item.inputTypes.map((inputType) => inputType.type)
                  : null}
              </td>
            ),
            outputTypes: (item) => (
              <td>{item.outputTypes.map((outputType) => outputType.type)}</td>
            ),
            consumed: (item) => (
              <td>
                {checkIfConsumed(item.outputs[0].stateRef.txhash) ? (
                  <CBadge color="warning">STATE CONSUMED</CBadge>
                ) : (
                  <CBadge color="success">STATE UNCONSUMED</CBadge>
                )}
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
                      Transaction Details
                      {/* {getActionButton(item, index)} */}
                    </CCardHeader>
                    <CCardBody>
                      <CRow>
                        <CCol xl="5" sm="4">
                          <CCard>
                            <CCardHeader color="secondary">
                              <strong>Input State</strong>
                              <CBadge
                                shape="pill"
                                color="warning"
                                className="float-right mt-1 mb-1"
                              >
                                {item.inputs && item.outputs
                                  ? "STATE CONSUMED"
                                  : null}
                              </CBadge>
                            </CCardHeader>
                            <CCardBody>
                              {item.inputs ? (
                                item.inputs.map((input) => (
                                  <CCard key={input.stateRef.txhash}>
                                    <CCardHeader>
                                      {input.type}
                                      <br />
                                      <small className="text-muted">
                                        {input.stateRef.txhash}
                                      </small>
                                    </CCardHeader>
                                    <CCardBody>
                                      {Object.keys(
                                        flattenObject(input.state)
                                      ).map((keyName, i) => (
                                        <dl className="row mb-0" key={i}>
                                          <dt className="col-sm-6">
                                            {keyName}
                                          </dt>
                                          <dd className="col-sm-6">
                                            {
                                              flattenObject(input.state)[
                                                keyName
                                              ]
                                            }
                                          </dd>
                                        </dl>
                                      ))}
                                    </CCardBody>
                                  </CCard>
                                ))
                              ) : (
                                <CAlert color="info">
                                  No input state - this indicates that a new
                                  Corda state has been created.
                                </CAlert>
                              )}
                            </CCardBody>
                          </CCard>
                        </CCol>
                        <CCol xl="2" sm="4">
                          <CCard>
                            <CCardHeader
                              color="dark"
                              className="text-center text-white"
                            >
                              <strong>Command</strong>
                            </CCardHeader>
                            <CCardBody className="text-center">
                              <CIcon name="cil-arrow-right" />
                              {" "}{item.commands}{" "}
                              <CIcon name="cil-arrow-right" />
                            </CCardBody>
                            <CCardFooter>
                              <strong>Signers</strong>
                              <br />
                              <small>
                                <ul>
                                  {item.signers.map((signer) => (
                                    <li key={signer.signature.by}>
                                      {signer.partyName}
                                    </li>
                                  ))}
                                </ul>
                                {/* {item.signers.map((signer) => signer.partyName)} */}
                              </small>
                            </CCardFooter>
                          </CCard>
                        </CCol>
                        <CCol xl="5" sm="4">
                          <CCard>
                            <CCardHeader color="secondary">
                              <strong>Output State</strong>
                              {item.outputs &&
                              checkIfConsumed(
                                item.outputs[0].stateRef.txhash
                              ) ? (
                                <CBadge
                                  shape="pill"
                                  color="warning"
                                  className="float-right mt-1 mb-1"
                                >
                                  STATE CONSUMED
                                </CBadge>
                              ) : (
                                <CBadge
                                  shape="pill"
                                  color="success"
                                  className="float-right mt-1 mb-1"
                                >
                                  STATE UNCONSUMED
                                </CBadge>
                              )}
                            </CCardHeader>
                            <CCardBody>
                              {item.outputs
                                ? item.outputs.map((output) => (
                                    <CCard key={output.stateRef.txhash}>
                                      <CCardHeader>
                                        {output.type}
                                        <br />
                                        <small className="text-muted">
                                          {output.stateRef.txhash}
                                        </small>
                                      </CCardHeader>
                                      <CCardBody>
                                        {Object.keys(
                                          flattenObject(output.state)
                                        ).map((keyName, i) => (
                                          <dl className="row mb-0" key={i}>
                                            <dt className="col-sm-6">
                                              {keyName}
                                            </dt>
                                            <dd className="col-sm-6">
                                              {
                                                flattenObject(output.state)[
                                                  keyName
                                                ]
                                              }
                                            </dd>
                                          </dl>
                                        ))}
                                      </CCardBody>
                                    </CCard>
                                  ))
                                : null}
                            </CCardBody>
                          </CCard>
                        </CCol>
                      </CRow>
                    </CCardBody>
                  </CCard>
                </CCollapse>
              );
            },
          }}
        />
      )}
    </>
  );
};

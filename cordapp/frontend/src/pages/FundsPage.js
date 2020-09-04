import React, { useState, useEffect, useContext } from "react";
import {
  CCard,
  CCardBody,
  CCardHeader,
  CButton,
  CModal,
  CModalHeader,
  CModalBody,
  CModalTitle,
} from "@coreui/react";
import { FundsTable } from "./views/funds/FundsTable";
import { FundsForm } from "./views/funds/FundsForm";
import NetworkProvider from "../providers/NetworkProvider";
import ReactNotification from "react-notifications-component";

import UseToaster from "../notification/Toaster";
import FundsProvider from "../providers/FundsProvider";
// import { FundsContext } from "../providers/FundsProvider";
// import getFunds from '../data/GetFunds'

const FundsPage = () => {
  // const funds = useContext(FundsContext);
  // const [dataState, setDataState] = useState([])

  // const summin = () => {
  //   dispatch({
  //     type: "GET_FUNDS", payload: getFunds().then(item => item)
  //   })
  // }
  // useEffect(() => {
  //   getFunds().then(item => {
  //     setDataState(item);
  //     console.log(item);
  //     console.log(dataState);
  //   })
  // }, [])
  // console.log(funds)
  const [show, setShow] = useState(false);
  const handleShow = () => setShow(true);
  const handleClose = () => setShow(false);
  const onFormSubmit = (response) => {
    handleClose();
    response.status === 200
      ? UseToaster("Success", response.entity.message, "success")
      : UseToaster("Error", response.entity.message, "danger");
    // summin();
    // console.log(state)
  };

  return (
    <>
      <FundsProvider>
        <ReactNotification />
        <CCard>
          <CCardHeader>
            Funds
            <div className="card-header-actions">
              <CButton
                className={"float-right mb-0"}
                color={"primary"}
                tabIndex="0"
                onClick={handleShow}
              >
                Issue Funds
              </CButton>
            </div>
          </CCardHeader>
          <CCardBody>
            <FundsTable />
          </CCardBody>
        </CCard>
        <CModal show={show} onClose={handleClose}>
          <CModalHeader closeButton>
            <CModalTitle>Funds Form</CModalTitle>
          </CModalHeader>
          <CModalBody>
            <NetworkProvider>
              <FundsForm onSubmit={onFormSubmit} />
            </NetworkProvider>
          </CModalBody>
        </CModal>
      </FundsProvider>
    </>
  );
};

export default FundsPage;

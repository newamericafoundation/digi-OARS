import React, { useState, useContext } from "react";
import { CButton, CBadge, CModal, CModalHeader, CModalBody, CModalTitle } from "@coreui/react";
import cogoToast from "cogo-toast";
import { toCurrency } from "../../utilities";
import { FundsForm } from "../../pages/views/funds/FundsForm";
import { FundsContext } from "../../providers/FundsProvider";
import NetworkProvider from "../../providers/NetworkProvider";

export const CreateFundButton = ({ size }) => {
  const [show, setShow] = useState(false);
  const [, fundsCallback] = useContext(FundsContext);

  const handleShow = () => setShow(true);
  const handleClose = () => setShow(false);

  const responseMessage = (message) => {
    return (
      <div>
        <strong>Fund ID:</strong> {message.entity.data.linearId.id}
        <br />
        <strong>Status:</strong>{" "}
        <CBadge color="warning">{message.entity.data.status}</CBadge>
        <br />
        <strong>Amount:</strong> {toCurrency(message.entity.data.amount, "USD")}
      </div>
    );
  };

  const onFormSubmit = (response) => {
    handleClose();
    if (response.status === 200) {
      const { hide } = cogoToast.success(responseMessage(response), {
        heading: "Return Repatriated",
        position: "top-right",
        hideAfter: 8,
        onClick: () => {
          hide();
        },
      });
      fundsCallback();
    } else {
      const { hide } = cogoToast.error(response.entity.message, {
        heading: "Error Repatriating Return",
        position: "top-right",
        hideAfter: 8,
        onClick: () => {
          hide();
        },
      });
    }
  };

  return (
    <>
      <CButton
        color="success"
        variant="outline"
        shape="square"
        size={size}
        onClick={() => handleShow()}
      >
        Issue a Return
      </CButton>
      <CModal show={show} onClose={handleClose} closeOnBackdrop={false}>
        <CModalHeader closeButton>
          <CModalTitle>Returns Form</CModalTitle>
        </CModalHeader>
        <CModalBody>
          <NetworkProvider>
            <FundsForm onSubmit={onFormSubmit} />
          </NetworkProvider>
        </CModalBody>
      </CModal>
    </>
  );
};

export default CreateFundButton;

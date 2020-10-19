import React, { useState, useContext } from "react";
import { CButton, CBadge } from "@coreui/react";
import cogoToast from "cogo-toast";
import { toCurrency } from "../../utilities";
import { RequestForm } from "../../pages/views/withdrawals/RequestForm";
import { FundsContext } from "../../providers/FundsProvider";
import { RequestsContext } from "../../providers/RequestsProvider";

export const CreateRequestButton = ({ size }) => {
  const [show, setShow] = useState(false);
  const [, fundsCallback] = useContext(FundsContext);
  const [, requestsCallback] = useContext(RequestsContext);

  const handleShow = () => setShow(true);
  const handleClose = () => setShow(false);

  const responseMessage = (message) => {
    return (
      <div>
        <strong>Request ID:</strong> {message.entity.data.linearId.id}
        <br />
        <strong>Status:</strong> <CBadge color="warning">PENDING</CBadge>
        <br />
        <strong>Amount:</strong> {toCurrency(message.entity.data.amount, "USD")}
      </div>
    );
  };

  const onFormSubmit = (response) => {
    handleClose();
    if (response.status === 200) {
      const { hide } = cogoToast.success(responseMessage(response), {
        heading: "Request Created",
        position: "top-right",
        hideAfter: 8,
        onClick: () => {
          hide();
        },
      });
    } else {
      const { hide } = cogoToast.error(response.entity.message, {
        heading: "Error Receiving Funds",
        position: "top-right",
        hideAfter: 8,
        onClick: () => {
          hide();
        },
      });
    }
    fundsCallback();
    requestsCallback();
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
        Make a Request
      </CButton>
      <RequestForm
        show={show}
        onSubmit={onFormSubmit}
        handleClose={handleClose}
      />
    </>
  );
};

export default CreateRequestButton;

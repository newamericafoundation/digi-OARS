import React, { useState, useContext } from "react";
import {
  CFormGroup,
  CLabel,
  CInput,
  CButton,
  CForm,
  CInputGroup,
  CInputGroupPrepend,
  CInputGroupText,
  CTextarea,
  CSpinner,
  CModal,
  CModalHeader,
  CModalTitle,
  CModalBody,
  CModalFooter,
} from "@coreui/react";
import CIcon from "@coreui/icons-react";
import useForm from "../../../form/index";
import { useAuth } from "../../../auth-hook";
import axios from "axios";
import { APIContext } from "../../../providers/APIProvider";
import CurrencyInput from "../../../form/CurrencyInput";

export const RequestForm = ({ show, onSubmit, handleClose }) => {
  const auth = useAuth();
  const [api] = useContext(APIContext);
  const [isLoading, setIsLoading] = useState(false);

  const stateSchema = {
    authorizedUserUsername: { value: auth.user.fullName, error: "" },
    amount: { value: 0, error: "" },
    purpose: { value: "", error: "" },
    accountId: { value: "", error: "" },
  };

  const stateValidatorSchema = {
    amount: {
      required: true,
      validator: {
        func: (value) =>
          /^[+-]?[0-9]{1,3}(?:,?[0-9]{3})*(?:\.[0-9]{2})?$/.test(value),
        error: "Invalid currency format.",
      },
    },
    accountId: {
      required: true,
    },
    purpose: {
      required: true,
    },
  };

  const onSubmitForm = (state) => {
    setIsLoading(true);
    const url =
      "http://" + window._env_.API_CLIENT_URL + ":" + api.port + "/api/request";

    axios
      .post(url, {
        amount: state.amount,
        authorizedUserDept: auth.meta.keycloak.tokenParsed.groups[0],
        authorizedUserUsername: auth.user.fullName,
        externalAccountId: state.accountId,
        purpose: state.purpose,
      })
      .then((response) => {
        onSubmit(response.data);
        setIsLoading(false);
      })
      .catch((err) => console.log(err));
  };

  const { values, errors, handleOnChange, handleOnSubmit, disable } = useForm(
    stateSchema,
    stateValidatorSchema,
    onSubmitForm
  );

  const {
    authorizedUserUsername,
    authorizedUserDept,
    accountId,
    purpose,
  } = values;

  return (
    <CModal show={show} onClose={handleClose} size="lg" closeOnBackdrop={false}>
      <CForm onSubmit={handleOnSubmit}>
        <CModalHeader closeButton>
          <CModalTitle>Request Form</CModalTitle>
        </CModalHeader>
        <CModalBody>
          <CFormGroup>
            <CLabel htmlFor="authorizedUserUsername">Requestor</CLabel>
            <CInputGroup className="input-prepend">
              <CInputGroupPrepend>
                <CInputGroupText>
                  <CIcon name="cil-user"></CIcon>
                </CInputGroupText>
              </CInputGroupPrepend>
              <CInput
                type="text"
                name="authorizedUserUsername"
                id="authorizedUserUsername"
                placeholder={auth.user.fullName}
                value={authorizedUserUsername}
                disabled
              />
            </CInputGroup>
          </CFormGroup>
          <CFormGroup>
            <CLabel htmlFor="authorizedUserUsername">Department</CLabel>
            <CInputGroup className="input-prepend">
              <CInputGroupPrepend>
                <CInputGroupText>
                  <CIcon name="cil-user"></CIcon>
                </CInputGroupText>
              </CInputGroupPrepend>
              <CInput
                type="text"
                name="authorizedUserDept"
                id="authorizedUserDept"
                placeholder={auth.meta.keycloak.tokenParsed.groups[0]}
                value={authorizedUserDept}
                disabled
              />
            </CInputGroup>
          </CFormGroup>
          <CFormGroup>
            <CLabel htmlFor="amount">Request Amount</CLabel>
            <CInputGroup className="input-prepend">
              <CInputGroupPrepend>
                <CInputGroupText>$</CInputGroupText>
              </CInputGroupPrepend>
              <CurrencyInput
                className="form-control"
                placeholder="0.00"
                type="text"
                name="amount"
                id="amount"
                onChange={handleOnChange}
              />
            </CInputGroup>
            <div className="text-muted small">
              <p className="text-danger">{errors.amount}</p>
            </div>
          </CFormGroup>
          <CFormGroup>
            <CLabel htmlFor="purpose">Purpose</CLabel>
            <CInputGroup className="input-prepend">
              <CInputGroupPrepend>
                <CInputGroupText>
                  <CIcon name="cil-speech"></CIcon>
                </CInputGroupText>
              </CInputGroupPrepend>
              <CTextarea
                type="text"
                name="purpose"
                id="purpose"
                placeholder="Purpose of funds requested"
                rows={3}
                value={purpose}
                onChange={handleOnChange}
              />
            </CInputGroup>
            <div className="text-muted small">
              <p className="text-danger">{errors.purpose}</p>
            </div>
          </CFormGroup>
          <CFormGroup>
            <CLabel htmlFor="accountId">Account ID</CLabel>
            <CInputGroup className="input-prepend">
              <CInputGroupPrepend>
                <CInputGroupText>
                  <CIcon name="cil-briefcase"></CIcon>
                </CInputGroupText>
              </CInputGroupPrepend>
              <CInput
                type="text"
                name="accountId"
                id="accountId"
                placeholder=""
                value={accountId}
                onChange={handleOnChange}
              />
            </CInputGroup>
            <div className="text-muted small">
              <p className="text-danger">{errors.accountId}</p>
            </div>
          </CFormGroup>
        </CModalBody>
        <CModalFooter>
          <CButton color="primary" type="submit" disabled={disable}>
            {isLoading ? (
              <CSpinner
                className="spinner-border spinner-border-sm mr-1"
                role="status"
                aria-hidden="true"
              />
            ) : null}
            Submit
          </CButton>
          <CButton color="secondary" onClick={handleClose}>
            Cancel
          </CButton>
        </CModalFooter>
      </CForm>
    </CModal>
  );
};

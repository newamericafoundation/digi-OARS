import React, { useState } from "react";
import {
  CRow,
  CCol,
  CFormGroup,
  CLabel,
  CInput,
  CButton,
  CForm,
  CInputGroup,
  CInputGroupPrepend,
  CInputGroupText,
  CInvalidFeedback,
  CTextarea,
  CSpinner
} from "@coreui/react";
import CIcon from "@coreui/icons-react";
import useForm from "../../../form/index";
import { useAuth } from "../../../auth-hook";
import axios from "axios";

export const RequestForm = ({ onSubmit, request }) => {
  const auth = useAuth();
  const [isLoading, setIsLoading] = useState(false);

  const stateSchema = {
    fundStateId: { value: request.linearId, error: "" },
    authorizedUserUsername: { value: auth.user.fullName, error: "" },
    amount: { value: 0, error: "" },
    purpose: { value: "", error: "" },
    externalAccountId: { value: "", error: "" },
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
    externalAccountId: {
      required: false,
    },
    purpose: {
      required: true,
    }
  };

  const onSubmitForm = (state) => {
    setIsLoading(true);
    const url =
      "http://" +
      window._env_.API_CLIENT_URL +
      ":" +
      window._env_.API_CLIENT_PORT +
      "/api/request";

    axios
      .post(url, {
        amount: state.amount,
        authorizedUserDept: auth.meta.keycloak.tokenParsed.groups[0],
        authorizedUserUsername: auth.user.fullName,
        externalAccountId: state.externalAccountId,
        fundStateLinearId: request.linearId,
        purpose: state.purpose,
      })
      .then((response) => {
        onSubmit(response.data);
        setIsLoading(false);
      })
      .catch((err) => console.log(err));
    onSubmit();
  };

  const { values, errors, handleOnChange, handleOnSubmit, disable } = useForm(
    stateSchema,
    stateValidatorSchema,
    onSubmitForm
  );

  const {
    fundStateId,
    authorizedUserUsername,
    amount,
    externalAccountId,
    purpose
  } = values;

  return (
    <CCol>
      <CForm onSubmit={handleOnSubmit}>
        <CRow>
          <CCol xs="12" md="9" xl="6">
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
                <CInvalidFeedback>{errors.amount}</CInvalidFeedback>
              </CInputGroup>
            </CFormGroup>
            <CFormGroup>
              <CLabel htmlFor="amount">Request Amount</CLabel>
              <CInputGroup className="input-prepend">
                <CInputGroupPrepend>
                  <CInputGroupText>$</CInputGroupText>
                </CInputGroupPrepend>
                <CInput
                  type="number"
                  name="amount"
                  id="amount"
                  value={amount}
                  placeholder={0}
                  valid={errors.amount.length === 0}
                  invalid={errors.amount.length > 0}
                  onChange={handleOnChange}
                />
                <CInvalidFeedback>{errors.amount}</CInvalidFeedback>
              </CInputGroup>
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
              </CFormGroup>
            </CFormGroup>
          </CCol>
          <CCol xs="12" md="9" xl="6">
            <CFormGroup>
              <CLabel htmlFor="fundStateId">Fund State ID</CLabel>
              <CInputGroup className="input-prepend">
                <CInputGroupPrepend>
                  <CInputGroupText>
                    <CIcon name="cil-wallet"></CIcon>
                  </CInputGroupText>
                </CInputGroupPrepend>
                <CInput
                  type="text"
                  name="fundStateId"
                  id="fundStateId"
                  placeholder={request.linearId}
                  value={fundStateId}
                  disabled
                />
              </CInputGroup>
            </CFormGroup>
            <CFormGroup>
              <CLabel htmlFor="externalAccountId">External Account ID</CLabel>
              <CInputGroup className="input-prepend">
                <CInputGroupPrepend>
                  <CInputGroupText>
                    <CIcon name="cil-briefcase"></CIcon>
                  </CInputGroupText>
                </CInputGroupPrepend>
                <CInput
                  type="text"
                  name="externalAccountId"
                  id="externalAccountId"
                  placeholder=""
                  value={externalAccountId}
                  onChange={handleOnChange}
                />
              </CInputGroup>
            </CFormGroup>
          </CCol>
        </CRow>
        <CRow>
          <CCol xs="12">
            <CFormGroup>
              <CButton
                className={"float-right"}
                color="primary"
                type="submit"
                disabled={disable}
              >
                {isLoading ? (
                    <CSpinner
                      className="spinner-border spinner-border-sm mr-1"
                      role="status"
                      aria-hidden="true"
                    />
                  ) : null}
                Submit
              </CButton>
            </CFormGroup>
          </CCol>
        </CRow>
      </CForm>
    </CCol>
  );
};

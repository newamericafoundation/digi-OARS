import React from "react";
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
} from "@coreui/react";
import CIcon from "@coreui/icons-react";
import useForm from "../../../form/index";
// import axios from "axios";
import { useAuth } from "../../../auth-hook";

export const RequestForm = ({ onSubmit }) => {
  const auth = useAuth();

  const stateSchema = {
    amount: { value: 0, error: "" },
    maxWithdrawalAmount: { value: 0, error: "" },
  };

  const stateValidatorSchema = {
    authorizedUser: {
      required: true,
    },
    amount: {
      required: true,
      validator: {
        func: (value) =>
          /^[+-]?[0-9]{1,3}(?:,?[0-9]{3})*(?:\.[0-9]{2})?$/.test(value),
        error: "Invalid currency format.",
      },
    },
    maxWithdrawalAmount: {
      required: true,
      validator: {
        func: (value) =>
          /^[+-]?[0-9]{1,3}(?:,?[0-9]{3})*(?:\.[0-9]{2})?$/.test(value),
        error: "Invalid currency format.",
      },
    },
  };

  const onSubmitForm = (state) => {
    //   console.log(state);
    // alert(JSON.stringify(state, null, 2));

    const url =
      "http://" +
      window._env_.API_CLIENT_URL +
      ":" +
      window._env_.API_CLIENT_PORT +
      "/api/fund";

    //   axios.post(url, {
    //       originParty: "O=USDoJ, L=New York, C=US",
    //       receivingParty: state.receivingParty,
    //       amount: state.amount,
    //       maxWithdrawalAmount: state.maxWithdrawalAmount
    //   }).then(res => console.log(res))

    onSubmit();
  };

  const { values, errors, handleOnChange, handleOnSubmit, disable } = useForm(
    stateSchema,
    stateValidatorSchema,
    onSubmitForm
  );

  const { amount, maxWithdrawalAmount } = values;

  return (
    <CCol>
      <CForm onSubmit={handleOnSubmit}>
        <CRow>
          <CCol xs="12" md="9" xl="6">
            <CFormGroup>
              <CLabel htmlFor="authorizedUser">Requestor</CLabel>
              <CInputGroup className="input-prepend">
                <CInputGroupPrepend>
                  <CInputGroupText>
                    <CIcon name="cil-user"></CIcon>
                  </CInputGroupText>
                </CInputGroupPrepend>
                <CInput
                  type="text"
                  name="authorizedUser"
                  id="authorizedUser"
                  placeholder={auth.user.email}
                  disabled
                />
                <CInvalidFeedback>{errors.amount}</CInvalidFeedback>
              </CInputGroup>
            </CFormGroup>
          </CCol>
        </CRow>
        <CRow>
          <CCol xs="12">
            <CFormGroup>
              <CLabel htmlFor="amount">Repatriation Amount</CLabel>
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
            </CFormGroup>
          </CCol>
        </CRow>
        <CRow>
          <CCol xs="12">
            <CFormGroup>
              <CLabel htmlFor="maxWithdrawalAmount">
                Maximum Withdrawal Amount
              </CLabel>
              <CInputGroup className="input-prepend">
                <CInputGroupPrepend>
                  <CInputGroupText>$</CInputGroupText>
                </CInputGroupPrepend>
                <CInput
                  type="number"
                  id="maxWithdrawalAmount"
                  name="maxWithdrawalAmount"
                  value={maxWithdrawalAmount}
                  placeholder={0}
                  valid={errors.maxWithdrawalAmount.length === 0}
                  invalid={errors.maxWithdrawalAmount.length > 0}
                  onChange={handleOnChange}
                />
                <CInvalidFeedback>{errors.amount}</CInvalidFeedback>
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
                Submit
              </CButton>
            </CFormGroup>
          </CCol>
        </CRow>
      </CForm>
    </CCol>
  );
};

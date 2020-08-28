import React from "react";
import {
  CRow,
  CCol,
  CFormGroup,
  CLabel,
  CSelect,
  CInput,
  CButton,
  CForm
} from "@coreui/react";

export const FundsForm = ({ onSubmit }) => {
  return (
    <CCol>
      <CForm onSubmit={onSubmit}>
        <CRow>
          <CCol xs="12">
            <CFormGroup>
              <CLabel htmlFor="receivingCountry">Recieving Country</CLabel>
              <CSelect custom name="receivingCountry" id="receivingCountry">
                <option value="1">Catan</option>
                <option value="2">Freedonia</option>
                <option value="3">Utopia</option>
                <option value="4">Herzoslovaki</option>
              </CSelect>
            </CFormGroup>
          </CCol>
        </CRow>
        <CRow>
          <CCol xs="12">
            <CFormGroup>
              <CLabel htmlFor="amount">Repatriation Amount</CLabel>
              <CInput id="amount" required />
            </CFormGroup>
          </CCol>
        </CRow>
        <CRow>
          <CCol xs="12">
            <CFormGroup>
              <CLabel htmlFor="maxWithdrawalAmount">
                Maximum Withdrawal Amount
              </CLabel>
              <CInput id="maxWithdrawalAmount" required />
            </CFormGroup>
          </CCol>
        </CRow>
        <CRow>
          <CCol xs="12">
            <CFormGroup>
              <CButton className={"float-right"} color="primary" type="submit">
                Submit
              </CButton>
            </CFormGroup>
          </CCol>
        </CRow>
      </CForm>
    </CCol>
  );
};

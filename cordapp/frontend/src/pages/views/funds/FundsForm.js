import React, { useContext, useState } from "react";
import {
  CRow,
  CCol,
  CFormGroup,
  CLabel,
  CSelect,
  CButton,
  CForm,
  CInputGroup,
  CInputGroupPrepend,
  CInputGroupText,
  CSpinner,
} from "@coreui/react";
import { NetworkContext } from "../../../providers/NetworkProvider";
import useForm from "../../../form/index";
import axios from "axios";
import { APIContext } from "../../../providers/APIProvider";
import CurrencyInput from "../../../form/CurrencyInput";

export const FundsForm = ({ onSubmit }) => {
  const [api] = useContext(APIContext);
  const [network] = useContext(NetworkContext);
  const [isLoading, setIsLoading] = useState(false);

  const stateSchema = {
    receivingParty: { value: "", error: "" },
    amount: { value: 0.00, error: "" },
    maxWithdrawalAmount: { value: 0.00, error: "" },
  };

  const stateValidatorSchema = {
    receivingParty: {
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
    setIsLoading(true);
    const url =
      "http://" + window._env_.API_CLIENT_URL + ":" + api.port + "/api/fund";

    axios
      .post(url, {
        originParty: "O=US_DoJ, L=New York, C=US",
        receivingParty: state.receivingParty,
        amount: state.amount,
        maxWithdrawalAmount: state.maxWithdrawalAmount,
      })
      .then((response) => {
        onSubmit(response.data);
        setIsLoading(false);
      })
      .catch((err) => console.log(err));
  };

  const getTreasuryNodes = () => {
    if (network.length > 0) {
      return network.filter((node) => node.includes("_Treasury"));
    }
  };

  const { errors, handleOnChange, handleOnSubmit, disable } = useForm(
    stateSchema,
    stateValidatorSchema,
    onSubmitForm
  );

  const treasuryNodes = getTreasuryNodes();

  return (
    <>
      <CCol>
        <CForm onSubmit={handleOnSubmit}>
          <CRow>
            <CCol xs="12">
              <CFormGroup>
                <CLabel htmlFor="receivingParty">Receiving Country</CLabel>
                <CSelect
                  custom
                  name="receivingParty"
                  id="receivingParty"
                  onChange={handleOnChange}
                >
                  <option placeholder={0}></option>
                  {treasuryNodes
                    ? treasuryNodes.map((item) => (
                        <option
                          key={item}
                          label={item
                            .split(/O=([a-zA-Z_]+)/)[1]
                            .replace("_", " ")}
                          value={item}
                        />
                      ))
                    : null}
                </CSelect>
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
                  <CurrencyInput
                    className="form-control"
                    placeholder="0.00"
                    type="text"
                    name="maxWithdrawalAmount"
                    id="maxWithdrawalAmount"
                    onChange={handleOnChange}
                  />
                </CInputGroup>
                <div className="text-muted small">
                  <p className="text-danger">{errors.maxWithdrawalAmount}</p>
                </div>
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
    </>
  );
};

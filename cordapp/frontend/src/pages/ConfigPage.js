import React, { useState, useContext } from "react";
import {
  CButton,
  CButtonGroup,
  CCard,
  CCardBody,
  CCardHeader,
  CCallout,
  CModal,
  CModalHeader,
  CModalBody,
  CForm,
  CFormGroup,
  CLabel,
  CInputGroup,
  CInputGroupPrepend,
  CInputGroupText,
  CModalFooter,
  CSpinner,
  CDataTable,
} from "@coreui/react";
import axios from "axios";
import { APIContext } from "../providers/APIProvider";
import CurrencyInput from "../form/CurrencyInput";
import useForm from "../form/index";
import { useAuth } from "../auth-hook";
import { ConfigContext } from "../providers/ConfigProvider";
import { toCurrency } from "../utilities";


const ConfigPage = () => {
  const auth = useAuth();
  const [api] = useContext(APIContext);
  const [configState, configCallback] = useContext(ConfigContext);
  const [show, setShow] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const handleShow = () => {
    setShow(true);
  };

  const handleClose = () => {
    setShow(false);
  };

  const stateSchema = {
    maxWithdrawalAmount: { value: 0, error: "" },
  };

  const stateValidatorSchema = {
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
      "http://" + window._env_.API_CLIENT_URL + ":" + api.port + "/api/config";

    axios
      .post(url, {
        creator: auth.user.fullName,
        country: "United States",
        maxWithdrawalAmount: state.maxWithdrawalAmount,
      })
      .then(() => {
        configCallback();
        setIsLoading(false);
        setShow(false);
      })
      .catch((err) => console.log(err));
  };

  const { values, errors, handleOnChange, handleOnSubmit, disable } = useForm(
    stateSchema,
    stateValidatorSchema,
    onSubmitForm
  );

  const { maxWithdrawalAmount } = values;

  return (
    <>
      <CCard>
        <CCardHeader>
          <CCallout className="float-left mt-1 mb-1" color="secondary">
            <h4 className="mt-1 text-dark">Global Configuration</h4>
          </CCallout>
          <div className="card-header-actions">
            <CButtonGroup className="float-right mt-1 mb-1">
              <CButton
                className={"float-right mb-0"}
                color={"primary"}
                tabIndex="0"
                onClick={() => {
                  handleShow();
                }}
              >
                Update
              </CButton>
            </CButtonGroup>
          </div>
        </CCardHeader>
        <CCardBody>
          <CDataTable
            items={configState.data}
            columnFilter
            itemsPerPage={10}
            sorter
            pagination
            scopedSlots={{
              maxWithdrawalAmount: (item) => (
                <td>{toCurrency(item.maxWithdrawalAmount, "USD")}</td>
              ),
            }}
          />
        </CCardBody>
      </CCard>
      <CModal show={show} onClose={handleClose} closeOnBackdrop={false}>
        <CForm onSubmit={handleOnSubmit}>
          <CModalHeader>Create Global Configuration</CModalHeader>
          <CModalBody>
            <CFormGroup>
              <CLabel htmlFor="maxWithdrawalAmount">
                Max Withdrawal Amount
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
                  value={maxWithdrawalAmount}
                  onChange={handleOnChange}
                />
              </CInputGroup>
              <div className="text-muted small">
                <p className="text-danger">{errors.amount}</p>
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
    </>
  );
};

export default ConfigPage;

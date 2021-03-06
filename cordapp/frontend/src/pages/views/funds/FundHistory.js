import React, { useState, useContext, useEffect } from "react";
import {
  CButton,
  CModal,
  CModalHeader,
  CModalTitle,
  CModalBody,
  CModalFooter,
  CDataTable,
  CBadge,
  CCallout,
} from "@coreui/react";
import axios from "axios";
import { APIContext } from "../../../providers/APIProvider";
import { toCurrency } from "../../../utilities";
import moment from "moment-timezone";
import * as Constants from "../../../constants";

export const FundHistory = ({ show, fundId, handleClose }) => {
  const [api] = useContext(APIContext);
  const [requestHistory, setRequestHistory] = useState();
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (fundId) {
      setIsLoading(true);
      const url =
        "http://" +
        window._env_.API_CLIENT_URL +
        ":" +
        api.port +
        "/api/fund/all?fundId=" +
        fundId;

      axios
        .get(url)
        .then((response) => {
          setRequestHistory(response.data.entity);
          setIsLoading(false);
        })
        .catch((err) => {
          console.log(err);
          setIsLoading(false);
        });
    }
  }, [api.port, fundId]);

  const fields = [
    { key: "status", _style: { width: "10%" } },
    { key: "updateDateTime", label: "Update Date/Time" },
    { key: "amount" },
    { key: "balance" },
  ];

  const getStatusBadge = (status) => {
    switch (status) {
      case "PAID":
        return "light";
      case "RECEIVED":
        return "success";
      case "ISSUED":
        return "warning";
      default:
        return "primary";
    }
  };

  return (
    <CModal show={show} onClose={handleClose} size="lg" closeOnBackdrop={true}>
      <CModalHeader closeButton>
        <CModalTitle>Return History</CModalTitle>
      </CModalHeader>
      <CModalBody>
        <CCallout color="info" className={"bg-light"}>
          <p className="text-muted mb-0">Return ID</p>
          <strong className="p">{fundId}</strong>
        </CCallout>
        <CCallout color="info" className={"bg-light"}>
          <p className="text-muted mb-0">Created Date/Time</p>
          <strong className="p">
            {requestHistory
              ? moment
                  .tz(requestHistory[0].createDatetime, "UTC")
                  .format(Constants.DATETIME_FORMAT)
              : null}
          </strong>
        </CCallout>

        <CDataTable
          items={requestHistory ? requestHistory : null}
          fields={fields}
          itemsPerPage={5}
          hover
          pagination
          loading={isLoading ? true : false}
          scopedSlots={{
            amount: (item) => <td>{toCurrency(item.amount, item.currency)}</td>,
            balance: (item) => <td>{toCurrency(item.balance, item.currency)}</td>,
            updateDateTime: (item) => (
              <td>
                {moment
                  .tz(item.updateDatetime, "UTC")
                  .format(Constants.DATETIME_FORMAT)}
              </td>
            ),
            status: (item) => (
              <td>
                <CBadge color={getStatusBadge(item.status)}>
                  {item.status}
                </CBadge>
              </td>
            ),
          }}
        />
      </CModalBody>
      <CModalFooter>
        <CButton color="secondary" onClick={handleClose}>
          Close
        </CButton>
      </CModalFooter>
    </CModal>
  );
};

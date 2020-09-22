import React, {
  createContext,
  useEffect,
  useReducer,
  useCallback,
  useContext,
} from "react";
import getRequests from "../data/GetRequests";
import { APIContext } from "./APIProvider";
import * as Constants from "../constants";
import { addAmounts } from "../utilities";

export const RequestsContext = createContext();

const initialState = {
  data: [],
  pending: [],
  approved: [],
  transferred: [],
  pendingAmount: 0,
  approvedAmount: 0,
  transferredAmount: 0,
  loading: true,
};

const reducer = (state, action) => {
  switch (action.type) {
    case "UPDATE_REQUESTS":
      const pending = action.payload.filter(
        (request) => request.status === Constants.REQUEST_PENDING
      );
      const approved = action.payload.filter(
        (request) => request.status === Constants.REQUEST_APPROVED
      );
      const transferred = action.payload.filter(
        (request) => request.status === Constants.REQUEST_TRANSFERRED
      );
      return {
        ...state,
        data: action.payload,
        pending: pending,
        approved: approved,
        transferred: transferred,
        pendingAmount: addAmounts(pending),
        approvedAmount: addAmounts(approved),
        transferredAmount: addAmounts(transferred),
        loading: false,
      };
    default:
      return state;
  }
};

const RequestsProvider = ({ children, authorizedUser }) => {
  const [state, dispatch] = useReducer(reducer, initialState);
  const [api] = useContext(APIContext);

  const callback = useCallback(
    () => {
      if (authorizedUser.isAuthenticated && authorizedUser.meta.keycloak.hasResourceRole("funds_requestor")) {
        getRequests(api.port).then((data) =>
          dispatch({
            type: "UPDATE_REQUESTS",
            payload: data.filter(
              (request) =>
                request.authorizedUserDept ===
                authorizedUser.meta.keycloak.tokenParsed.groups[0]
            ),
          })
        );
      } else {
        getRequests(api.port).then((data) =>
          dispatch({
            type: "UPDATE_REQUESTS",
            payload: data,
          })
        );
      }
    },
    [dispatch, authorizedUser.isAuthenticated, authorizedUser.meta.keycloak, api.port]
  );

  useEffect(() => {
    if (api.port) {
      if (authorizedUser.isAuthenticated && authorizedUser.meta.keycloak.hasResourceRole("funds_requestor")) {
        getRequests(api.port).then((data) =>
          dispatch({
            type: "UPDATE_REQUESTS",
            payload: data.filter(
              (request) =>
                request.authorizedUserDept ===
                authorizedUser.meta.keycloak.tokenParsed.groups[0]
            ),
          })
        );
      } else {
        getRequests(api.port).then((data) =>
          dispatch({
            type: "UPDATE_REQUESTS",
            payload: data,
          })
        );
      }
    }
  }, [api.port, authorizedUser.isAuthenticated, authorizedUser.meta.keycloak]);

  return (
    <RequestsContext.Provider value={[state, callback]}>
      {children}
    </RequestsContext.Provider>
  );
};

export default RequestsProvider;

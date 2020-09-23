import React, {
  createContext,
  useEffect,
  useReducer,
  useCallback,
  useContext,
} from "react";
import getTransfers from "../data/GetTransfers";
import { APIContext } from "./APIProvider";
import { addAmounts } from "../utilities";
import  useInterval  from "../interval-hook";
import * as Constants from "../constants";

export const TransfersContext = createContext();

const initialState = {
  data: [],
  amount: [],
  loading: true,
};

const reducer = (state, action) => {
  switch (action.type) {
    case "UPDATE_TRANSFERS":
      const data = action.payload;
      return {
        data: data,
        amount: addAmounts(data),
        loading: false,
      };
    default:
      return state;
  }
};

const TransfersProvider = ({ children, authorizedUser }) => {
  const [state, dispatch] = useReducer(reducer, initialState);
  const [api] = useContext(APIContext);

  const callback = useCallback(() => {
    if (
      authorizedUser.isAuthenticated &&
      authorizedUser.meta.keycloak.hasResourceRole("funds_requestor")
    ) {
      getTransfers(api.port).then((data) =>
        dispatch({
          type: "UPDATE_TRANSFERS",
          payload: data.filter(
            (transfer) =>
              transfer.receivingDept ===
              authorizedUser.meta.keycloak.tokenParsed.groups[0]
          ),
        })
      );
    } else {
      getTransfers(api.port).then((data) =>
        dispatch({ type: "UPDATE_TRANSFERS", payload: data })
      );
    }
  }, [dispatch, authorizedUser.isAuthenticated, authorizedUser.meta.keycloak, api.port]);

  useInterval(() => {
    callback();
  }, Constants.REFRESH_INTERVAL_MS);

  useEffect(() => {
    if (api.port) {
      if (
        authorizedUser.isAuthenticated &&
        authorizedUser.meta.keycloak.hasResourceRole("funds_requestor")
      ) {
        getTransfers(api.port).then((data) =>
          dispatch({
            type: "UPDATE_TRANSFERS",
            payload: data.filter(
              (transfer) =>
                transfer.receivingDept ===
                authorizedUser.meta.keycloak.tokenParsed.groups[0]
            ),
          })
        );
      } else {
        getTransfers(api.port).then((data) =>
          dispatch({ type: "UPDATE_TRANSFERS", payload: data })
        );
      }
    }
  }, [api.port, authorizedUser.isAuthenticated, authorizedUser.meta.keycloak]);

  return (
    <TransfersContext.Provider value={[state, callback]}>
      {children}
    </TransfersContext.Provider>
  );
};

export default TransfersProvider;

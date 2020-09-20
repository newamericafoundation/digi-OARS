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
  loading: true,
};

const reducer = (state, action) => {
  switch (action.type) {
    case "UPDATE_REQUESTS":
      const pending = action.payload.filter((request) => request.status === Constants.REQUEST_PENDING);
      const approved = action.payload.filter((request) => request.status === Constants.REQUEST_APPROVED);
      const transferred = action.payload.filter((request) => request.status === Constants.REQUEST_TRANSFERRED);
      return {
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

const RequestsProvider = ({ children }) => {
  const [state, dispatch] = useReducer(reducer, initialState);
  const [api] = useContext(APIContext);

  const callback = useCallback(
    () =>
      getRequests(api.port).then((data) =>
        dispatch({ type: "UPDATE_REQUESTS", payload: data })
      ),
    [dispatch, api.port]
  );

  useEffect(() => {
    if (api.port) {
      getRequests(api.port).then((data) =>
        dispatch({ type: "UPDATE_REQUESTS", payload: data })
      );
    }
  }, [api.port]);

  return (
    <RequestsContext.Provider value={[state, callback]}>
      {children}
    </RequestsContext.Provider>
  );
};

export default RequestsProvider;

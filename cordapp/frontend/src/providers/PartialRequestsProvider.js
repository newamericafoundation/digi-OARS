import React, {
  createContext,
  useEffect,
  useReducer,
  useCallback,
  useContext,
} from "react";
import getPartialRequests from "../data/GetPartialRequests";
import { APIContext } from "./APIProvider";
import { addAmounts } from "../utilities";
import  useInterval  from "../interval-hook";
import * as Constants from "../constants";

export const PartialRequestsContext = createContext();

const initialState = {
  data: [],
  totalAmount: 0,
  loading: true,
};

const reducer = (state, action) => {
  switch (action.type) {
    case "UPDATE_PARTIAL_REQUESTS":
      return {
        data: action.payload,
        totalAmount: addAmounts(action.payload),
        loading: false,
      };
    default:
      return state;
  }
};

const PartialRequestsProvider = ({ children }) => {
  const [state, dispatch] = useReducer(reducer, initialState);
  const [api] = useContext(APIContext);

  const callback = useCallback(
    () =>
      getPartialRequests(api.port).then((data) =>
        dispatch({ type: "UPDATE_PARTIAL_REQUESTS", payload: data })
      ),
    [dispatch, api.port]
  );

  useInterval(() => {
    callback();
  }, Constants.REFRESH_INTERVAL_MS);

  useEffect(() => {
    if (api.port) {
      getPartialRequests(api.port).then((data) =>
        dispatch({ type: "UPDATE_PARTIAL_REQUESTS", payload: data })
      );
    }
  }, [api.port]);

  return (
    <PartialRequestsContext.Provider value={[state, callback]}>
      {children}
    </PartialRequestsContext.Provider>
  );
};

export default PartialRequestsProvider;

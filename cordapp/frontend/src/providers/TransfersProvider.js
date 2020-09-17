import React, {
  createContext,
  useEffect,
  useReducer,
  useCallback,
  useContext,
} from "react";
import getTransfers from "../data/GetTransfers";
import { APIContext } from "./APIProvider";

export const TransfersContext = createContext();

const initialState = {
  data: [],
  loading: true,
};

const reducer = (state, action) => {
  switch (action.type) {
    case "UPDATE_TRANSFERS":
      return {
        data: action.payload,
        loading: false,
      };
    default:
      return state;
  }
};

const TransfersProvider = ({ children }) => {
  const [state, dispatch] = useReducer(reducer, initialState);
  const [api] = useContext(APIContext);

  const callback = useCallback(
    () =>
      getTransfers(api.port).then((data) =>
        dispatch({ type: "UPDATE_TRANSFERS", payload: data })
      ),
    [dispatch, api.port]
  );

  useEffect(() => {
    if (api.port) {
      getTransfers(api.port).then((data) =>
        dispatch({ type: "UPDATE_TRANSFERS", payload: data })
      );
    }
  }, [api.port]);

  return (
    <TransfersContext.Provider value={[state, callback]}>
      {children}
    </TransfersContext.Provider>
  );
};

export default TransfersProvider;

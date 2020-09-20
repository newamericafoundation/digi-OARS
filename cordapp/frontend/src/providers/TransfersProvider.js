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

export const TransfersContext = createContext();

const initialState = {
  data: [],
  amount: [],
  loading: true,
};

const reducer = (state, action) => {
  switch (action.type) {
    case "UPDATE_TRANSFERS":
      const data = action.payload
      return {
        data: data,
        amount: addAmounts(data),
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

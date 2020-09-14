import React, {
  createContext,
  useEffect,
  useReducer,
  useCallback,
  useContext
} from "react";
import getFunds from "../data/GetFunds";
import { APIContext } from "./APIProvider";

export const FundsContext = createContext();

const initialState = {
  data: [],
  loading: true,
};

const reducer = (state, action) => {
  switch (action.type) {
    case "UPDATE_FUNDS":
      return {
        data: action.payload,
        loading: false,
      };
    default:
      return state;
  }
};

const FundsProvider = ({ children }) => {
  const [state, dispatch] = useReducer(reducer, initialState);
  const [api] = useContext(APIContext);

  const callback = useCallback(
    () =>
      getFunds(api.port).then((data) =>
        dispatch({ type: "UPDATE_FUNDS", payload: data })
      ),
    [dispatch, api.port]
  );

  useEffect(() => {
    if (api.port) {
      getFunds(api.port).then((data) =>
        dispatch({ type: "UPDATE_FUNDS", payload: data })
      );
    }
  }, [api.port]);

  return (
    <FundsContext.Provider value={[state, callback]}>
      {children}
    </FundsContext.Provider>
  );
};

export default FundsProvider;

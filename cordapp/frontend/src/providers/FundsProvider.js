import React, {
  createContext,
  useEffect,
  useReducer,
  useCallback,
  useContext
} from "react";
import getFunds from "../data/GetFunds";
import { APIContext } from "./APIProvider";
import * as Constants from "../constants";


export const FundsContext = createContext();

const initialState = {
  data: [],
  issued: [],
  received: [],
  issueAmount: 0,
  receivedAmount: 0,
  loading: true,
};

const reducer = (state, action) => {
  switch (action.type) {
    case "UPDATE_FUNDS":
      const issued = action.payload.filter((fund) => fund.status === Constants.FUND_ISSUED)
      const received = action.payload.filter((fund) => fund.status === Constants.FUND_RECEIVED)
      return {
        data: action.payload,
        issued: issued,
        received: received,
        issuedAmount: issued.reduce((total, fund) => total + parseFloat(fund.amount), 0),
        receivedAmount: received.reduce((total, fund) => total + parseFloat(fund.amount), 0),
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

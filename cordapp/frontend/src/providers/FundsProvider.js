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
import { addAmounts } from "../utilities";

export const FundsContext = createContext();

const initialState = {
  data: [],
  issued: [],
  received: [],
  paid: [],
  issuedAmount: 0,
  receivedAmount: 0,
  paidAmount: 0,
  loading: true,
};

const reducer = (state, action) => {
  switch (action.type) {
    case "UPDATE_FUNDS":
      const issued = action.payload.filter((fund) => fund.status === Constants.FUND_ISSUED)
      const received = action.payload.filter((fund) => fund.status === Constants.FUND_RECEIVED)
      const paid = action.payload.filter((fund) => fund.status === Constants.FUND_PAID)
      return {
        data: action.payload,
        issued: issued,
        received: received,
        issuedAmount: addAmounts(issued),
        receivedAmount: addAmounts(received),
        paidAmount: addAmounts(paid),
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

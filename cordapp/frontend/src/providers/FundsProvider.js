import React, {
  createContext,
  useEffect,
  useReducer,
  useCallback,
} from "react";
import getFunds from "../data/GetFunds";

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

  const callback = useCallback(
    () =>
      getFunds().then((data) => dispatch({ type: "UPDATE_FUNDS", payload: data })),
    [dispatch]
  );

  useEffect(() => {
    getFunds().then((data) => dispatch({ type: "UPDATE_FUNDS", payload: data }));
  }, []);

  return (
    <FundsContext.Provider value={[state, callback]}>
      {children}
    </FundsContext.Provider>
  );
};

export default FundsProvider;

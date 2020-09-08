import React, {
  createContext,
  useEffect,
  useReducer,
  useCallback,
} from "react";
import getFunds from "../data/GetFunds";

export const RequestsContext = createContext();

const initialState = {
  data: [],
  loading: true,
};

const reducer = (state, action) => {
  switch (action.type) {
    case "UPDATE_REQUESTS":
      return {
        data: action.payload,
        loading: false,
      };
    default:
      return state;
  }
};

const RequestsProvider = ({ children }) => {
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
    <RequestsContext.Provider value={[state, callback]}>
      {children}
    </RequestsContext.Provider>
  );
};

export default RequestsProvider;

import React, {
  createContext,
  useEffect,
  useReducer,
  useCallback,
} from "react";
import getRequests from "../data/GetRequests";

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
    getRequests().then((data) => dispatch({ type: "UPDATE_REQUESTS", payload: data })),
    [dispatch]
  );

  useEffect(() => {
    getRequests().then((data) => dispatch({ type: "UPDATE_REQUESTS", payload: data }));
  }, []);

  return (
    <RequestsContext.Provider value={[state, callback]}>
      {children}
    </RequestsContext.Provider>
  );
};

export default RequestsProvider;

import React, {
  createContext,
  useEffect,
  useReducer,
  useCallback,
  useContext,
} from "react";
import getRequests from "../data/GetRequests";
import { APIContext } from "./APIProvider";

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

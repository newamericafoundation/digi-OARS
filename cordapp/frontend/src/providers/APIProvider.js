import React, {
  createContext,
  useReducer,
  useEffect,
} from "react";

export const APIContext = createContext();

const initialState = {
  port: "",
  loading: true,
};

const reducer = (state, action) => {
  switch (action.type) {
    case "SET_PORT":
      return {
        port: action.port,
        loading: false,
      };
    default:
      return state;
  }
};

const APIProvider = ({ children, port }) => {
  const [state, dispatch] = useReducer(reducer, initialState);

  useEffect(() => {
    if (port) {
      dispatch({ type: "SET_PORT", port: port });
    }
    
  }, [port]);

  return <APIContext.Provider value={[state]}>{children}</APIContext.Provider>;
};

export default APIProvider;

import React, {
  createContext,
  useEffect,
  useReducer,
  useCallback,
  useContext,
} from "react";
import getConfig from "../data/GetConfig";
import { APIContext } from "./APIProvider";

export const ConfigContext = createContext();

const initialState = {
  data: [],
  loading: true,
};

const reducer = (state, action) => {
  switch (action.type) {
    case "GET_CONFIG":
      return {
        data: action.payload,
        loading: false,
      };
    default:
      return state;
  }
};

const ConfigProvider = ({ children }) => {
  const [state, dispatch] = useReducer(reducer, initialState);
  const [api] = useContext(APIContext);


  const callback = useCallback(
    () => {
      getConfig(api.port).then((data) => {
        dispatch({ type: "GET_CONFIG", payload: data });
      });
    },
    [dispatch, api.port],
  )

  useEffect(() => {
    if (api.port) {
      getConfig(api.port).then((data) => {
        dispatch({ type: "GET_CONFIG", payload: data });
      });
    }
  }, [api.port]);

  return (
    <ConfigContext.Provider value={[state, callback]}>
      {children}
    </ConfigContext.Provider>
  );
};

export default ConfigProvider;

import React, { createContext, useState, useEffect, useContext } from "react";
import axios from "axios";
import { APIContext } from "./APIProvider";

export const NetworkContext = createContext(null);

const { Provider } = NetworkContext;

const NetworkProvider = ({ children, port }) => {
  const [state, setState] = useState([]);
  const [api] = useContext(APIContext);
  useEffect(() => {
    try {
      if (api.port) {
        const url =
          "http://" + window._env_.API_CLIENT_URL + ":" + api.port + "/api/network";
          axios.get(url).then((response) => setState(response.data));
      }
    } catch (error) {
      console.log(error);
    }
  }, [api.port]);
  return <Provider value={[state, setState]}>{children}</Provider>;
};

NetworkProvider.context = NetworkContext;

export default NetworkProvider;

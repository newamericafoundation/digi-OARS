import React, { createContext, useState, useEffect } from "react";
import axios from "axios";

export const NetworkContext = createContext(null);

const { Provider } = NetworkContext;

const NetworkProvider = ({ children }) => {
  const [state, setState] = useState([]);

  useEffect(() => {
    const url =
      "http://" +
      window._env_.API_CLIENT_URL +
      ":" +
      window._env_.API_CLIENT_PORT +
      "/api/network";

    try {
      axios.get(url).then((response) => setState(response.data));
    } catch (error) {
      console.log(error);
    }
  }, []);
  return <Provider value={[state, setState]}>{children}</Provider>;
};

NetworkProvider.context = NetworkContext;

export default NetworkProvider;

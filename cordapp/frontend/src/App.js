import React, { useEffect, useState } from "react";
import "./scss/style.scss";
import { AppRouter } from "./routes/index";
import FundsProvider from "./providers/FundsProvider";
import RequestsProvider from "./providers/RequestsProvider";
import ReactNotification from "react-notifications-component";
import APIProvider from "providers/APIProvider";
import { useAuth } from "./auth-hook";
import TransfersProvider from "./providers/TransfersProvider";
import PartialRequestsProvider from "./providers/PartialRequestsProvider";

const App = () => {
  const auth = useAuth();
  const [apiPort, setApiPort] = useState();

  useEffect(() => {
    if (auth.isAuthenticated) {
      setApiPort(auth.meta.keycloak.tokenParsed.port);
    }
  }, [auth]);

  return (
    <APIProvider port={apiPort}>
      <FundsProvider>
        <RequestsProvider authorizedUser={auth}>
          <TransfersProvider authorizedUser={auth}>
            <PartialRequestsProvider>
              <div className="App">
                <ReactNotification />
                <AppRouter />
              </div>
            </PartialRequestsProvider>
          </TransfersProvider>
        </RequestsProvider>
      </FundsProvider>
    </APIProvider>
  );
};

export default App;

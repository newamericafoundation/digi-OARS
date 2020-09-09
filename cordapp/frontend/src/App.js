import React from "react";
import "./scss/style.scss";
import { KeycloakProvider } from "@react-keycloak/web";
import keycloak from "keycloak";
import { AppRouter } from "./routes/index";
import FundsProvider from "./providers/FundsProvider";
import RequestsProvider from "./providers/RequestsProvider";
import ReactNotification from "react-notifications-component";

function App() {
  return (
    <FundsProvider>
      <RequestsProvider>
        <KeycloakProvider keycloak={keycloak}>
          <div className="App">
            <ReactNotification />
            <AppRouter />
          </div>
        </KeycloakProvider>
      </RequestsProvider>
    </FundsProvider>
  );
}

export default App;

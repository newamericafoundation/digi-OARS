import React from "react";
import "./scss/style.scss";
import { KeycloakProvider } from "@react-keycloak/web";
import keycloak from "keycloak";
import { AppRouter } from "./routes/index";
import FundsProvider from "./providers/FundsProvider";

function App() {
  return (
    <FundsProvider>
      <KeycloakProvider keycloak={keycloak}>
        <div className="App">
          <AppRouter />
        </div>
      </KeycloakProvider>
    </FundsProvider>
  );
}

export default App;

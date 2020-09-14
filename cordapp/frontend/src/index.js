import React from "react";
import ReactDOM from "react-dom";
import "./index.css";
import App from "./App";
import * as serviceWorker from "./serviceWorker";
import { Provider } from "react-redux";
import store from "./store";
import { icons } from "./assets/icons";
import { KeycloakProvider } from "@react-keycloak/web";
import keycloak from "keycloak";

React.icons = icons;

ReactDOM.render(
  <Provider store={store}>
    <KeycloakProvider keycloak={keycloak}>
      <App />
    </KeycloakProvider>
  </Provider>,
  document.getElementById("root")
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();

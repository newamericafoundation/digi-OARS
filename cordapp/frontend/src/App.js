import React from 'react';
import './scss/style.scss';
import { KeycloakProvider } from '@react-keycloak/web';
import keycloak from 'keycloak';
import { AppRouter } from './routes/index';

function App() {
  return (
    <KeycloakProvider keycloak={keycloak}>
      <div className="App">
        <AppRouter />
      </div>
    </KeycloakProvider>
  );
}

export default App;

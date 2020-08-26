import React from 'react';
// import logo from './logo.svg';
import './scss/style.scss';
// import './App.css';
import { KeycloakProvider } from '@react-keycloak/web';
import keycloak from 'keycloak';
import { AppRouter } from './routes/index';

// function App() {
//   return (
//     <KeycloakProvider keycloak={keycloak}>
//     <div className="App">
//       <header className="App-header">
//         <img src={logo} className="App-logo" alt="logo" />
        
//         <p>
//           Edit <code>src/App.js</code> and save to reload.
//         </p>
//         <a
//           className="App-link"
//           href="https://reactjs.org"
//           target="_blank"
//           rel="noopener noreferrer"
//         >
//           Learn React
//         </a>
//       </header>
//     </div>
//     </KeycloakProvider>
//   );
// }

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

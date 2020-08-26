import Keycloak from 'keycloak-js';

const keycloakConfig = {
   url: "http://" + window._env_.KEYCLOAK_URL + ":" + window._env_.KEYCLOAK_PORT + "/auth",
   realm: window._env_.KEYCLOAK_REALM,
   clientId: window._env_.KEYCLOAK_CLIENT_ID
}

const keycloak = new Keycloak(keycloakConfig);

export default keycloak;
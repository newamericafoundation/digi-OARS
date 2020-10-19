import React from "react";
import { useAuth } from "../auth-hook";
import { Actions } from "../pages/views/home/Actions";
import { Jumbotron } from "../pages/views/home/Jumbotron";

const HomePage = () => {
  const auth = useAuth();

  return (
    <>
      {auth.meta.keycloak.hasResourceRole("funds_receiver") ||
      auth.meta.keycloak.hasResourceRole("request_approver") ? (
        <Actions auth={auth} />
      ) : null}
      <Jumbotron auth={auth} />
    </>
  );
};

export default HomePage;

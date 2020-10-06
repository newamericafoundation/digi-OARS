import React from "react";
import { useAuth } from "../auth-hook";
import { Actions } from "../pages/views/home/Actions";
import { Widgets } from "../pages/views/home/Widgets";
import { Jumbotron } from "../pages/views/home/Jumbotron";
import { WelcomeCallout } from "../pages/views/home/WelcomeCallout";

const HomePage = () => {
  const auth = useAuth();

  return (
    <>
      <Jumbotron auth={auth} />
      <WelcomeCallout auth={auth} />
      {auth.meta.keycloak.hasResourceRole("funds_receiver") ? (
        <Actions />
      ) : null}
      <Widgets auth={auth} />
    </>
  );
};

export default HomePage;

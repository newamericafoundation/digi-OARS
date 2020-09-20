import React from "react";
import { useAuth } from "../auth-hook";
import { Widgets } from "../pages/views/home/Widgets";
import { Jumbotron } from "../pages/views/home/Jumbotron";
import { WelcomeCallout } from "../pages/views/home/WelcomeCallout";

const HomePage = () => {
  const auth = useAuth();

  return (
    <>
      <Jumbotron auth={auth} />
      <WelcomeCallout auth={auth} />
      <Widgets auth={auth} />
    </>
  );
};

export default HomePage;

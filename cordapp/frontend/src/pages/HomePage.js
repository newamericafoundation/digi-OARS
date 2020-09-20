import React from "react";
import { useAuth } from "../auth-hook";
import { Widgets } from "../pages/views/home/Widgets";
import { Jumbotron } from "../pages/views/home/Jumbotron";

const HomePage = () => {
  const auth = useAuth();

  return (
    <>
      <Jumbotron auth={auth} />
      <Widgets auth={auth} />
    </>
  );
};

export default HomePage;

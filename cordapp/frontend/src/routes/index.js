import React from "react";
import { useKeycloak } from "@react-keycloak/web";
import { Route, Switch, BrowserRouter } from "react-router-dom";

const loading = (
  <div className="pt-3 text-center">
    <div className="sk-spinner sk-spinner-pulse"></div>
  </div>
);

const Layout = React.lazy(() => import("../containers/Layout"));
// const Content = React.lazy(() => import("../containers/Content"));
// const Login = React.lazy(() => import("../pages/views/login/Login"));

export const AppRouter = () => {
  const [initialized] = useKeycloak();

  if (!initialized) {
    return loading;
  }

  return (
    <BrowserRouter>
      <React.Suspense fallback={loading}>
        <Switch>
          <Route
            path="/"
            name="Home"
            render={(props) => <Layout {...props} />}
          />
        </Switch>
      </React.Suspense>
    </BrowserRouter>
  );
};

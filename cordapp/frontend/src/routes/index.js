import React from "react";
import { useKeycloak } from "@react-keycloak/web";
import { Route, Switch, BrowserRouter } from "react-router-dom";
import { publicRoutes, privateRoutes } from "../routes";

const loading = (
  <div className="pt-3 text-center">
    <div className="sk-spinner sk-spinner-pulse"></div>
  </div>
);

const Layout = React.lazy(() => import("../containers/Layout"));
// const Login = React.lazy(() => import('../views/Login'));

const routes = [...publicRoutes, ...privateRoutes];

export const AppRouter = () => {
  const [initialized] = useKeycloak();

  if (!initialized) {
    return loading;
  }

  return (
    <BrowserRouter>
      <React.Suspense fallback={loading}>
        <Switch>
          {routes.map((route, idx) => (
              route.component &&
            <Route
              key={idx}
              path={route.path}
              name={route.name}
              exact={route.exact}
              render={() => <Layout {...route.component} />}
            />
          ))}
          {/* <Route path="/login" name="Login" render={props => <Login {...props} />}/> */}
        </Switch>
      </React.Suspense>
    </BrowserRouter>
  );
};

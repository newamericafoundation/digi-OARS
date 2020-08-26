import React from 'react';
import { useKeycloak } from '@react-keycloak/web';
import { Route, Switch, BrowserRouter } from 'react-router-dom';

const loading = (
    <div className="pt-3 text-center">
        <div className="sk-spinner sk-spinner-pulse"></div>
    </div>
)

const Layout = React.lazy(() => import('../containers/Layout'));
// const Login = React.lazy(() => import('../views/Login'));

export const AppRouter = () => {
    const [initialized] = useKeycloak()

    if (!initialized) {
        return loading
    }
    
    return (
        <BrowserRouter>
            <React.Suspense fallback={loading}>
                <Switch>
                    <Route path="/" name="Home" render={props => <Layout {...props} />}></Route>
                    {/* <Route path="/login" name="Login" render={props => <Login {...props} />}/> */}
                </Switch>
            </React.Suspense>
        </BrowserRouter>
    );
}
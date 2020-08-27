import React, { Suspense } from 'react'
import {
  Redirect,
  Route,
  Switch
} from 'react-router-dom'
import { CContainer, CFade } from '@coreui/react'
import { useKeycloak } from '@react-keycloak/web';

import {publicRoutes} from '../routes'
  
const loading = (
  <div className="pt-3 text-center">
    <div className="sk-spinner sk-spinner-pulse"></div>
  </div>
)

const Content = () => {
  const [keycloak] = useKeycloak();
  
  const isAuthorised = (roles) => {
    if (keycloak && roles) {
        return roles.some(r => {
            const realm =  keycloak.hasRealmRole(r);
            const resource = keycloak.hasResourceRole(r);
            return realm || resource;
        });
    }
    return false;
  }

  return (
    <main className="c-main">
      <CContainer fluid>
        <Suspense fallback={loading}>
          <Switch>
            {publicRoutes.map((route, idx) => {
              return route.component && (
                <Route
                  key={idx}
                  path={route.path}
                  name={route.name}
                  exact={route.exact}
                  render={props => {
                    return <CFade><route.component {...props} /></CFade>
                  }}
                  />
                  
              )
            })}
            {/* {privateRoutes.map((route, idx) => {
              return route.component && (
                <Route
                  key={idx}
                  path={route.path}
                  name={route.name}
                  exact={route.exact}
                  render={props => {
                    return isAuthorised(route.roles) ? <CFade><route.component {...props} /></CFade> : <Redirect to="/" />
                  }}
                  />    
              )
            })} */}
          </Switch>
        </Suspense>
      </CContainer>
    </main>
  )
}

export default React.memo(Content)

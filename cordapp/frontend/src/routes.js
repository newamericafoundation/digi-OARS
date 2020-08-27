import React from 'react';

const HomePage = React.lazy(() => import('./pages/HomePage'));
// const SecuredPage = React.lazy(() => import('./views/SecuredPage'));
// const Login = React.lazy(() => import('./views/Login'));

// export const privateRoutes = [
//   { roles: ['RealmAdmin','RealmUser'], path: '/secure', name: 'SecuredPage', component: SecuredPage },
// ]
export const publicRoutes = [
  { path: '/', exact: true, name: 'Home', component: HomePage},
  // { path: '/login', exact: true, name: 'Login', component: Login} 
];
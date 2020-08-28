import React from 'react';

const HomePage = React.lazy(() => import('./pages/HomePage'));
const FundsPage = React.lazy(() => import('./pages/FundsPage'));

// const SecuredPage = React.lazy(() => import('./views/SecuredPage'));
// const Login = React.lazy(() => import('./views/Login'));


export const publicRoutes = [
  { path: '/', exact: true, name: 'Home', component: HomePage},
  // { path: '/login', exact: true, name: 'Login', component: Login} 
];

export const privateRoutes = [
  { roles: ['user'], path: '/funds', name: 'Funds', component: FundsPage },
]
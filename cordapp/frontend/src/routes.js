import React from 'react';

const HomePage = React.lazy(() => import('./pages/HomePage'));
const FundsPage = React.lazy(() => import('./pages/FundsPage'));
const WithdrawalsPage = React.lazy(() => import('./pages/WithdrawalsPage'));
const TransfersPage = React.lazy(() => import('./pages/TransfersPage'));
const TransfersHistoryPage = React.lazy(() => import('./pages/TransfersHistoryPage'));
const AdminPage = React.lazy(() => import('./pages/AdminPage'));

// const SecuredPage = React.lazy(() => import('./views/SecuredPage'));
// const Login = React.lazy(() => import('./views/Login'));


export const publicRoutes = [
  { path: '/', exact: true, name: 'Home', component: HomePage},
  // { path: '/login', exact: true, name: 'Login', component: Login} 
];

export const privateRoutes = [
  { roles: ['user'], path: '/funds', exact: true, name: 'Funds', component: FundsPage },
  { roles: ['user'], path: '/withdrawals', exact: true, name: 'Withdrawals', component: WithdrawalsPage },
  { roles: ['user'], path: '/transfers/approvals', exact: true, name: 'Transfers', component: TransfersPage },
  { roles: ['request_transferer'], path: '/transfers/history', exact: true, name: 'History', component: TransfersHistoryPage },
  { roles: ['admin'], path: '/admin', exact: true, name: 'Admin', component: AdminPage},
]
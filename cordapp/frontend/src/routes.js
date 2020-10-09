import React from 'react';

const HomePage = React.lazy(() => import('./pages/HomePage'));
const FundsPage = React.lazy(() => import('./pages/FundsPage'));
const WithdrawalsPage = React.lazy(() => import('./pages/WithdrawalsPage'));
const PartialWithdrawalsPage = React.lazy(() => import('./pages/PartialWithdrawalsPage'));

const TransfersPage = React.lazy(() => import('./pages/TransfersPage'));
const TransfersHistoryPage = React.lazy(() => import('./pages/TransfersHistoryPage'));
const AdminPage = React.lazy(() => import('./pages/AdminPage'));

// const Login = React.lazy(() => import('./pages/views/login/Login'));

export const publicRoutes = [
  { path: '/', exact: true, name: 'Home', component: HomePage},
  // { path: '/login', exact: true, name: 'Login', component: Login} 
];

export const privateRoutes = [
  { roles: ['user'], path: '/returns', exact: true, name: 'Returns', component: FundsPage },
  { roles: ['user', 'funds_requestor'], path: '/withdrawals', exact: true, name: 'Withdrawal Requests', component: WithdrawalsPage },
  { roles: ['partial_request_viewer'], path: '/withdrawals/partial', exact: true, name: 'Withdrawals', component: PartialWithdrawalsPage },
  { roles: ['user'], path: '/transfers/approvals', exact: true, name: 'Transfers', component: TransfersPage },
  { roles: ['user'], path: '/transfers/history', exact: true, name: 'Transfers History', component: TransfersHistoryPage },
  { roles: ['admin'], path: '/admin', exact: true, name: 'Admin', component: AdminPage},
]
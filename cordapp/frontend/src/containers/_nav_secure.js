export default [
  {
    _tag: "CSidebarNavItem",
    name: "Dashboard",
    to: "/dashboard",
    icon: "cil-speedometer",
  },
  {
    _tag: "CSidebarNavTitle",
    _children: ["Asset Repatriation"],
  },
  {
    _tag: "CSidebarNavItem",
    name: "Funds",
    to: "/funds",
    icon: "cil-bank",
  },
  {
    _tag: "CSidebarNavItem",
    name: "Withdrawals",
    to: "/withdrawals",
    icon: "cil-wallet",
  },
  {
    _tag: "CSidebarNavDropdown",
    name: "Transfers",
    icon: "cil-chevron-right",
    _children: [
      {
        _tag: 'CSidebarNavItem',
        name: 'Requests Awaiting Transfer',
        to: '/transfers/approvals',
      },
      {
        _tag: 'CSidebarNavItem',
        name: 'History',
        to: '/transfers/history',
      }
    ]
  }
];

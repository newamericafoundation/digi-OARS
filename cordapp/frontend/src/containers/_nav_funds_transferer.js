export default [
  {
    _tag: "CSidebarNavDropdown",
    name: "Transfers",
    icon: "cil-chevron-right",
    _children: [
      {
        _tag: "CSidebarNavItem",
        name: "Requests Awaiting Transfer",
        to: "/transfers/approvals",
      },
      {
        _tag: "CSidebarNavItem",
        name: "History",
        to: "/transfers/history",
      },
    ],
  },
];

export default [
  {
    _tag: "CSidebarNavTitle",
    _children: ["Asset Repatriation"],
  },
  {
    _tag: "CSidebarNavItem",
    name: "Returns",
    to: "/returns",
    icon: "cil-bank",
  },
  {
    _tag: "CSidebarNavItem",
    name: "Requests",
    to: "/requests",
    icon: "cil-wallet",
  },
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

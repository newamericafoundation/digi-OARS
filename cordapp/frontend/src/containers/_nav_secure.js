export default [
  {
    _tag: "CSidebarNavItem",
    name: "Dashboard",
    to: "/",
    icon: "cil-speedometer",
  },
  {
    _tag: "CSidebarNavTitle",
    _children: ["Asset Repatriation"],
  },
  {
    _tag: "CSidebarNavItem",
    name: "Funds",
    to: "/",
    icon: "cil-bank",
  },
  {
    _tag: "CSidebarNavItem",
    name: "Withdrawals",
    to: "/",
    icon: "cil-wallet",
  },
  {
    _tag: "CSidebarNavItem",
    name: "Transfers",
    to: "/",
    icon: "cil-chevron-right",
  },
];

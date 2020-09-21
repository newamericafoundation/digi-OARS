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
  }
];

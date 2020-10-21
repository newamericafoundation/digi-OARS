export default [
  {
    _tag: "CSidebarNavTitle",
    _children: ["Administration"],
  },
  {
    _tag: "CSidebarNavDropdown",
    name: "User Management",
    icon: "cil-user",
    _children: [
      {
        _tag: "CSidebarNavItem",
        name: "Users",
        to: "/admin/users",
      }
    ],
  },
  {
    _tag: "CSidebarNavDropdown",
    name: "Node Explorer",
    icon: "cordaLogo",
    _children: [
      {
        _tag: "CSidebarNavItem",
        name: "Transactions",
        to: "/node/transactions",
      }
    ],
  }
];

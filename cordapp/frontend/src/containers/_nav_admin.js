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
      },
      // {
      //   _tag: "CSidebarNavItem",
      //   name: "History",
      //   to: "/transfers/history",
      // },
    ],
  }
];

data "azurerm_subnet" "subnet" {
  name                 = "new-america-nodes-sn-1"
  virtual_network_name = "new-america-nodes-vnet"
  resource_group_name  = data.azurerm_resource_group.resource_group.name
}
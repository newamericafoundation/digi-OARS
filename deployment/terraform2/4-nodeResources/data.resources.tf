data "azurerm_subnet" "subnet" {
  name                 = "sn-${data.azurerm_resource_group.resource_group.name}"
  virtual_network_name = "vnet-${data.azurerm_resource_group.resource_group.name}"
  resource_group_name  = data.azurerm_resource_group.resource_group.name
}
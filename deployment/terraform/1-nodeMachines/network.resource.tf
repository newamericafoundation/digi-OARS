resource "azurerm_virtual_network" "vnet" {
  name                = "${azurerm_resource_group.resource_group.name}-vnet"
  location            = azurerm_resource_group.resource_group.location
  address_space       = ["10.0.0.0/16"]
  resource_group_name = azurerm_resource_group.resource_group.name
  tags                = var.tags
}

resource "azurerm_subnet" "subnet" {
  name                 = "${azurerm_resource_group.resource_group.name}-sn-1"
  virtual_network_name = azurerm_virtual_network.vnet.name
  resource_group_name  = azurerm_resource_group.resource_group.name
  address_prefixes     = ["10.0.1.0/24"]
}
resource "azurerm_virtual_network" "vnet" {
  name                = "vnet-${data.azurerm_resource_group.resource_group.name}"
  location            = data.azurerm_resource_group.resource_group.location
  address_space       = ["10.0.0.0/16"]
  resource_group_name = data.azurerm_resource_group.resource_group.name
  tags                = var.tags
}

resource "azurerm_subnet" "subnet" {
  name                 = "sn-${data.azurerm_resource_group.resource_group.name}"
  virtual_network_name = azurerm_virtual_network.vnet.name
  resource_group_name  = data.azurerm_resource_group.resource_group.name
  address_prefixes     = ["10.0.1.0/24"]
  service_endpoints    = ["Microsoft.ContainerRegistry"]
}
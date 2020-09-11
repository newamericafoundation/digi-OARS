//resource "azurerm_application_security_group" "asg-nodes" {
//  name                = "asg-nodes"
//  resource_group_name = data.azurerm_resource_group.resource_group.name
//  location            = data.azurerm_resource_group.resource_group.name
//  tags                = var.tags
//}
//
//resource "azurerm_application_security_group" "asg-notary" {
//  name                = "asg-notary"
//  resource_group_name = data.azurerm_resource_group.resource_group.name
//  location            = data.azurerm_resource_group.resource_group.name
//  tags                = var.tags
//}
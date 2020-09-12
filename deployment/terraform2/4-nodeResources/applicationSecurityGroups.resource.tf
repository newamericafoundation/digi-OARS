resource "azurerm_application_security_group" "asg-nodes" {
  name                = "asg-nodes-${data.azurerm_resource_group.resource_group.name}"
  resource_group_name = data.azurerm_resource_group.resource_group.name
  location            = data.azurerm_resource_group.resource_group.location
  tags                = var.tags
}
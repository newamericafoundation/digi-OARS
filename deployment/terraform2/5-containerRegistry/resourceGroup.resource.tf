data "azurerm_resource_group" "resource_group" {
  name     = "${var.resource_group_name_deployment}-${terraform.workspace}"
}
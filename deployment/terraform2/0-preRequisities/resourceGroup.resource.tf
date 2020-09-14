resource "azurerm_resource_group" "resource_group" {
  name     = var.resource_group_name_tf
  location = var.location
  tags     = var.tags
}

resource "azurerm_resource_group" "resource_group_deployment" {
  name     = "${var.resource_group_name_deployment}-${terraform.workspace}"
  location = var.location
  tags     = var.tags
}
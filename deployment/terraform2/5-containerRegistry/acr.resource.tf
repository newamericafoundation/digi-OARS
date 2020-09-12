resource "azurerm_container_registry" "acr" {
  name                     = "newamericaacr${terraform.workspace}"
  resource_group_name      = data.azurerm_resource_group.resource_group.name
  location                 = data.azurerm_resource_group.resource_group.location
  sku                      = "Premium"
  admin_enabled            = true
//  georeplication_locations = ["East US"]
//  network_rule_set {
//    default_action = "Deny"
//    ip_rule {
//      action   = "Allow"
//      ip_range = "*"
//    }
//    virtual_network {
//      action    = "Allow"
//      subnet_id = "/subscriptions/af4f0732-dd8c-4330-a357-b9593255c3f0/resourceGroups/new-america-prod/providers/Microsoft.Network/virtualNetworks/vnet-new-america-prod/subnets/sn-new-america-prod"
//    }
//  }
}

output "acr_login_server" {
  value = azurerm_container_registry.acr.login_server
}

output "acr_admin_username" {
  value = azurerm_container_registry.acr.admin_username
}

output "acr_admin_password" {
  value = azurerm_container_registry.acr.admin_password
}
resource "azurerm_container_registry" "acr" {
  name                     = "newamerica${terraform.workspace}acr"
  resource_group_name      = azurerm_resource_group.resource_group.name
  location                 = azurerm_resource_group.resource_group.location
  sku                      = "Premium"
  admin_enabled            = true
//  georeplication_locations = ["East US"]
  network_rule_set {
    default_action = "Deny"
    ip_rule {
      action   = "Allow"
      ip_range = "81.148.212.130"
    }
    virtual_network {
      action    = "Allow"
      subnet_id = "/subscriptions/af4f0732-dd8c-4330-a357-b9593255c3f0/resourceGroups/new-america-nodes/providers/Microsoft.Network/virtualNetworks/new-america-nodes-vnet/subnets/new-america-nodes-sn-1"
    }
  }
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
resource "azurerm_network_security_rule" "ssh_keycloak" {
  name                                       = "restrictedips_to_keycloak"
  resource_group_name                        = data.azurerm_resource_group.resource_group.name
  access                                     = "Allow"
  direction                                  = "Inbound"
  network_security_group_name                = module.keycloak.network_security_group_name
  priority                                   = 100
  protocol                                   = "Tcp"
  source_address_prefixes                    = ["81.148.212.130"]
  source_port_range                          = "*"
  destination_application_security_group_ids = [azurerm_application_security_group.asg-keycloak.id]
  destination_port_range                     = "22"
}

data "azurerm_application_security_group" "asg-nodes" {
  name                = "asg-nodes"
  resource_group_name = "new-america-nodes"
}

resource "azurerm_network_security_rule" "frontend" {
  name                                       = "frontend_inbound"
  resource_group_name                        = data.azurerm_resource_group.resource_group.name
  access                                     = "Allow"
  direction                                  = "Inbound"
  network_security_group_name                = module.keycloak.network_security_group_name
  priority                                   = 110
  protocol                                   = "Tcp"
  source_address_prefixes                    = ["81.148.212.130"]
  source_port_range                          = "*"
  destination_application_security_group_ids = [azurerm_application_security_group.asg-keycloak.id]
  destination_port_ranges                    = ["80"]
}

resource "azurerm_network_security_rule" "ui" {
  name                        = "ui_inbound"
  resource_group_name         = data.azurerm_resource_group.resource_group.name
  access                      = "Allow"
  direction                   = "Inbound"
  network_security_group_name = module.keycloak.network_security_group_name
  priority                    = 120
  protocol                    = "Tcp"
  source_address_prefix       = "*"
  source_port_range           = "*"
  destination_address_prefix  = "*"
  destination_port_ranges     = ["80"]
}
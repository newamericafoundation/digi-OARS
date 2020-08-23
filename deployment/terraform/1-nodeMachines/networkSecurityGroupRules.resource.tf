resource "azurerm_network_security_rule" "ssh_nodes" {
  name                                       = "restrictedips_to_nodes"
  resource_group_name                        = azurerm_resource_group.resource_group.name
  access                                     = "Allow"
  direction                                  = "Inbound"
  network_security_group_name                = module.peer_nodes.network_security_group_name
  priority                                   = 100
  protocol                                   = "Tcp"
  source_address_prefixes                    = ["81.148.212.130"]
  source_port_range                          = "*"
  destination_application_security_group_ids = [azurerm_application_security_group.asg-nodes.id]
  destination_port_range                     = "22"
}

resource "azurerm_network_security_rule" "ssh_notary" {
  name                                       = "restrictedips_ssh_to_notary"
  resource_group_name                        = azurerm_resource_group.resource_group.name
  access                                     = "Allow"
  direction                                  = "Inbound"
  network_security_group_name                = module.notary.network_security_group_name
  priority                                   = 105
  protocol                                   = "Tcp"
  source_address_prefixes                    = ["81.148.212.130"]
  source_port_range                          = "*"
  destination_application_security_group_ids = [azurerm_application_security_group.asg-notary.id]
  destination_port_range                     = "22"
}

resource "azurerm_network_security_rule" "p2p_nodes" {
  name                                       = "p2p_inbound"
  resource_group_name                        = azurerm_resource_group.resource_group.name
  access                                     = "Allow"
  direction                                  = "Inbound"
  network_security_group_name                = module.peer_nodes.network_security_group_name
  priority                                   = 110
  protocol                                   = "Tcp"
  source_address_prefix                      = "*"
  source_port_range                          = "*"
  destination_application_security_group_ids = [azurerm_application_security_group.asg-nodes.id]
  destination_port_range                     = "10200"
}
resource "azurerm_network_security_rule" "ssh_inbound" {
  name                                       = "ssh_inbound"
  resource_group_name                        = data.azurerm_resource_group.resource_group.name
  access                                     = "Allow"
  direction                                  = "Inbound"
  network_security_group_name                = module.ui.network_security_group_name
  priority                                   = 100
  protocol                                   = "Tcp"
  source_address_prefix                      = "Internet"
  source_port_range                          = "*"
  destination_application_security_group_ids = [azurerm_application_security_group.asg.id]
  destination_port_range                     = "22"
}

resource "azurerm_network_security_rule" "ui_inbound" {
  name                                       = "ui_inbound"
  resource_group_name                        = data.azurerm_resource_group.resource_group.name
  access                                     = "Allow"
  direction                                  = "Inbound"
  network_security_group_name                = module.ui.network_security_group_name
  priority                                   = 110
  protocol                                   = "Tcp"
  source_address_prefix                      = "Internet"
  source_port_range                          = "*"
  destination_application_security_group_ids = [azurerm_application_security_group.asg.id]
  destination_port_range                     = "3000"
}
resource "azurerm_lb_rule" "ssh_rule_ui" {
  resource_group_name            = data.azurerm_resource_group.resource_group.name
  loadbalancer_id                = azurerm_lb.load_balancer.id
  name                           = "ssh-ui"
  protocol                       = "Tcp"
  frontend_port                  = 22000
  backend_port                   = 22
  backend_address_pool_id        = azurerm_lb_backend_address_pool.ui_backend_pool.id
  probe_id                       = azurerm_lb_probe.probe.id
  frontend_ip_configuration_name = "ipconf-${data.azurerm_resource_group.resource_group.name}"
}

resource "azurerm_lb_rule" "ssh_rule_keycloak" {
  resource_group_name            = data.azurerm_resource_group.resource_group.name
  loadbalancer_id                = azurerm_lb.load_balancer.id
  name                           = "ssh-keycloak"
  protocol                       = "Tcp"
  frontend_port                  = 23000
  backend_port                   = 22
  backend_address_pool_id        = azurerm_lb_backend_address_pool.keycloak_backend_pool.id
  probe_id                       = azurerm_lb_probe.probe.id
  frontend_ip_configuration_name = "ipconf-${data.azurerm_resource_group.resource_group.name}"
}
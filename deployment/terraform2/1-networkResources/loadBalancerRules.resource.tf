resource "azurerm_lb_rule" "ssh_rule_ui" {
  resource_group_name            = data.azurerm_resource_group.resource_group.name
  loadbalancer_id                = module.ui-lb.load_balancer_id
  name                           = "ssh-ui"
  protocol                       = "Tcp"
  frontend_port                  = 22000
  backend_port                   = 22
  backend_address_pool_id        = module.ui-lb.load_balancer_backend_address_pool_id
  probe_id                       = module.ui-lb.load_balancer_ssh_probe_id
  frontend_ip_configuration_name = module.ui-lb.load_balancer_frontend_ip_conf_name
}

resource "azurerm_lb_rule" "ssh_rule_keycloak" {
  resource_group_name            = data.azurerm_resource_group.resource_group.name
  loadbalancer_id                = module.keycloak-lb.load_balancer_id
  name                           = "ssh-keycloak"
  protocol                       = "Tcp"
  frontend_port                  = 22000
  backend_port                   = 22
  backend_address_pool_id        = module.keycloak-lb.load_balancer_backend_address_pool_id
  probe_id                       = module.keycloak-lb.load_balancer_ssh_probe_id
  frontend_ip_configuration_name = module.keycloak-lb.load_balancer_frontend_ip_conf_name
}

resource "azurerm_lb_rule" "ssh_rule_node" {
  for_each                       = var.node_to_ssh_port_map
  resource_group_name            = data.azurerm_resource_group.resource_group.name
  loadbalancer_id                = azurerm_lb.load_balancer.id
  name                           = "ssh-node-${each.key}"
  protocol                       = "Tcp"
  frontend_port                  = each.value
  backend_port                   = 22
  backend_address_pool_id        = data.azurerm_lb_backend_address_pool.backend_address_pool[each.key].id
  frontend_ip_configuration_name = azurerm_lb.load_balancer.frontend_ip_configuration[0].name
}

resource "azurerm_lb_rule" "p2p_rule_node" {
  for_each                       = var.node_to_p2p_port_map
  resource_group_name            = data.azurerm_resource_group.resource_group.name
  loadbalancer_id                = azurerm_lb.load_balancer.id
  name                           = "p2p-node-${each.key}"
  protocol                       = "Tcp"
  frontend_port                  = each.value
  backend_port                   = each.value
  backend_address_pool_id        = data.azurerm_lb_backend_address_pool.backend_address_pool[each.key].id
  frontend_ip_configuration_name = azurerm_lb.load_balancer.frontend_ip_configuration[0].name
}

resource "azurerm_lb_rule" "api_rule_node" {
  for_each                       = var.lb_to_api_port_map
  resource_group_name            = data.azurerm_resource_group.resource_group.name
  loadbalancer_id                = azurerm_lb.load_balancer.id
  name                           = "api-node-${each.key}"
  protocol                       = "Tcp"
  frontend_port                  = each.value
  backend_port                   = 8080
  backend_address_pool_id        = data.azurerm_lb_backend_address_pool.backend_address_pool[each.key].id
  frontend_ip_configuration_name = azurerm_lb.load_balancer.frontend_ip_configuration[0].name
}

resource "azurerm_lb_rule" "frontend_rule_ui" {
  resource_group_name            = data.azurerm_resource_group.resource_group.name
  loadbalancer_id                = module.ui-lb.load_balancer_id
  name                           = "frontend-ui"
  protocol                       = "Tcp"
  frontend_port                  = 80
  backend_port                   = 3000
  backend_address_pool_id        = module.ui-lb.load_balancer_backend_address_pool_id
  probe_id                       = module.ui-lb.load_balancer_frontend_probe_id
  frontend_ip_configuration_name = module.ui-lb.load_balancer_frontend_ip_conf_name
}

resource "azurerm_lb_rule" "frontend_rule_keycloak" {
  resource_group_name            = data.azurerm_resource_group.resource_group.name
  loadbalancer_id                = module.keycloak-lb.load_balancer_id
  name                           = "frontend-keycloak"
  protocol                       = "Tcp"
  frontend_port                  = 80
  backend_port                   = 3000
  backend_address_pool_id        = module.keycloak-lb.load_balancer_backend_address_pool_id
  probe_id                       = module.keycloak-lb.load_balancer_frontend_probe_id
  frontend_ip_configuration_name = module.keycloak-lb.load_balancer_frontend_ip_conf_name
}
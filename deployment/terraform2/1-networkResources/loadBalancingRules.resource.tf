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

//resource "azurerm_lb_rule" "ssh_rule_node" {
//  resource_group_name            = data.azurerm_resource_group.resource_group.name
//  loadbalancer_id                = module.node-lb.load_balancer_id
//  name                           = "ssh-keycloak"
//  protocol                       = "Tcp"
//  frontend_port                  = 22000
//  backend_port                   = 22
//  backend_address_pool_id        = module.node-lb.load_balancer_backend_address_pool_id
//  probe_id                       = module.node-lb.load_balancer_ssh_probe_id
//  frontend_ip_configuration_name = module.node-lb.load_balancer_frontend_ip_conf_name
//}


//resource "azurerm_lb_rule" "frontend_rule_ui" {
//  resource_group_name            = data.azurerm_resource_group.resource_group.name
//  loadbalancer_id                = module.ui-lb.load_balancer_id
//  name                           = "frontend-ui"
//  protocol                       = "Tcp"
//  frontend_port                  = 80
//  backend_port                   = 3000
//  backend_address_pool_id        = module.ui-lb.load_balancer_backend_address_pool_id
//  probe_id                       = module.ui-lb.load_balancer_frontend_probe_id
//  frontend_ip_configuration_name = module.ui-lb.load_balancer_frontend_ip_conf_name
//}
//
//resource "azurerm_lb_rule" "frontend_rule_keycloak" {
//  resource_group_name            = data.azurerm_resource_group.resource_group.name
//  loadbalancer_id                = module.keycloak-lb.load_balancer_id
//  name                           = "frontend-keycloak"
//  protocol                       = "Tcp"
//  frontend_port                  = 80
//  backend_port                   = 9080
//  backend_address_pool_id        = module.keycloak-lb.load_balancer_backend_address_pool_id
//  probe_id                       = module.keycloak-lb.load_balancer_frontend_probe_id
//  frontend_ip_configuration_name = module.keycloak-lb.load_balancer_frontend_ip_conf_name
//}
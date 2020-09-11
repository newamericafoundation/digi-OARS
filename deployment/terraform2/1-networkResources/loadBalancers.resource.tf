//resource "azurerm_public_ip" "public_ip" {
//  name                = "pip-${data.azurerm_resource_group.resource_group.name}"
//  location            = data.azurerm_resource_group.resource_group.location
//  resource_group_name = data.azurerm_resource_group.resource_group.name
//  allocation_method   = "Static"
//  domain_name_label   = var.public_ip_dns
//  sku                 = var.sku
//  tags                = var.tags
//}
//
//resource "azurerm_lb" "load_balancer" {
//  name                = "lb-${data.azurerm_resource_group.resource_group.name}"
//  location            = data.azurerm_resource_group.resource_group.location
//  resource_group_name = data.azurerm_resource_group.resource_group.name
//  sku                 = var.sku
//  tags                = var.tags
//
//  frontend_ip_configuration {
//    name                 = "ipconf-${data.azurerm_resource_group.resource_group.name}"
//    public_ip_address_id = azurerm_public_ip.public_ip.id
//  }
//}
//
//resource "azurerm_lb_backend_address_pool" "ui_backend_pool" {
//  resource_group_name = data.azurerm_resource_group.resource_group.name
//  loadbalancer_id     = azurerm_lb.load_balancer.id
//  name                = "ui-${data.azurerm_resource_group.resource_group.name}"
//}
//
//resource "azurerm_lb_backend_address_pool" "keycloak_backend_pool" {
//  resource_group_name = data.azurerm_resource_group.resource_group.name
//  loadbalancer_id     = azurerm_lb.load_balancer.id
//  name                = "keycloak-${data.azurerm_resource_group.resource_group.name}"
//}
//
//resource "azurerm_lb_probe" "probe" {
//  resource_group_name = data.azurerm_resource_group.resource_group.name
//  loadbalancer_id     = azurerm_lb.load_balancer.id
//  name                = "ssh-running-probe"
//  port                = 22
//}
//
//output "load_balancer_public_ip" {
//  value = azurerm_public_ip.public_ip.fqdn
//}

module "ui-lb" {
  source              = "../modules/loadBalancer"
  resource_group_name = data.azurerm_resource_group.resource_group.name
  lb_name             = "ui"
  public_ip_dns       = "oars"
  tags                = var.tags
}

module "keycloak-lb" {
  source              = "../modules/loadBalancer"
  resource_group_name = data.azurerm_resource_group.resource_group.name
  lb_name             = "keycloak"
  public_ip_dns       = "secure-oars"
  tags                = var.tags
}
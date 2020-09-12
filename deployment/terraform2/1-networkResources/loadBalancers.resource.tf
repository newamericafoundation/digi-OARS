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
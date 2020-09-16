data "azurerm_subnet" "subnet" {
  name                 = "sn-${data.azurerm_resource_group.resource_group.name}"
  virtual_network_name = "vnet-${data.azurerm_resource_group.resource_group.name}"
  resource_group_name  = data.azurerm_resource_group.resource_group.name
}

data "azurerm_lb" "load_balancer" {
  name                = "lb-node-${data.azurerm_resource_group.resource_group.name}"
  resource_group_name = data.azurerm_resource_group.resource_group.name
}

data "azurerm_lb_backend_address_pool" "load_balancer_backend_pool" {
  for_each        = var.node_virtual_machines
  name            = "${each.key}-${data.azurerm_resource_group.resource_group.name}"
  loadbalancer_id = data.azurerm_lb.load_balancer.id
}
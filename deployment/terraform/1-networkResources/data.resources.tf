data "azurerm_lb_backend_address_pool" "backend_address_pool" {
  for_each = var.node_virtual_machines
  loadbalancer_id = azurerm_lb.load_balancer.id
  name = "${each.key}-${data.azurerm_resource_group.resource_group.name}"
}
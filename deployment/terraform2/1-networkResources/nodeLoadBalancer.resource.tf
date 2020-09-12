resource "azurerm_public_ip" "public_ip" {
  name                = "pip-node-${data.azurerm_resource_group.resource_group.name}"
  location            = data.azurerm_resource_group.resource_group.location
  resource_group_name = data.azurerm_resource_group.resource_group.name
  allocation_method   = "Static"
  domain_name_label   = "node-oars"
  sku                 = var.sku
  tags                = var.tags
}

resource "azurerm_lb" "load_balancer" {
  name                = "lb-node-${data.azurerm_resource_group.resource_group.name}"
  location            = data.azurerm_resource_group.resource_group.location
  resource_group_name = data.azurerm_resource_group.resource_group.name
  sku                 = var.sku
  tags                = var.tags

  frontend_ip_configuration {
    name                 = "ipconf-node-${data.azurerm_resource_group.resource_group.name}"
    public_ip_address_id = azurerm_public_ip.public_ip.id
  }
}

resource "azurerm_lb_backend_address_pool" "backend_address_pool" {
  for_each            = var.node_virtual_machines
  resource_group_name = data.azurerm_resource_group.resource_group.name
  loadbalancer_id     = azurerm_lb.load_balancer.id
  name                = "${each.key}-${data.azurerm_resource_group.resource_group.name}"
}

resource "azurerm_lb_probe" "ssh_probe" {
  for_each            = var.node_virtual_machines
  resource_group_name = data.azurerm_resource_group.resource_group.name
  loadbalancer_id     = azurerm_lb.load_balancer.id
  name                = "ssh-running-probe-${each.key}"
  port                = 22
}
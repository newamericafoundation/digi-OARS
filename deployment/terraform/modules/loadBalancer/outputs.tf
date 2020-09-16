output "load_balancer_name" {
  value = azurerm_lb.load_balancer.name
}

output "load_balancer_id" {
  value = azurerm_lb.load_balancer.id
}

output "load_balancer_public_ip" {
  value = azurerm_public_ip.public_ip.fqdn
}

output "load_balancer_ssh_probe_id" {
  value = azurerm_lb_probe.ssh_probe.id
}

output "load_balancer_frontend_probe_id" {
  value = azurerm_lb_probe.frontend_probe.id
}

output "load_balancer_backend_address_pool_id" {
  value = azurerm_lb_backend_address_pool.backend_address_pool.id
}

output "load_balancer_frontend_ip_conf_name" {
  value = azurerm_lb.load_balancer.frontend_ip_configuration[0].name
}
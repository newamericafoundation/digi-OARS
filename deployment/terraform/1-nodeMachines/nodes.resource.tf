module "peer_nodes" {
  source                        = "../modules/nodes"
  resource_group_name           = azurerm_resource_group.resource_group.name
  vm_hostname                   = var.vm_hostname
  admin_username                = var.admin_username
  enable_ssh_key                = true
  public_ssh_key                = var.public_ssh_key
  vnet_subnet_id                = azurerm_subnet.subnet.id
  delete_os_disk_on_termination = true
  virtual_machines              = var.node_virtual_machines
  application_security_group_id = azurerm_application_security_group.asg-nodes.id
  data_disk_size_gb             = 20
  tags                          = var.tags
}

module "notary" {
  source                        = "../modules/nodes"
  resource_group_name           = azurerm_resource_group.resource_group.name
  vm_hostname                   = var.vm_hostname
  admin_username                = var.admin_username
  enable_ssh_key                = true
  public_ssh_key                = var.public_ssh_key
  vnet_subnet_id                = azurerm_subnet.subnet.id
  delete_os_disk_on_termination = true
  virtual_machines              = var.notary_virtual_machine
  application_security_group_id = azurerm_application_security_group.asg-notary.id
  data_disk_size_gb             = 20
  tags                          = var.tags
}

output "notary_ip" {
  value = module.notary.public_ip_address
}

output "node_dns" {
  value = module.peer_nodes.public_ip_dns_name
}
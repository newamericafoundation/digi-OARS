module "keycloak" {
  source                        = "../modules/nodes"
  resource_group_name           = data.azurerm_resource_group.resource_group.name
  vm_hostname                   = "keycloak"
  admin_username                = var.admin_username
  enable_ssh_key                = true
  public_ssh_key                = var.public_ssh_key
  vnet_subnet_id                = data.azurerm_subnet.subnet.id
  delete_os_disk_on_termination = true
  virtual_machines              = var.keycloak_virtual_machines
  application_security_group_id = azurerm_application_security_group.asg-keycloak.id
  data_disk_size_gb             = 20
  tags                          = var.tags
}

output "keycloak_ip" {
  value = module.keycloak.public_ip_address
}
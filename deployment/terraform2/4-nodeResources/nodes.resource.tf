module "nodes" {
  source                        = "../modules/virtualMachines"
  resource_group_name           = data.azurerm_resource_group.resource_group.name
  vm_hostname                   = var.vm_hostname
  vm_size                       = "Standard_B2ms"
  admin_username                = var.admin_username
  enable_ssh_key                = true
  public_ssh_key                = var.public_ssh_key
  vnet_subnet_id                = data.azurerm_subnet.subnet.id
  delete_os_disk_on_termination = true
  virtual_machines              = var.node_virtual_machines
  application_security_group_id = azurerm_application_security_group.asg-nodes.id
  data_disk_size_gb             = 30
  tags                          = var.tags
}
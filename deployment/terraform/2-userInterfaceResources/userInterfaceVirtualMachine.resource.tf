resource "azurerm_application_security_group" "asg" {
  name                = "asg-ui-${data.azurerm_resource_group.resource_group.name}"
  resource_group_name = data.azurerm_resource_group.resource_group.name
  location            = data.azurerm_resource_group.resource_group.location
  tags                = var.tags
}

module "ui" {
  source                        = "../modules/virtualMachines"
  resource_group_name           = data.azurerm_resource_group.resource_group.name
  vm_hostname                   = var.vm_hostname
  admin_username                = var.admin_username
  enable_ssh_key                = true
  public_ssh_key                = var.public_ssh_key
  vnet_subnet_id                = data.azurerm_subnet.subnet.id
  delete_os_disk_on_termination = true
  virtual_machines              = var.ui_virtual_machines
  application_security_group_id = azurerm_application_security_group.asg.id
  data_disk_size_gb             = 15
  availability_set              = true
  tags                          = var.tags
}

resource "azurerm_network_interface_backend_address_pool_association" "nic_alb_assoc" {
  network_interface_id    = module.ui.network_interface_ids[0]
  ip_configuration_name   = data.azurerm_network_interface.network_interface.ip_configuration[0].name
  backend_address_pool_id = data.azurerm_lb_backend_address_pool.load_balancer_backend_pool.id
}
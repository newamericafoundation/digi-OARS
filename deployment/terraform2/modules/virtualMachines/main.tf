data "azurerm_resource_group" "vm" {
  name = var.resource_group_name
}

module "os" {
  source       = "./os"
  vm_os_simple = var.vm_os_simple
}

resource "random_id" "vm-sa" {
  keepers = {
    vm_hostname = var.vm_hostname
  }
  byte_length = 6
}

locals {
  instances                      = length(var.virtual_machines) # number of machine instances
  datadisk_count                 = var.nb_data_disk_per_vm      # number of datadisks to be created per machine
  machine_keys                   = keys(var.virtual_machines)
  machine_datadiskdisk_count_map = { for k in local.machine_keys : k => local.datadisk_count }
  luns                           = { for k in local.datadisk_object : k.index => k.lun }
  datadisk_object = flatten([
    for machine_name, count in local.machine_datadiskdisk_count_map : [
      for i in range(count) : {
        index = format("datadisk-%s_disk%02d", machine_name, i)
        lun   = i
      }
    ]
  ])
  common_tags = {
    Source = "terraform"
  }
  module_path = path.module
}

resource "azurerm_storage_account" "vm-sa" {
  count                    = var.boot_diagnostics ? 1 : 0
  name                     = "bootdiag${lower(random_id.vm-sa.hex)}"
  resource_group_name      = data.azurerm_resource_group.vm.name
  location                 = data.azurerm_resource_group.vm.location
  account_tier             = element(split("_", var.boot_diagnostics_sa_type), 0)
  account_replication_type = element(split("_", var.boot_diagnostics_sa_type), 1)
  tags                     = merge(local.common_tags, var.tags)
}

resource "azurerm_availability_set" "vm" {
  count                        = var.availability_set ? 1 : 0
  name                         = "${var.vm_hostname}-avset"
  resource_group_name          = data.azurerm_resource_group.vm.name
  location                     = data.azurerm_resource_group.vm.location
  platform_fault_domain_count  = 2
  platform_update_domain_count = 2
  managed                      = true
  tags                         = merge(local.common_tags, var.tags)
}

resource "azurerm_virtual_machine" "vm-linux" {
  for_each                      = var.virtual_machines
  name                          = "vm-${var.vm_hostname}-${each.key}"
  resource_group_name           = data.azurerm_resource_group.vm.name
  location                      = data.azurerm_resource_group.vm.location
  availability_set_id           = var.availability_set == true ? join(",", azurerm_availability_set.vm.*.id) : null
  vm_size                       = var.vm_size
  network_interface_ids         = [azurerm_network_interface.vm[each.key].id]
  delete_os_disk_on_termination = var.delete_os_disk_on_termination

  storage_image_reference {
    id        = var.vm_os_id
    publisher = var.vm_os_id == "" ? coalesce(var.vm_os_publisher, module.os.calculated_value_os_publisher) : ""
    offer     = var.vm_os_id == "" ? coalesce(var.vm_os_offer, module.os.calculated_value_os_offer) : ""
    sku       = var.vm_os_id == "" ? coalesce(var.vm_os_sku, module.os.calculated_value_os_sku) : ""
    version   = var.vm_os_id == "" ? var.vm_os_version : ""
  }

  storage_os_disk {
    name              = "osdisk-${var.vm_hostname}-${each.key}"
    create_option     = "FromImage"
    caching           = "ReadWrite"
    managed_disk_type = var.storage_account_type
    disk_size_gb      = var.os_storage_size_gb
  }

  os_profile {
    computer_name  = each.key
    admin_username = var.admin_username
    admin_password = var.admin_password
    custom_data    = filebase64("${local.module_path}/scripts/autopart.sh")
  }

  os_profile_linux_config {
    disable_password_authentication = var.enable_ssh_key
    dynamic ssh_keys {
      for_each = var.enable_ssh_key ? [var.public_ssh_key] : []
      content {
        path     = "/home/${var.admin_username}/.ssh/authorized_keys"
        key_data = file(var.public_ssh_key)
      }
    }
  }

  boot_diagnostics {
    enabled     = var.boot_diagnostics
    storage_uri = var.boot_diagnostics ? join(",", azurerm_storage_account.vm-sa.*.primary_blob_endpoint) : ""
  }

  tags = merge(local.common_tags, var.tags)

  depends_on = [
    azurerm_network_interface_security_group_association.nic_security_group_assoc,
    azurerm_network_interface_application_security_group_association.nic-asg-assoc
  ]
}

//resource "azurerm_public_ip" "vm" {
//  for_each            = var.virtual_machines
//  name                = "pip-${var.vm_hostname}-${each.key}"
//  resource_group_name = data.azurerm_resource_group.vm.name
//  location            = data.azurerm_resource_group.vm.location
//  allocation_method   = var.allocation_method
//  domain_name_label   = each.value["public_ip_dns_label"] != "" ? "${terraform.workspace}-${var.vm_hostname}-${each.value["public_ip_dns_label"]}" : null
//  tags                = merge(local.common_tags, var.tags)
//}

resource "azurerm_network_interface" "vm" {
  for_each                      = var.virtual_machines
  name                          = "nic-${each.key}"
  resource_group_name           = data.azurerm_resource_group.vm.name
  location                      = data.azurerm_resource_group.vm.location
  enable_accelerated_networking = var.enable_accelerated_networking

  ip_configuration {
    name                          = "ip-${var.vm_hostname}-${each.key}"
    subnet_id                     = var.vnet_subnet_id
    private_ip_address_allocation = "Dynamic"
//    public_ip_address_id          = azurerm_public_ip.vm[each.key].id
  }
  tags = merge(local.common_tags, var.tags)
}

resource "azurerm_network_interface_security_group_association" "nic_security_group_assoc" {
  for_each                  = var.virtual_machines
  network_interface_id      = azurerm_network_interface.vm[each.key].id
  network_security_group_id = azurerm_network_security_group.vm.id
}

resource "azurerm_network_security_group" "vm" {
  name                = "nsg-${var.vm_hostname}"
  resource_group_name = data.azurerm_resource_group.vm.name
  location            = data.azurerm_resource_group.vm.location
  tags                = merge(local.common_tags, var.tags)
}

resource "azurerm_managed_disk" "datadisk" {
  for_each             = toset([for j in local.datadisk_object : j.index])
  name                 = each.key
  location             = data.azurerm_resource_group.vm.location
  resource_group_name  = data.azurerm_resource_group.vm.name
  storage_account_type = var.data_sa_type
  create_option        = "Empty"
  disk_size_gb         = var.data_disk_size_gb
  tags                 = merge(local.common_tags, var.tags)
}

resource "azurerm_virtual_machine_data_disk_attachment" "managed_disk" {
  for_each           = toset([for j in local.datadisk_object : j.index])
  managed_disk_id    = azurerm_managed_disk.datadisk[each.key].id
  virtual_machine_id = azurerm_virtual_machine.vm-linux[element(split("_", element(split("-", each.key), 1)), 0)].id
  lun                = lookup(local.luns, each.key)
  caching            = "ReadWrite"
}


resource "azurerm_network_interface_application_security_group_association" "nic-asg-assoc" {
  for_each                      = var.virtual_machines
  network_interface_id          = azurerm_network_interface.vm[each.key].id
  application_security_group_id = var.application_security_group_id
}
output "vm_ids" {
  description = "Virtual machine ids created."
  value       = concat(values(azurerm_virtual_machine.vm-linux)[*].id)
}

output "network_security_group_id" {
  description = "id of the security group provisioned"
  value       = azurerm_network_security_group.vm.id
}

output "network_security_group_name" {
  description = "name of the security group provisioned"
  value       = azurerm_network_security_group.vm.name
}

output "network_interface_names" {
  value = values(azurerm_network_interface.vm)[*].name
}

output "network_interface_ids" {
  description = "ids of the vm nics provisoned."
  value       = values(azurerm_network_interface.vm)[*].id
}

output "network_interface_private_ip" {
  description = "private ip addresses of the vm nics"
  value       = values(azurerm_network_interface.vm)[*].private_ip_address
}

output "network_interface_map_name_id" {
  value       = {
    for thing in azurerm_network_interface.vm:
        element(split("-", thing.name), 1) => thing.id
  }
}

output "network_interface_map_name_ipconf" {
  value       = {
    for thing in azurerm_network_interface.vm:
        element(split("-", thing.name), 1) => thing.ip_configuration[0].name
  }
}

//output "public_ip_id" {
//  description = "id of the public ip address provisoned."
//  value       = values(azurerm_public_ip.vm)[*].id
//}

output "vm_name" {
  description = "Name of the virtual machine created"
  value       = values(azurerm_virtual_machine.vm-linux)[*].name
}

//output "public_ip_address" {
//  description = "The actual ip address allocated for the resource."
//  value       = values(azurerm_public_ip.vm)[*].ip_address
//}
//
//output "public_ip_dns_name" {
//  description = "fqdn to connect to the first vm provisioned."
//  value       = values(azurerm_public_ip.vm)[*].fqdn
//}

output "availability_set_id" {
  description = "id of the availability set where the vms are provisioned."
  value       = azurerm_availability_set.vm.*.id
}
###################################################################################
# Resource Group                                                                  #
###################################################################################
variable "resource_group_name" {
  description = "The name of the resource group in which the resources will be created"
}

###################################################################################
# Virtual Machine                                                                 #
###################################################################################
variable "virtual_machines" {
  type = map(object({
    public_ip_dns_label = string
  }))
  description = "(Required) Map of virtual machines and public IP DNS.  Public IP DNS can be empty string if not required."
}

variable "admin_username" {
  description = "(Required) The admin username of the VM that will be deployed"
}

variable "vm_hostname" {
  description = "(Required) Local hostnname of the virtual machine"
}

variable "vm_size" {
  description = "(Optional) Specifies the size of the virtual machine."
  default     = "Standard_D2s_v3"
}

variable "custom_data" {
  description = "(Optional) The custom data to supply to the machine. This can be used as a cloud-init for Linux systems."
  default     = "/scripts/autopart.sh"
}

variable "storage_account_type" {
  description = "(Optional) Defines the type of storage account to be created. Valid options are Standard_LRS, Standard_ZRS, Standard_GRS, Standard_RAGRS, Premium_LRS."
  default     = "Standard_LRS"
}

variable "admin_password" {
  description = "(Optional) The admin password to be used on the VMSS that will be deployed. The password must meet the complexity requirements of Azure"
  default     = ""
}

variable "public_ssh_key" {
  description = "(Optional) Path to the public key to be used for ssh access to the VM."
  default     = "~/.ssh/id_rsa.pub"
}

variable "availability_set" {
  type        = bool
  description = "(Optional) Enable or Disable availability set"
  default     = false
}

variable "vm_os_simple" {
  description = "(Optional) Specify UbuntuServer, RHEL, and CentOS to get the latest image version of the specified os.  Do not provide this value if a custom value is used for vm_os_publisher, vm_os_offer, and vm_os_sku.  Options are UbuntuServer, RHEL, CentOS."
  type        = string
  default     = "UbuntuServer"
}

variable "vm_os_id" {
  description = "(Optional) The resource ID of the image that you want to deploy if you are using a custom image."
  default     = ""
}

variable "vm_os_publisher" {
  description = "(Optional) The name of the publisher of the image that you want to deploy. This is ignored when vm_os_id or vm_os_simple are provided."
  default     = ""
}

variable "vm_os_offer" {
  description = "(Optional) The name of the offer of the image that you want to deploy. This is ignored when vm_os_id or vm_os_simple are provided."
  default     = ""
}

variable "vm_os_sku" {
  description = "(Optional) The sku of the image that you want to deploy. This is ignored when vm_os_id or vm_os_simple are provided."
  default     = ""
}

variable "vm_os_version" {
  description = "(Optional) The version of the image that you want to deploy. This is ignored when vm_os_id or vm_os_simple are provided."
  default     = "latest"
}

variable "enable_ssh_key" {
  type        = bool
  description = "(Optional) Enable ssh key authentication in Linux virtual Machine"
  default     = true
}

###################################################################################
# Networking                                                                      #
###################################################################################
variable "vnet_subnet_id" {
  description = "(Required) The subnet id of the virtual network where the virtual machines will reside."
}

variable "application_security_group_id" {
  type        = string
  description = "(Required) Application security group id to provide logical grouping in NSG."
}

variable "allocation_method" {
  description = "(Optional) Defines how an IP address is assigned. Options are Static or Dynamic."
  default     = "Static"
}

variable "enable_accelerated_networking" {
  type        = bool
  description = "(Optional) Enable accelerated networking on Network interface"
  default     = false
}

variable "remote_port" {
  description = "(Optional) Remote tcp port to be used for access to the vms created via the nsg applied to the nics."
  default     = ""
}

variable "remote_ip" {
  description = "(Optional) Remote IP to be used for access to the vms created via the nsg applied to the nics."
  default     = ""
}

###################################################################################
# Disks                                                                           #
###################################################################################
variable "os_storage_size_gb" {
  description = "(Optional) OS storage disk size (minimum 30GB)."
  default     = 30
}

variable "data_disk_size_gb" {
  description = "(Optional) Data storage disk size"
  default     = 10
}

variable "nb_data_disk_per_vm" {
  description = "(Optional) Number of the data disks attached to each virtual machine"
  default     = 1
}

variable "data_sa_type" {
  description = "(Optional) Data Disk Storage Account type"
  default     = "Standard_LRS"
}

variable "delete_os_disk_on_termination" {
  type        = bool
  description = "(Optional) Delete datadisk when machine is terminated"
  default     = false
}

###################################################################################
# Boot Diagnostics                                                                #
###################################################################################
variable "boot_diagnostics" {
  type        = bool
  description = "(Optional) Enable or Disable boot diagnostics"
  default     = false
}

variable "boot_diagnostics_sa_type" {
  description = "(Optional) Storage account type for boot diagnostics"
  default     = "Standard_LRS"
}

###################################################################################
# Tagging                                                                         #
###################################################################################
variable "tags" {
  type        = map(string)
  description = "A map of the tags to use on the resources that are deployed with this module."
}
###################################################################################
# Resource Group                                                                  #
###################################################################################
variable "resource_group_name" {
  type        = string
  description = "The name of the resource group in which the resources will be created."
}

###################################################################################
# Azure Load Balancer                                                             #
###################################################################################
variable "lb_name" {
  type        = string
  description = "The name of the load balancer."
}

variable "sku" {
  type        = string
  description = "SKU of the resources."
  default     = "Standard"
}

variable "tags" {
  type        = map(string)
  description = "General tags of all resources."
}

###################################################################################
# Public IP                                                                       #
###################################################################################
variable "public_ip_dns" {
  type        = string
  description = "DNS of the public ip resource."
}
variable "resource_group_name" {
  type    = string
  default = "new-america-nodes"
}
variable "location" {
  type = string
}
variable "subscription_id" {
  type = string
}
variable "client_id" {
  type = string
}
variable "client_secret" {
  type = string
}
variable "tenant_id" {
  type = string
}
variable "tags" {
  type = map(string)
}
variable "vm_hostname" {
  type    = string
  default = "oars"
}
variable "admin_username" {
  type    = string
  default = "corda"
}
variable "public_ssh_key" {
  type    = string
  default = "~/.ssh/id_rsa.pub"
}
variable "keycloak_virtual_machines" {
  type = map(object({
    public_ip_dns_label = string
  }))
  default = {
    "keycloak" = {
      public_ip_dns_label = ""
    }
  }
}
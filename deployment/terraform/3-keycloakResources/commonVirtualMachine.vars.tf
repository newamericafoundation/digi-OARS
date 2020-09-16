variable "vm_hostname" {
  type    = string
  default = "secure"
}
variable "admin_username" {
  type    = string
  default = "corda"
}
variable "public_ssh_key" {
  type    = string
  default = "~/.ssh/id_rsa.pub"
}
variable "virtual_machines" {
  type = map(object({
    public_ip_dns_label = string
  }))
  default = {
    "keycloak" = {
      public_ip_dns_label = ""
    }
  }
}
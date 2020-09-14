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
variable "ui_virtual_machines" {
  type = map(object({
    public_ip_dns_label = string
  }))
  default = {
    "ui" = {
      public_ip_dns_label = ""
    }
  }
}
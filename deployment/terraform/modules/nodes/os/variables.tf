variable "vm_os_simple" {
  default = ""
}

# Definition of the standard OS with "SimpleName" = "publisher,offer,sku"
variable "standard_os" {
  default = {
    "UbuntuServer" = "Canonical,UbuntuServer,18.04-LTS"
    "RHEL"         = "RedHat,RHEL,7.3"
    "CentOS"       = "OpenLogic,CentOS,7.3"
  }
}
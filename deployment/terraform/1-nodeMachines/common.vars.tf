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
variable "notary_virtual_machine" {
  type = map(object({
    public_ip_dns_label = string
  }))
  default = {
    "notary" = {
      public_ip_dns_label = ""
    }
  }
}
variable "node_virtual_machines" {
  type = map(object({
    public_ip_dns_label = string
  }))
  default = {
    "usdos" = {
      public_ip_dns_label = "usdos"
    }
    "usdoj" = {
      public_ip_dns_label = "usdoj"
    }
    "uscso" = {
      public_ip_dns_label = "uscso"
    }
    "ustreasury" = {
      public_ip_dns_label = "ustreasury"
    }
    "catanmofa" = {
      public_ip_dns_label = "catanmofa"
    }
    "catanmoj" = {
      public_ip_dns_label = "catanmoj"
    }
    "catancso" = {
      public_ip_dns_label = "catancso"
    }
    "catantreasury" = {
      public_ip_dns_label = "catantreasury"
    }
    "newamerica" = {
      public_ip_dns_label = "newamerica"
    }
  }
}
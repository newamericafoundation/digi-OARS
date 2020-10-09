variable "node_virtual_machines" {
  type = map(object({
    public_ip_dns_label = string
  }))
  default = {
    "notary" = {
      public_ip_dns_label = ""
    }
    "usdos" = {
      public_ip_dns_label = ""
    }
    "usdoj" = {
      public_ip_dns_label = ""
    }
    "uscso" = {
      public_ip_dns_label = ""
    }
    "catanmofa" = {
      public_ip_dns_label = ""
    }
    "catanmoj" = {
      public_ip_dns_label = ""
    }
    "catancso" = {
      public_ip_dns_label = ""
    }
    "catantreasury" = {
      public_ip_dns_label = ""
    }
    "newamerica" = {
      public_ip_dns_label = ""
    }
  }
}
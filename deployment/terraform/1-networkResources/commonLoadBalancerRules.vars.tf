variable "node_to_ssh_port_map" {
  type = map(string)
  default = {
    "notary"        = 22000
    "usdos"         = 22100
    "usdoj"         = 22200
    "uscso"         = 22300
    "catanmofa"     = 22500
    "catanmoj"      = 22600
    "catancso"      = 22700
    "catantreasury" = 22800
    "newamerica"    = 22900
  }
}

variable "node_to_p2p_port_map" {
  type = map(string)
  default = {
    "notary"        = 12000
    "usdos"         = 12100
    "usdoj"         = 12200
    "uscso"         = 12300
    "catanmofa"     = 12500
    "catanmoj"      = 12600
    "catancso"      = 12700
    "catantreasury" = 12800
    "newamerica"    = 12900
  }
}

variable "lb_to_api_port_map" {
  type = map(string)
  default = {
    "usdos"         = 10100
    "usdoj"         = 10200
    "uscso"         = 10300
    "catanmofa"     = 10500
    "catanmoj"      = 10600
    "catancso"      = 10700
    "catantreasury" = 10800
    "newamerica"    = 10900
  }
}
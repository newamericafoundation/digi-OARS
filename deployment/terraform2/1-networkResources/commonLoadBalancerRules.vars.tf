variable "node_to_ssh_port_map" {
  type = map(string)
  default = {
    "notary"        = 22000
    "usdos"         = 22100
    "usdoj"         = 22200
    "uscso"         = 22300
    "ustreasury"    = 22400
    "catanmofa"     = 22500
    "catanmoj"      = 22600
    "catancso"      = 22700
    "catantreasury" = 22800
    "newamerica"    = 22900
  }
}
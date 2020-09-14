variable "resource_group_name_tf" {
  type    = string
  default = "new-america-terraform"
}
variable "resource_group_name_deployment" {
  type    = string
  default = "new-america"
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
  default = {
    Source      = "terraform"
    Owner       = "neal.shah@r3.com"
    Environment = "prod"
  }
}

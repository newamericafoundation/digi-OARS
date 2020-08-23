variable "resource_group_name" {
  type    = string
  default = "new-america-tfstate"
}
variable "location" {
  type = string
}
variable "storage_account_name" {
  type    = string
  default = "newamericatfstate"
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
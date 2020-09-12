terraform {
  required_version = ">= 0.12.25"

  backend "azure" {
    resource_group_name  = "new-america-terraform"
    storage_account_name = "newamericaterraformstate"
    container_name       = "prod-tfstate"
    key                  = "network.prod.terraform.tfstate"
  }
}

output "StateStorageUrl" {
  value = "https://newamericatfstate.blob.core.windows.net/${terraform.workspace}-tfstate/network.${terraform.workspace}.terraform.tfstate:${terraform.workspace}"
}
terraform {
  required_version = ">= 0.12.25"

  backend "azure" {
    resource_group_name  = "new-america-tfstate"
    storage_account_name = "newamericatfstate"
    container_name       = "staging-tfstate"
    key                  = "acr.staging.terraform.tfstate"
  }
}

output "StateStorageUrl" {
  value = "https://newamericatfstate.blob.core.windows.net/staging-tfstate/acr.staging.terraform.tfstate:${terraform.workspace}"
}
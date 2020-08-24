# Terraform Azure Deployment

## Deployment Diagram

TO-DO

## State Files

Terraform backend state files are stored in an Azure Storage Account
`newamericatfstate`.

## Terraform `tfvars`

`terraform.tfvars` files are not committed to source control as they contain
secure data pertaining to the cloud deployment.

This file is required for both `0-preRequisites` and `1-nodeMachines`.

Here is a template file:

```hcl
location             = "eastus"
subscription_id      = "< hidden >"
client_id            = "< hidden >"
client_secret        = "< hidden >"
tenant_id            = "< hidden >"
tags = {
  Source      = "terraform"
  Owner       = "< hidden >"
  Environment = "staging"
}
```

## Parent Module Overview

### `0-preRequisites`

This module deploys an Azure Storage Account and creates a container
to store Terraform tfstate files.

This module must only be run once, therefore  it can be ignored for all deployments.

### `1-nodeMachines`

This module deploys the node machines and notary machine, along with the networking
resources.

#### Steps:
1. `terraform init`
2. `terraform workspace new poc` - this is for the PoC workspace
3. `terraform plan` - check for any changes
4. `terraform apply` - apply changes (you will be prompted for confirmation)
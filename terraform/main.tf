resource "azurerm_resource_group" "transport" {
  name     = "rg-transport-app"
  location = "Germany West Central"

  tags = {
    project     = "Transport-App"
    environment = "student"
    managed_by  = "terraform"
  }
}
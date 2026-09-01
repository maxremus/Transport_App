resource "azurerm_log_analytics_workspace" "transport" {
  name                = "law-transport-app"
  location            = azurerm_resource_group.transport.location
  resource_group_name = azurerm_resource_group.transport.name
  sku                 = "PerGB2018"
  retention_in_days   = 30

  tags = {
    project     = "Transport-App"
    environment = "student"
    managed_by  = "terraform"
  }
}

resource "azurerm_container_app_environment" "transport" {
  name                       = "cae-transport-app"
  location                   = azurerm_resource_group.transport.location
  resource_group_name        = azurerm_resource_group.transport.name
  log_analytics_workspace_id = azurerm_log_analytics_workspace.transport.id

  tags = {
    project     = "Transport-App"
    environment = "student"
    managed_by  = "terraform"
  }
}
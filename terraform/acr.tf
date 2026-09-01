resource "azurerm_container_registry" "transport" {
  name                = "transportappacr2026"
  resource_group_name = azurerm_resource_group.transport.name
  location            = azurerm_resource_group.transport.location

  sku           = "Basic"
  admin_enabled = true

  tags = {
    project     = "Transport-App"
    environment = "student"
    managed_by  = "terraform"
  }
}
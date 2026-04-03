@mobile @android
Feature: ApiDemos Navegación y verificación de Asset

  Como usuario de ApiDemos
  Quiero navegar a la pantalla de lectura de Asset
  Para validar la navegación y el contenido mostrado

  Background:
    Given mobile execution is enabled

  Scenario: Navegar a Content > Assets > Read Asset y verificar textos
    Given the user is on the application home page
    When the user navigates to Content/Assets/Read Asset
    Then the screen title should be "Content/Assets/Read Asset"
    And the subtitle should be "This text is stored in a raw Asset."

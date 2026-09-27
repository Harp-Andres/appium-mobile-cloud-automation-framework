@mobile @android @theapp-smoke
Feature: TheApp farm smoke
  Lightweight navigation against the shared AUT (TheApp) for BrowserStack, AWS Device Farm, or local Appium.

  Background:
    Given mobile execution is enabled

  Scenario: Home shows Login Screen entry and form opens
    Given the user is on TheApp home
    When the user opens the Login Screen
    Then the login form should be visible

  @login
  Scenario: Optional login with demo credentials
    Given the user is on TheApp home
    When the user opens the Login Screen
    And the user logs in with username "alice" and password "mypassword"
    Then the secret area should show user "alice"

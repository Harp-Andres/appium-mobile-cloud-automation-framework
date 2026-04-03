@framework-health @mobile @android
Feature: Mobile framework health check
  To validate the execution chain end to end
  As a mobile automation team
  I want to confirm that Appium reaches the device and opens the app

  Scenario: Start mobile session and verify the app opens on device
    Given mobile execution is enabled
    When I initialize the Appium driver
    Then the mobile session should be available
    When I query the app launch state
    Then the app should be running in foreground


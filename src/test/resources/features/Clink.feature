@desktop

Feature: Clink Dashboard

  Scenario: The dashboard screen must include the Device Status
    Given I launch CLINK application
    And I click connect to a device
    When I enter my Clink credentials and login
    And I select a device from the dropdown
    Then I should see the Dashboard screen
    And I should see correct details
#    And I close the application

  Scenario: The dashboard screen must include series of other actions that can be performed
    Given I launch CLINK application
    And I click connect to a device
    When I enter my Clink credentials and login
    And I select a device from the dropdown
    Then I should see the Dashboard screen
    And I navigate to Network Settings
    Then I should see the 1. Network Settings screen
    And I can update network settings details
    Then I verify inputs are persistent


  Scenario: I can login using Insight Token
    Given I launch CLINK application
    And I click connect to a device
    When I enter my Insight Token and login
    And I select a device from the dropdown
    Then I should see the Dashboard screen
    And I navigate to Network Settings
    Then I should see the 1. Network Settings screen
    And I can update network settings details
    Then I verify inputs are persistent

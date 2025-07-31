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
    When I enter my Insight Token and login
    And I select a device from the dropdown
    Then I should see the Dashboard screen
    And I navigate to Network Settings page
    Then I should see the Network Settings screen
    Then I verify Network Settings labels are present
#    And I can update network settings details
#    Then I verify inputs are persistent


  Scenario: I can login using Insight Token
    Given I launch CLINK application
    And I click connect to a device
    When I enter my Insight Token and login
    And I select a device from the dropdown
    Then I should see the Dashboard screen
    And I navigate to Network Settings page
    Then I should see the Network Settings screen
    And I can update network settings details
    Then I verify inputs are persistent

  Scenario: I can update Survey Settings
    Given I launch CLINK application
    And I click connect to a device
    When I enter my Insight Token and login
    And I select a device from the dropdown
    Then I should see the Dashboard screen
    And I navigate to Survey Settings page
    Then I should see the Survey Settings screen
    And I can update survey settings details
    Then I refresh the page
    Then I verify changes are persistent



    Scenario: Complete scenario
      Given I launch CLINK application
      And I click connect to a device
      When I enter my Insight Token and login
      And I select a device from the dropdown
      Then I should see the Dashboard screen
      And I navigate to Network Settings page
      Then I should see the Network Settings screen
      Then I verify Network Settings labels are present
      And I navigate to Dashboard via navigation bar
      And I navigate to Survey Settings page
      Then I should see the Survey Settings screen
      And I can update survey settings details
      Then I refresh the page
      Then I verify changes are persistent
      And I navigate to Lane Setup via navigation bar
      And I navigate to Advanced via navigation bar
      And I navigate to Network Settings via navigation bar
      Then I can disconnect the device
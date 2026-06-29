Feature: MS Teams

  Scenario: User can send messages
    Given I launch MS TEAMS application


  Scenario: User can send messages for a duration
    Given I launch MS TEAMS application
    Then I click on my own chat
    And I type random text in the message box for 60 minutes
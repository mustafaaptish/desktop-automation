@desktop

Feature: Notepad Basic Operations

  Scenario: Notepad CRUD
    Given I launch NOTEPAD application
    And I type "This is Notepad automation" in notepad
    Then I wait for 3 seconds
    Then I verify the text
    And I clear the text
    And I open a new tab
    And I type "This is a new tab" in notepad
    Then I wait for 3 seconds
    Then I verify the text
    And I clear the text
    And I open a new tab
    And I type "There is no AI here" in notepad
    Then I wait for 3 seconds
    Then I verify the text
    Then I close Notepad

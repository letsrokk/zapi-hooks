Feature: ZAPI For Zephyr Integration

  @tmsLink=AQA-1400
  Scenario: Successful Scenario with TMS Link
    Given preconditions
    When action
    Then success == true

  @tmsLink=AQA-1401
  Scenario: Successful Scenario with TMS Link
    Given preconditions
    When action
    Then success == false

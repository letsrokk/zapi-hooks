Feature: ZAPI For Zephyr Integration

#  @tmsLink=AQA-1400
#  Scenario: Successful Scenario with TMS Link
#    Given preconditions
#    When action
#    Then success == true
#
#  @tmsLink=AQA-1401
#  Scenario: Successful Scenario with TMS Link
#    Given preconditions
#    When action
#    Then success == false

  @tmsLink=REGS-17
  Scenario Outline: Scenario Outline with multiple examples
    Given preconditions
    When action
    Then success == <EXPECTED_RESULT>

    Examples:
    | EXPECTED_RESULT |
    | true            |
    | false           |
    | true            |

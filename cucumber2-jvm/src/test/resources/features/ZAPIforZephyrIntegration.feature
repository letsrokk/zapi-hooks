Feature: ZAPI For Zephyr Integration (CUCUMBER JVM 2.0)

  @tmsLink=AQA-1400
  Scenario: Successful Scenario with TMS Link
    Given preconditions
    When action
    Then success == true

  @issue=AQA-1414
  @tmsLink=AQA-1401
  Scenario: Successful Scenario with TMS Link
    Given preconditions
    When action
    Then success == false

  @issue=AQA-1418
  @tmsLink=AQA-1417
  Scenario Outline: Scenario Outline with multiple examples
    Given preconditions
    When action
    Then success == <EXPECTED_RESULT>

    Examples:
    | EXPECTED_RESULT |
    | true            |
    | false           |
    | true            |

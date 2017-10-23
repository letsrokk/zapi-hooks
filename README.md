# zapi-hooks

Zephyr for Jira integration hooks (using ZAPI)

[![Download](https://api.bintray.com/packages/letsrokk/github/zapi-hooks/images/download.svg) ](https://bintray.com/letsrokk/github/zapi-hooks/_latestVersion)

# Cucumber JVM 2.0

Configuration example

```
package org.fxclub.qa.zapi.cucumber2;

import cucumber.api.CucumberOptions;
import cucumber.api.testng.AbstractTestNGCucumberTests;

@CucumberOptions(
        features = "src/test/resources/features/ZAPIforZephyrIntegration.feature",
        glue = "org.fxclub.qa.zapi.cucumber2",
        plugin = {"json:target/cucumber-report.json","org.fxclub.qa.zapi.cucumber2.ZAPICucumberPlugin"}
)
public class RunCucumberTests extends AbstractTestNGCucumberTests {

}
```

# Zephyr Test Case ID annotations for Scenarios

Scenarios should be annotated with `@tmsLink=PROJECT-12345`

```
Feature: ZAPI For Zephyr Integration (CUCUMBER JVM 2.0)

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
```

# Parameters

Jira URL, username and token can be set in one of 2 ways:
- as System Property variable on test run
```
<plugin>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.20.1</version>
    <configuration>
        <testFailureIgnore>true</testFailureIgnore>
        <systemPropertyVariables>
            <ZAPI_JIRA_URL>https://jira.example.org</ZAPI_JIRA_URL>
            <ZAPI_JIRA_USERNAME>jirausername</ZAPI_JIRA_USERNAME>
            <ZAPI_JIRA_PASSWORD>jirapassword</ZAPI_JIRA_PASSWORD>
        </systemPropertyVariables>
    </configuration>
</plugin>
```
- or as Environment variables
```
export ZAPI_JIRA_URL=https://jira.example.com/
export ZAPI_JIRA_USERNAME=jirausername
export ZAPI_JIRA_PASSWORD=jirapassword
```

# Selecting Jira Project Version for Test Cycle

By default, all test cycles created in `Unscheduled` version, and if we want to detect version in Test 
then we need to implement custom `VersionDetector`
```
package org.fxclub.qa.ws.reg_ws.v3.steps;

import org.fxclub.qa.ws.reg_ws.helpers.V3xRegWsHelper;
import org.fxclub.qa.zapi.core.VersionDetector;

import java.net.MalformedURLException;

public class RegWsVersionDetector implements VersionDetector{

    @Override
    public String getVersion() {
        try {
            String version = new V3xRegWsHelper().getInfo().getApp().getVersion();
            return version;
        } catch (MalformedURLException e) {
            return "Unscheduled";
        }
    }

}
```
and set class name as parameter `ZAPI_VERSION_DETECTOR` for test execution
```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <systemProperties>
            <ZAPI_VERSION_DETECTOR>org.fxclub.qa.ws.reg_ws.v3.steps.RegWsVersionDetector</ZAPI_VERSION_DETECTOR>
        </systemProperties>
    </configuration>
</plugin>
```

# Some examples

![Test Cycles Screen](screenshots/testCyclesScreen.png?raw=true "Test Cycles Screen")

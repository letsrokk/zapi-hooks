package org.fxclub.qa.zapi.cucumber;

import cucumber.api.CucumberOptions;
import cucumber.api.testng.AbstractTestNGCucumberTests;

@CucumberOptions(
        features = "src/test/resources/features/ZAPIforZephyrIntegration.feature",
        glue = "org.fxclub.qa.zapi.cucumber",
        plugin = {"json:target/cucumber-report.json","org.fxclub.qa.zapi.cucumber.ZAPICucumberHook"}
)
public class RunCucumberTests extends AbstractTestNGCucumberTests {

}

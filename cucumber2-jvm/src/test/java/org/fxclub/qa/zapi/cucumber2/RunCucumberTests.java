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

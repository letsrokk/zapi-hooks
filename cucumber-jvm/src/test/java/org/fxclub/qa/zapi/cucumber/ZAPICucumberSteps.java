package org.fxclub.qa.zapi.cucumber;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.testng.Assert;

public class ZAPICucumberSteps {

    @Given("^preconditions$")
    public void preconditions() throws Throwable {
        Thread.sleep(1000);
    }

    @When("^action$")
    public void action() throws Throwable {
        Thread.sleep(1000);
    }

    @Then("^success == ([a-zA-Z]+)$")
    public void success(boolean isSuccess) throws Throwable {
        Assert.assertTrue(isSuccess);
    }
}

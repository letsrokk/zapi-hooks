package org.fxclub.qa.zapi.cucumber;

import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.fxclub.qa.zapi.*;
import org.fxclub.qa.zapi.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by majer-dy on 17/02/2017.
 */
public class ZAPICucumberHook implements Formatter, Reporter {

    private Logger logger = LoggerFactory.getLogger(ZAPICucumberHook.class);

    private ZAPIClient zapiClient = new ZAPIClient();

    private Feature currentFeature;
    private String currentStatus;

    private VersionDetector versionDetector;
    private final String ZAPI_CYCLE_NAME_DEFAULT = "CUCUMBER";

    public ZAPICucumberHook(){
        try {
            versionDetector = (VersionDetector) Class.forName(
                    System.getProperty("ZAPI_VERSION_DETECTOR","org.fxclub.qa.zapi.core.DefaultVersionDetector")
            ).newInstance();
        }catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex){
            versionDetector = new DefaultVersionDetector();
        }
    }

    @Override
    public void syntaxError(String s, String s1, List<String> list, String s2, Integer integer) {

    }

    @Override
    public void uri(String s) {

    }

    @Override
    public void feature(Feature feature) {
        this.currentFeature = feature;
    }

    @Override
    public void scenarioOutline(ScenarioOutline scenarioOutline) {

    }

    @Override
    public void examples(Examples examples) {

    }

    @Override
    public void startOfScenarioLifeCycle(Scenario scenario) {
        this.currentStatus = ZAPIClient.PASSED;
    }

    @Override
    public void background(Background background) {

    }

    @Override
    public void scenario(Scenario scenario) {

    }

    @Override
    public void step(Step step) {

    }

    @Override
    public void endOfScenarioLifeCycle(Scenario scenario) {
        try{
            Collection<Tag> tags = scenario.getTags();

            for(Tag tag : tags){
                Matcher testCaseIDMatcher = Pattern.compile("@tmsLink=([a-zA-Z0-9-]+)").matcher(tag.getName());
                if(testCaseIDMatcher.find()){
                    String testCaseId = testCaseIDMatcher.group(1);
                    TestCase testCase = new TestCase(testCaseId);
                    ProjectInfo projectInfo = zapiClient.getProjectInfo(testCase);
                    ProjectVersions projectVersions = zapiClient.getVersions(projectInfo);

                    ProjectVersion projectVersion = projectVersions.getUnreleasedVersions().stream()
                            .filter(version -> version.getLabel().equalsIgnoreCase(versionDetector.getVersion()))
                            .findFirst()
                            .orElse(projectVersions.getUnreleasedVersions().get(0));

                    TestCycle testCycle = zapiClient.getOrCreateTestCycle(
                            Optional.ofNullable(currentFeature.getName()).orElse(ZAPI_CYCLE_NAME_DEFAULT),
                            projectInfo,
                            projectVersion
                    );
                    zapiClient.addTestsToCycle(testCycle, testCase);
                    zapiClient.udpateExecutionStatus(testCycle, testCase, this.currentStatus);
                }
            }
        }catch (Exception e){
            logger.error("ZAPI Client Exception: " + e.getMessage());
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public void done() {

    }

    @Override
    public void close() {

    }

    @Override
    public void eof() {

    }

    @Override
    public void before(Match match, Result result) {

    }

    @Override
    public void result(Result result) {
        if(ZAPIClient.FAILED.equals(result.getStatus())){
            //Set Scenario status to FAILED
            this.currentStatus = ZAPIClient.FAILED;
        } else if(ZAPIClient.SKIPPED.equals(result.getStatus())){
            //Set Scenario status to SKIPPED
            if(ZAPIClient.PASSED.equals(result.getStatus())){
                //if scenario wasn't FAILED before
                this.currentStatus = ZAPIClient.SKIPPED;
            }
        }
    }

    @Override
    public void after(Match match, Result result) {

    }

    @Override
    public void match(Match match) {

    }

    @Override
    public void embedding(String s, byte[] bytes) {

    }

    @Override
    public void write(String s) {

    }
}

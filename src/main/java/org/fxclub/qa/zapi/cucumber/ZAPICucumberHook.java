package org.fxclub.qa.zapi.cucumber;

import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.fxclub.qa.zapi.*;
import org.fxclub.qa.zapi.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by majer-dy on 17/02/2017.
 */
public class ZAPICucumberHook implements Formatter, Reporter {

    private Logger logger = LoggerFactory.getLogger(ZAPICucumberHook.class);

    private ZAPIClient zapiClient = new ZAPIClient();
    private VersionDetector versionDetector;
    private final String ZAPI_CYCLE_NAME_DEFAULT = "CUCUMBER";

    private final static Map<String,ProjectInfo> projectInfoMap = new HashMap<>();
    private final static Map<Integer,ProjectVersions> projectVersionsMap = new HashMap<>();
    private final static Map<String,TestCycle> testCyclesMap = new HashMap<>();

    private Feature currentFeature;
    private Scenario currentScenario;
    private String currentStatus;
    private Examples currentExamples;

    private static final String FAILED = "failed";
    private static final String PASSED = "passed";
    private static final String SKIPPED = "skipped";
    private static final String PENDING = "pending";

    private String getZephyrStatusId(String cucumberStatus){
        switch (cucumberStatus) {
            case PASSED:
                return ZAPIClient.PASSED;
            case FAILED:
                return ZAPIClient.FAILED;
            case PENDING:
                return ZAPIClient.IN_PROGRESS;
            case SKIPPED:
            default:
                return ZAPIClient.SKIPPED;

        }
    }

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
        this.currentExamples = examples;
    }

    @Override
    public void startOfScenarioLifeCycle(Scenario scenario) {
        this.currentScenario = scenario;
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
                    logger.debug("Zephyr: Test Case: " + testCase.toString());

                    ProjectInfo projectInfo = getProjectInfo(testCase);
                    logger.debug("Zephyr: Project Info: " + projectInfo.toString());

                    ProjectVersion projectVersion = getProjectVersion(projectInfo);
                    logger.debug("Zephyr: Project Version: " + projectVersion.toString());

                    TestCycle testCycle = getTestCycle(projectInfo, projectVersion);
                    logger.debug("Zephyr: Test Cycle: " + testCycle.toString());

                    logger.debug("Zephyr: add " + testCase.toString() + " to " + testCycle.toString());
                    zapiClient.addTestsToCycle(testCycle, testCase);

                    logger.debug("Zephyr: update execution status for " + testCase.toString());
                    udpateExecutionStatus(testCycle, testCase, this.currentStatus);
                }
            }
        }catch (Exception e){
            logger.error("Zephyr: ZAPI Client Exception: " + e.getMessage());
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    private synchronized ProjectInfo getProjectInfo(TestCase testCase){
        return projectInfoMap.computeIfAbsent(
                testCase.getProjectKey(),
                projectKey -> zapiClient.getProjectInfo(projectKey)
        );
    }

    private synchronized ProjectVersion getProjectVersion(ProjectInfo projectInfo){
        ProjectVersions projectVersions = projectVersionsMap.computeIfAbsent(
                projectInfo.getId(),
                id -> zapiClient.getVersions(id)
        );

        return Stream.concat(
                projectVersions.getReleasedVersions().stream(),
                projectVersions.getUnreleasedVersions().stream()
        ).filter(version -> version.getLabel().equalsIgnoreCase(versionDetector.getVersion()))
                .findFirst()
                .orElse(projectVersions.getUnreleasedVersions().get(0));
    }

    private synchronized TestCycle getTestCycle(ProjectInfo projectInfo, ProjectVersion projectVersion){
        return testCyclesMap.computeIfAbsent(
                currentFeature.getName(),
                feature -> {
                    String testCycleName = Optional.ofNullable(feature).orElse(ZAPI_CYCLE_NAME_DEFAULT);
                    logger.debug(String.format("Zephyr: create test cycle with name \"%s\"", testCycleName));

                    TestCycle testCycle = zapiClient.getTestCycle(testCycleName, projectInfo, projectVersion);
                    if(testCycle == null){
                        testCycle = zapiClient.createTestCycle(testCycleName, projectInfo, projectVersion);
                    } else {
                        logger.debug("Zephyr: delete existing test cycle " + testCycle.toString());
                        zapiClient.deleteTestCycle(testCycle);

                        testCycle = zapiClient.createTestCycle(testCycleName, projectInfo, projectVersion);
                    }
                    logger.debug("Zephyr: created new test cycle " + testCycle.toString());

                    return testCycle;
                }
        );
    }

    private synchronized Execution udpateExecutionStatus(TestCycle testCycle, TestCase testCase, String currentStatus) {
        String currentStatusId = getZephyrStatusId(currentStatus);

        Execution execution = zapiClient.getExecution(testCycle, testCase);

        switch (execution.getExecutionStatus()){
            case ZAPIClient.UNEXECUTED:
            case ZAPIClient.PASSED:
                execution = zapiClient.udpateExecutionStatus(execution, currentStatusId);
                break;
            default:
                //nothing to do
        }

        if(!StringUtils.equals(currentStatusId, ZAPIClient.PASSED)){
            String comment = execution.getComment();
            if(StringUtils.isNotEmpty(comment)) {
                comment += "\n";
            }
            comment += currentStatus.toUpperCase();
            if(currentExamples != null) {
                comment += ": " + getExampleAsString(currentScenario, currentExamples) + "\n==================";
            }
            execution = zapiClient.udpateExecutionComment(execution, comment);
        }

        return execution;
    }

    private String getExampleAsString(final Scenario scenario, final Examples exmpls) {
        if (exmpls != null) {
            final List<String> names = exmpls.getRows().get(0).getCells();

            final List<String> values = exmpls.getRows().stream()
                    .filter(exmpl -> Objects.equals(exmpl.getLine(), scenario.getLine()))
                    .findFirst()
                    .get()
                    .getCells();

            List<String> exampleAsList = new ArrayList<>();
            for(int i = 0; i < names.size(); i++){
                exampleAsList.add(
                        new StringBuilder()
                                .append(names.get(i))
                                .append(": [")
                                .append(values.get(0))
                                .append("]")
                                .toString()
                );
            }

            return StringUtils.join(exampleAsList,", ");
        } else {
            return "";
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
        this.currentStatus = result.getStatus();
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

package org.fxclub.qa.zapi.cucumber2;

import cucumber.api.Result;
import cucumber.api.event.EventHandler;
import cucumber.api.event.EventPublisher;
import cucumber.api.event.TestCaseFinished;
import cucumber.api.event.TestSourceRead;
import cucumber.api.formatter.Formatter;
import cucumber.runtime.formatter.TestSourcesModel;
import gherkin.ast.Examples;
import gherkin.ast.ScenarioDefinition;
import gherkin.ast.ScenarioOutline;
import gherkin.ast.TableRow;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxclub.qa.zapi.ZAPIClient;
import org.fxclub.qa.zapi.core.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ZAPICucumberPlugin implements Formatter {

    private Logger logger = LogManager.getLogger(ZAPICucumberPlugin.class);

    private final String testCasePattern = "@tmsLink=([a-zA-Z0-9-]+)";
    private final String issuePattern = "@issue=([a-zA-Z0-9-]+)";

    private ZAPIClient zapiClient = new ZAPIClient();
    private VersionDetector versionDetector;

    private final static ConcurrentHashMap<String,ProjectInfo> projectInfoMap = new ConcurrentHashMap<>();
    private final static ConcurrentHashMap<Integer,ProjectVersions> projectVersionsMap = new ConcurrentHashMap<>();
    private final static ConcurrentHashMap<String,ZephyrTestCycle> testCyclesMap = new ConcurrentHashMap<>();
    private final static Object syncObjectForExecutionUpdates = new Object();

    private String currentFeatureName;
    private String currentScenarioName;
    private String currentScenarioParameters;

    private final TestSourcesModel testSources = new TestSourcesModel();

    private final EventHandler<TestSourceRead> featureStartedHandler = this::handleFeatureStartedHandler;
    private final EventHandler<TestCaseFinished> caseFinishedHandler = this::handleTestCaseFinished;

    public ZAPICucumberPlugin(){
        try {
            versionDetector = (VersionDetector) Class.forName(
                    System.getProperty("ZAPI_VERSION_DETECTOR","DefaultVersionDetector")
            ).newInstance();
        }catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex){
            versionDetector = new DefaultVersionDetector();
        }
    }

    @Override
    public void setEventPublisher(EventPublisher eventPublisher) {
        eventPublisher.registerHandlerFor(TestSourceRead.class, featureStartedHandler);
        eventPublisher.registerHandlerFor(TestCaseFinished.class, caseFinishedHandler);
    }

    private void handleFeatureStartedHandler(final TestSourceRead event) {
        testSources.addTestSourceReadEvent(event.uri, event);
    }

    private void handleTestCaseFinished(final TestCaseFinished event) {
        List<String> testCaseIds = event.testCase.getTags().stream()
                .filter(tag -> Pattern.matches(testCasePattern, tag.getName()))
                .map(tag -> {
                    Matcher issueMatcher = Pattern.compile(testCasePattern).matcher(tag.getName());
                    issueMatcher.find();
                    return issueMatcher.group(1);
                }).collect(Collectors.toList());

        currentFeatureName = testSources.getFeature(event.testCase.getUri()).getName();
        currentScenarioName = event.testCase.getName();

        ScenarioDefinition scenarioDefinition =
                testSources.getScenarioDefinition(event.testCase.getUri(), event.testCase.getLine());
        currentScenarioParameters = getExamplesAsParameters(scenarioDefinition, event.testCase.getLine());

        for(String testCaseId : testCaseIds){
            ZephyrTestCase testCase = new ZephyrTestCase(testCaseId);
            logger.debug("Zephyr: Test Case: " + testCase.toString());

            ProjectInfo projectInfo = getProjectInfo(testCase);
            logger.debug("Zephyr: Project Info: " + projectInfo.toString());

            ProjectVersion projectVersion = getProjectVersion(projectInfo);
            logger.debug("Zephyr: Project Version: " + projectVersion.toString());

            ZephyrTestCycle testCycle = getTestCycle(projectInfo, projectVersion);
            logger.debug("Zephyr: Test Cycle: " + testCycle.toString());

            logger.debug("Zephyr: add " + testCase.toString() + " to " + testCycle.toString());
            zapiClient.addTestsToCycle(testCycle, testCase);

            logger.debug("Zephyr: update execution status for " + testCase.toString());
            udpateExecutionStatus(testCycle, testCase, event.result, getDefectKeysFromTags(event));
        }
    }

    private String[] getDefectKeysFromTags(TestCaseFinished event){
        return event.testCase.getTags().stream()
                .filter(tag -> Pattern.matches(issuePattern, tag.getName()))
                .map(tag -> {
                    Matcher issueMatcher = Pattern.compile(issuePattern).matcher(tag.getName());
                    issueMatcher.find();
                    return issueMatcher.group(1);
                }).toArray(String[]::new);
    }

    private ProjectInfo getProjectInfo(ZephyrTestCase testCase){
        synchronized (projectInfoMap){
            return projectInfoMap.computeIfAbsent(
                    testCase.getProjectKey(),
                    projectKey -> zapiClient.getProjectInfo(projectKey)
            );
        }
    }

    private ProjectVersion getProjectVersion(ProjectInfo projectInfo){
        ProjectVersions projectVersions;
        synchronized (projectVersionsMap){
            projectVersions = projectVersionsMap.computeIfAbsent(
                    projectInfo.getId(),
                    id -> zapiClient.getVersions(id)
            );

        }

        return Stream.concat(
                projectVersions.getReleasedVersions().stream(),
                projectVersions.getUnreleasedVersions().stream()
        ).filter(version -> version.getLabel().equalsIgnoreCase(versionDetector.getVersion()))
                .findFirst()
                .orElse(projectVersions.getUnreleasedVersions().get(0));
    }

    private synchronized ZephyrTestCycle getTestCycle(ProjectInfo projectInfo, ProjectVersion projectVersion){
        synchronized (testCyclesMap){
            return testCyclesMap.computeIfAbsent(
                    currentFeatureName,
                    feature -> {
                        String testCycleName = Optional.ofNullable(feature).orElse("CUCUMBER");
                        logger.debug(String.format("Zephyr: create test cycle with name \"%s\"", testCycleName));

                        ZephyrTestCycle testCycle = zapiClient.getTestCycle(testCycleName, projectInfo, projectVersion);
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
    }

    private Execution udpateExecutionStatus(
            ZephyrTestCycle testCycle,
            ZephyrTestCase testCase,
            Result currentResult,
            String... defects
    ) {
        synchronized (syncObjectForExecutionUpdates){
            ZephyrStatus zephyrCurrentStatus = ZephyrStatus.fromCucumberStatus(currentResult.getStatus().lowerCaseName());

            Execution execution = zapiClient.getExecution(testCycle, testCase);

            if (ZephyrStatus.fromId(execution.getExecutionStatus()) == ZephyrStatus.UNEXECUTED
                    || ZephyrStatus.fromId(execution.getExecutionStatus()) == ZephyrStatus.PASSED){
                execution = zapiClient.udpateExecutionStatus(execution, zephyrCurrentStatus.getId());
            }

            if(zephyrCurrentStatus != ZephyrStatus.PASSED){
                String comment = execution.getComment();
                if(StringUtils.isNotEmpty(comment)) {
                    comment += "\n";
                }
                comment += currentResult.getStatus().firstLetterCapitalizedName();
                if(StringUtils.isNotEmpty(currentScenarioParameters)) {
                    comment += ": " + currentScenarioParameters;
                }
                comment += "\n==================";
                execution = zapiClient.udpateExecutionComment(execution, comment);

                if(ArrayUtils.isNotEmpty(defects)) {
                    execution = zapiClient.udpateExecutionDefectsList(execution, defects);
                }

//            if(currentResult.getError() != null){
//                zapiClient.addAttachment(
//                        execution,
//                        ExceptionUtils.getMessage(currentResult.getError())
//                                + "\n"
//                                + ExceptionUtils.getStackTrace(currentResult.getError())
//                );
//            }
            }

            return execution;
        }
    }

    private String getExamplesAsParameters(final ScenarioDefinition scenarioDefinition, final int testCaseLine) {
        if(scenarioDefinition instanceof ScenarioOutline){
            final Examples examples = ((ScenarioOutline) scenarioDefinition).getExamples().get(0);
            final TableRow row = examples.getTableBody().stream()
                    .filter(example -> example.getLocation().getLine() == testCaseLine)
                    .findFirst().get();

            List<String> parameters =
                    row.getCells().stream()
                            .map(cell -> "[" + cell.getValue() + "]")
                            .collect(Collectors.toList());

            return parameters.toString();
        } else {
            return "";
        }
    }
}

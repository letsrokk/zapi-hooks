package org.fxclub.qa.zapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.fxclub.qa.zapi.core.*;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

public class ZAPIClient {

    public static final String FAILED = "failed";
    public static final String PASSED = "passed";
    public static final String SKIPPED = "skipped";
    public static final String UNEXECUTED = "unexecuted";
    public static final String IN_PROGRESS = "wip";
    public static final String BLOCKED = "blocked";

    private String getStatusId(String status){
        switch (status) {
            case PASSED:
                return "1";
            case FAILED:
                return "2";
            case IN_PROGRESS:
                return "3";
            case BLOCKED:
                return "4";
            case SKIPPED:
            case UNEXECUTED:
            default:
                return "-1";

        }
    }

    private String jiraUrl;
    private String username;
    private String password;

    private RequestSpecification spec;

    public ZAPIClient(){
        jiraUrl = Optional.ofNullable(System.getenv("ZAPI_JIRA_URL")).orElse("https://jira.example.org");
        if(!jiraUrl.endsWith("/"))
            jiraUrl += "/";
        username = Optional.ofNullable(System.getenv("ZAPI_JIRA_USERNAME")).orElse("jirauser");
        password = Optional.ofNullable(System.getenv("ZAPI_JIRA_PASSWORD")).orElse("jirapassword");

        spec = RestAssured.given().contentType(ContentType.JSON).auth().preemptive().basic(username, password);
    }

    private HashMap<String, ProjectInfo> projects = new HashMap<>();

    public ProjectInfo getProjectInfo(TestCase testCase){
        if(projects.containsKey(testCase.getProjectKey())){
            return projects.get(testCase.getProjectKey());
        } else {
            return getProjectInfoBykeyOrID(testCase.getProjectKey());
        }
    }

    private ProjectInfo getProjectInfoBykeyOrID(String keyOrId) {
        String requestUrl = jiraUrl + "rest/api/2/project/" + keyOrId;
        ProjectInfo projectInfo = spec
                .when().get(requestUrl)
                .then().extract().body().as(ProjectInfo.class);
        projects.put(projectInfo.getKey(), projectInfo);
        return projectInfo;
    }

    public ProjectVersions getVersions(ProjectInfo projectInfo){
        String requestUrl = jiraUrl + "rest/zapi/latest/util/versionBoard-list?projectId="+projectInfo.getId()+"&versionId=";
        ProjectVersions versions = spec
                .when().get(requestUrl)
                .then().extract().body().as(ProjectVersions.class);
        return versions;
    }

    public TestCycles getCycles(ProjectInfo projectInfo, ProjectVersion projectVersion){
        String requestUrl = jiraUrl + "rest/zapi/latest/cycle" +
                "?projectId=" + projectInfo.getId() +
                "&versionId=" + projectVersion.getValue();

        HttpAuthenticationFeature basicAuthFeature = HttpAuthenticationFeature.basicBuilder().credentials(username, password).build();
        Client client = ClientBuilder.newClient().register(basicAuthFeature);
        javax.ws.rs.core.Response response = client.target(requestUrl).request(MediaType.APPLICATION_JSON_TYPE).get();
        String responseString = response.readEntity(String.class);

        ObjectMapper mapper = new ObjectMapper();
        TestCycles testCycles;
        try {
            testCycles = mapper.readValue(responseString, TestCycles.class);
        } catch (IOException e) {
            testCycles = new TestCycles();
        }
        return testCycles;
    }

    public TestCycle getTestCycleById(String id){
        String requestUrl = jiraUrl + "rest/zapi/latest/cycle/" + id;
        TestCycle testCycle = spec
                .when().get(requestUrl)
                .then().extract().body().as(TestCycle.class);

        ProjectInfo projectInfo = getProjectInfoBykeyOrID(""+testCycle.getProjectId());
        testCycle.setId(Integer.parseInt(id));
        testCycle.setProjectKey(projectInfo.getKey());

        return testCycle;
    }

    @SuppressWarnings("unchecked")
    public TestCycle createTestCycle(String name, ProjectInfo projectInfo, ProjectVersion projectVersion){
        String requestUrl = jiraUrl + "rest/zapi/latest/cycle";

        JSONObject createCycleBody = new JSONObject();
        createCycleBody.put("clonedCycleId","");
        createCycleBody.put("name",name);
        createCycleBody.put("environment","AUTOMATION");
        createCycleBody.put("description","Automatically generated test cycle");
        String dateString = new SimpleDateFormat("d/MMM/yy").format(new Date());
        createCycleBody.put("startDate",dateString);
        createCycleBody.put("endDate",dateString);
        createCycleBody.put("projectId",projectInfo.getId());
        createCycleBody.put("versionId",projectVersion.getValue());
        createCycleBody.put("sprintId","1");

        Response response =
                spec.when()
                        .with().contentType(ContentType.JSON).body(createCycleBody.toJSONString())
                        .post(requestUrl);

        String newCycleId = response.then().extract().jsonPath().get("id");

        return getTestCycleById(newCycleId);
    }

    private File zapiLockFile = new File("target/zapiLockFile.txt");

    public synchronized TestCycle getOrCreateTestCycle(String name, ProjectInfo projectInfo, ProjectVersion projectVersion) throws IOException {
        zapiLockFile.createNewFile();
        FileChannel fileChannel = new RandomAccessFile(zapiLockFile, "rw").getChannel();
        FileLock fileLock = fileChannel.lock();
        try{
            TestCycles testCycles = getCycles(projectInfo, projectVersion);
            TestCycle testCycle = testCycles.searchTestCycle(name, projectVersion);
            if(testCycle == null){
                testCycle = createTestCycle(name, projectInfo, projectVersion);
            }
            return testCycle;
        }catch (Exception e){
            throw e;
        } finally {
            fileLock.release();
        }
    }

    @SuppressWarnings("unchecked")
    public void addTestsToCycle(TestCycle testCycle, TestCase... testCases) {
        String requestUrl = jiraUrl + "rest/zapi/latest/execution/addTestsToCycle/";

        JSONObject createCycleBody = new JSONObject();

        JSONArray issues = new JSONArray();
        for(TestCase testCase : testCases)
            issues.add(testCase.getIssueKey());

        createCycleBody.put("issues",issues);
        createCycleBody.put("versionId",testCycle.getVersionId());
        createCycleBody.put("cycleId",testCycle.getId());
        createCycleBody.put("projectId",testCycle.getProjectId());
        createCycleBody.put("method","1");

        spec.when().with().contentType(ContentType.JSON).body(createCycleBody.toJSONString()).post(requestUrl);
    }

    public String getIssueIdByKey(String key){
        String requestUrl = jiraUrl + "rest/api/latest/issue/" + key;
        return spec.when().get(requestUrl).then().extract().jsonPath().get("id");
    }

    @SuppressWarnings("unchecked")
    public Execution getExecution(TestCycle testCycle, TestCase testCase){
        String issueId = getIssueIdByKey(testCase.getIssueKey());
        String requestUrl = jiraUrl + "rest/zapi/latest/execution";

        JSONObject getExecution = new JSONObject();
        getExecution.put("cycleId",testCycle.getId());
        getExecution.put("issueId",issueId);
        getExecution.put("projectId",testCycle.getProjectId());
        getExecution.put("versionId",testCycle.getVersionId());
        getExecution.put("assigneeType","assignee");
        getExecution.put("assignee",username);

        Executions executions = spec
                .when().with().contentType(ContentType.JSON).body(getExecution.toJSONString()).post(requestUrl)
                .then().extract().body().as(Executions.class);
        return  executions.getExecutions().get(0);
    }

    @SuppressWarnings("unchecked")
    public Execution udpatedExecutionStatus(Execution execution, String newStatus){
        String requestUrl = jiraUrl + "rest/zapi/latest/execution/"+execution.getId()+"/execute";

        JSONObject udpatedStatus = new JSONObject();
        udpatedStatus.put("status",newStatus);

        execution = spec
                .when().with().contentType(ContentType.JSON).body(udpatedStatus.toJSONString()).put(requestUrl)
                .then().extract().body().as(Execution.class);
        return  execution;
    }

    public Execution udpateExecutionStatus(TestCycle testCycle, TestCase testCase, String currentStatus) {
        Execution execution = getExecution(testCycle, testCase);
        execution = udpatedExecutionStatus(execution, getStatusId(currentStatus));
        return execution;
    }
}

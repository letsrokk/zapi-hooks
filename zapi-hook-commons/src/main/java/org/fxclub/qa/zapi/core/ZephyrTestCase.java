package org.fxclub.qa.zapi.core;

/**
 * Created by majer-dy on 17/02/2017.
 */
public class ZephyrTestCase {

    private String id;
    private String projectKey;

    public ZephyrTestCase(){}

    public ZephyrTestCase(String testCaseId){
        this.id = testCaseId.split("-")[1];
        this.projectKey = testCaseId.split("-")[0];
    }

    public String getIssueKey(){
        return projectKey + "-" + id;
    }

    public String getId(){
        return this.id;
    }

    public String getProjectKey(){
        return this.projectKey;
    }

    @Override
    public String toString() {
        return "ZephyrTestCase{" +
                "id='" + id + '\'' +
                ", projectKey='" + projectKey + '\'' +
                '}';
    }
}

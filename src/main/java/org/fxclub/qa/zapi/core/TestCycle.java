package org.fxclub.qa.zapi.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by majer-dy on 17/02/2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class TestCycle {

    private int id;
    private String name;
    private String description;

    private int projectId;
    private String projectKey;

    private String versionName;
    private int versionId;

    private int sprintId;
    private String build;
    private String environment;

    private String createdBy;
    private String modifiedBy;

    public TestCycle(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionId() {
        return versionId;
    }

    public void setVersionId(int versionId) {
        this.versionId = versionId;
    }

    public int getSprintId() {
        return sprintId;
    }

    public void setSprintId(int sprintId) {
        this.sprintId = sprintId;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Override
    public String toString() {
        return "TestCycle{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", projectKey='" + projectKey + '\'' +
                ", versionName='" + versionName + '\'' +
                '}';
    }
}

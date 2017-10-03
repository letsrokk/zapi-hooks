package org.fxclub.qa.zapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by majer-dy on 17/02/2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Execution {

    private int id;
    private int orderId;
    private String executionStatus;
    private String comment;
    private String htmlComment;
    private int cycleId;
    private String cycleName;
    private int versionId;
    private String versionName;
    private int projectId;
    private String createdBy;
    private String modifiedBy;
    private int issueId;
    private String issueKey;
    private String summary;
    private String issueDescription;
    private String label;
    private String component;
    private String projectKey;

    public Execution(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getExecutionStatus() {
        return executionStatus;
    }

    public void setExecutionStatus(String executionStatus) {
        this.executionStatus = executionStatus;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getHtmlComment() {
        return htmlComment;
    }

    public void setHtmlComment(String htmlComment) {
        this.htmlComment = htmlComment;
    }

    public int getCycleId() {
        return cycleId;
    }

    public void setCycleId(int cycleId) {
        this.cycleId = cycleId;
    }

    public String getCycleName() {
        return cycleName;
    }

    public void setCycleName(String cycleName) {
        this.cycleName = cycleName;
    }

    public int getVersionId() {
        return versionId;
    }

    public void setVersionId(int versionId) {
        this.versionId = versionId;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
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

    public void setmodifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public int getIssueId() {
        return issueId;
    }

    public void setIssueId(int issueId) {
        this.issueId = issueId;
    }

    public String getIssueKey() {
        return issueKey;
    }

    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    @Override
    public String toString() {
        return "Execution{" +
                "id=" + id +
                ", cycleId=" + cycleId +
                ", cycleName='" + cycleName + '\'' +
                ", versionId=" + versionId +
                ", versionName='" + versionName + '\'' +
                ", projectId=" + projectId +
                ", issueId=" + issueId +
                ", issueKey='" + issueKey + '\'' +
                ", projectKey='" + projectKey + '\'' +
                '}';
    }
}

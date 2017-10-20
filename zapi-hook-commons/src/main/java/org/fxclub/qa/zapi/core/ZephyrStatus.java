package org.fxclub.qa.zapi.core;

import org.fxclub.qa.zapi.ZAPIClient;

import java.util.Arrays;

public enum ZephyrStatus {

    PASSED("passed", "1"),
    FAILED("failed", "2"),
    IN_PROGRESS("in_progress", "3"),
    BLOCKED("blocked", "4"),
    UNEXECUTED("unexecuted", "-1");

    private String status;
    private String id;

    ZephyrStatus(String status, String id){
        this.status = status;
        this.id = id;
    }

    public String toLowerCaseName(){
        return status.toLowerCase();
    }

    public String getId(){
        return id;
    }

    public static ZephyrStatus fromId(String executionStatus) {
        return Arrays.stream(ZephyrStatus.values())
                .filter(status -> status.getId().equals(executionStatus))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("cannot find Execution status with Id " + executionStatus));
    }

    public static ZephyrStatus fromCucumberStatus(String cucumberStatus){
        switch (cucumberStatus){
            case "passed":
                return PASSED;
            case "failed":
                return FAILED;
            default:
                return UNEXECUTED;
        }
    }

}

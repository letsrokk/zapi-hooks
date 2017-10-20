package org.fxclub.qa.zapi.core;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ZephyrTestCycles {

    @JsonProperty("recordsCount")
    private int recordsCount;

    private List<String> testCycleIds = new ArrayList<>();
    private List<ZephyrTestCycle> testCycles = new ArrayList<>();

    public ZephyrTestCycles() {}

    public void setRecordsCount(int recordsCount){
        this.recordsCount = recordsCount;
    }

    public int getRecordsCount(){
        return recordsCount;
    }

    @JsonAnySetter
    public void setDynamicTestCycles(String name, ZephyrTestCycle cc) {
        testCycleIds.add(name);
        cc.setId(Integer.parseInt(name));
        testCycles.add(cc);
    }

    public List<ZephyrTestCycle> getTestCycles() {
        return testCycles;
    }

    public void setTestCycle(List<ZephyrTestCycle> testCycles) {
        this.testCycles = testCycles;
    }

    @Override
    public String toString() {
        return "ZephyrTestCycles{" +
                "recordsCount=" + recordsCount +
                ", testCycles=" + testCycles +
                '}';
    }

    public ZephyrTestCycle searchTestCycle(String name, ProjectVersion projectVersion) {
        for(ZephyrTestCycle testCycle : testCycles){
            if(StringUtils.equals(name, testCycle.getName()) && StringUtils.equals(testCycle.getVersionName(), projectVersion.getLabel()))
                return testCycle;
        }
        return null;
    }
}

package org.fxclub.qa.zapi.core;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class TestCycles {

    @JsonProperty("recordsCount")
    private int recordsCount;

    private List<String> testCycleIds = new ArrayList<>();
    private List<TestCycle> testCycles = new ArrayList<>();

    public TestCycles() {}

    public void setRecordsCount(int recordsCount){
        this.recordsCount = recordsCount;
    }

    public int getRecordsCount(){
        return recordsCount;
    }

    @JsonAnySetter
    public void setDynamicTestCycles(String name, TestCycle cc) {
        testCycleIds.add(name);
        cc.setId(Integer.parseInt(name));
        testCycles.add(cc);
    }

    public List<TestCycle> getTestCycles() {
        return testCycles;
    }

    public void setTestCycle(List<TestCycle> testCycles) {
        this.testCycles = testCycles;
    }

    @Override
    public String toString() {
        return "TestCycles{" +
                "recordsCount=" + recordsCount +
                ", testCycles=" + testCycles +
                '}';
    }

    public TestCycle searchTestCycle(String name, ProjectVersion projectVersion) {
        for(TestCycle testCycle : testCycles){
            if(StringUtils.equals(name, testCycle.getName()) && StringUtils.equals(testCycle.getVersionName(), projectVersion.getLabel()))
                return testCycle;
        }
        return null;
    }
}

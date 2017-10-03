package org.fxclub.qa.zapi.core;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class TestCyclesByVersions {

    private List<String> nameList = new ArrayList<>();
    private List<List<TestCycles>> testCyclesByVersions = new ArrayList<>();

    @JsonAnySetter
    public void setDynamicTestCyclesByVersions(String name, List<TestCycles> tcv) {
        nameList.add(name);
        testCyclesByVersions.add(tcv);
    }

    public List<List<TestCycles>> getTestCyclesByVersions() {
        return testCyclesByVersions;
    }

    public void setTestCyclesByVersions(List<List<TestCycles>> testCyclesByVersions) {
        this.testCyclesByVersions = testCyclesByVersions;
    }

    public TestCycle searchTestCycle(String name, ProjectVersion projectVersion) {
        for(List<TestCycles> testCyclesList : testCyclesByVersions){
            for(TestCycles testCycles : testCyclesList){
                for(TestCycle testCycle : testCycles.getTestCycles()){
                    if(StringUtils.equals(name, testCycle.getName()) && StringUtils.equals(testCycle.getVersionName(), projectVersion.getLabel()))
                        return testCycle;
                }
            }
        }
        return null;
    }
}

package org.fxclub.qa.zapi.core;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ZephyrTestCyclesByVersions {

    private List<String> nameList = new ArrayList<>();
    private List<List<ZephyrTestCycles>> testCyclesByVersions = new ArrayList<>();

    @JsonAnySetter
    public void setDynamicTestCyclesByVersions(String name, List<ZephyrTestCycles> tcv) {
        nameList.add(name);
        testCyclesByVersions.add(tcv);
    }

    public List<List<ZephyrTestCycles>> getTestCyclesByVersions() {
        return testCyclesByVersions;
    }

    public void setTestCyclesByVersions(List<List<ZephyrTestCycles>> testCyclesByVersions) {
        this.testCyclesByVersions = testCyclesByVersions;
    }

    public ZephyrTestCycle searchTestCycle(String name, ProjectVersion projectVersion) {
        for(List<ZephyrTestCycles> testCyclesList : testCyclesByVersions){
            for(ZephyrTestCycles testCycles : testCyclesList){
                for(ZephyrTestCycle testCycle : testCycles.getTestCycles()){
                    if(StringUtils.equals(name, testCycle.getName()) && StringUtils.equals(testCycle.getVersionName(), projectVersion.getLabel()))
                        return testCycle;
                }
            }
        }
        return null;
    }
}

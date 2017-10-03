package org.fxclub.qa.zapi;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by majer-dy on 17/02/2017.
 */
public class Executions {

    private List<String> executionIds = new ArrayList<>();
    private List<Execution> executions = new ArrayList<>();

    public Executions() {}

    @JsonAnySetter
    public void setDynamicExecuitions(String name, Execution cc) {
        executionIds.add(name);
        cc.setId(Integer.parseInt(name));
        executions.add(cc);
    }

    public List<Execution> getExecutions() {
        return executions;
    }

    public void setExecutions(List<Execution> executions) {
        this.executions = executions;
    }
}

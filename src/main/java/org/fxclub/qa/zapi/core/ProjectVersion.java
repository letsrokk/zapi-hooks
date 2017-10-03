package org.fxclub.qa.zapi.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by majer-dy on 17/02/2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectVersion {

    private boolean archived;
    private String label;
    private int value;

    public ProjectVersion(){}

    public boolean isArchived() {
        return archived;
    }

    public String getLabel() {
        return label;
    }

    public int getValue() {
        return value;
    }
}

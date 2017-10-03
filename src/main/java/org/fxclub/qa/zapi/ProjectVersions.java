package org.fxclub.qa.zapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by majer-dy on 17/02/2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectVersions {

    private String type;
    private boolean hasAccessToSoftware;
    private List<ProjectVersion> releasedVersions;
    private List<ProjectVersion> unreleasedVersions;

    public ProjectVersions(){}

    public String getType() {
        return type;
    }

    public boolean isHasAccessToSoftware() {
        return hasAccessToSoftware;
    }

    public List<ProjectVersion> getReleasedVersions() {
        return releasedVersions;
    }

    public List<ProjectVersion> getUnreleasedVersions() {
        return unreleasedVersions;
    }
}

package org.fxclub.qa.zapi.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectInfo {

    private int id;
    private String self;
    private String key;

    public ProjectInfo(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "ProjectInfo{" +
                "id=" + id +
                ", self='" + self + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}

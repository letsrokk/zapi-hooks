package org.fxclub.qa.zapi.core;

public class DefaultVersionDetector implements VersionDetector {
    @Override
    public synchronized String getVersion() {
        return "Unscheduled";
    }
}

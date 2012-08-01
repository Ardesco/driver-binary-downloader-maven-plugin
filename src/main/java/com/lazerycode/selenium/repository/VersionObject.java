package com.lazerycode.selenium.repository;

import java.util.HashMap;
import java.util.Map;

public class VersionObject {
    private Map version = new HashMap<String, OSObject>();

    public void addStandaloneExecutableType(String value) {
        if (!this.version.containsKey(value)) {
            this.version.put(value, new OSObject());
        }
    }

    public OSObject returnObject(String version) {
        if (!this.version.containsKey(version)) {
            return (OSObject) this.version.get(version);
        }
        return null;
    }

}

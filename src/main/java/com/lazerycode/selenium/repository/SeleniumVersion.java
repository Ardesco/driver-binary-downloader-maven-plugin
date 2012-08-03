package com.lazerycode.selenium.repository;

import java.util.HashMap;
import java.util.Map;

//TODO obselete?

public class SeleniumVersion {
    private Map version = new HashMap<String, String>();

    public void addSeleniumVersion(String value) {
        if (!this.version.containsKey(value)) {
            this.version.put(value, new OperatingSystem());
        }
    }

    public String returnSeleniumVersion(String version) {
        if (!this.version.containsKey(version)) {
            return (String) this.version.get(version);
        }
        return null;
    }

}

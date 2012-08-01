package com.lazerycode.selenium.repository;

import com.lazerycode.selenium.Driver;
import com.lazerycode.selenium.OS;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.HashMap;
import java.util.Map;

public class OperatingSystem {

    private Map operatingSystem = new HashMap<OS, BitRate>();

    public void addOperatingSystem(String value) throws MojoExecutionException{
        OS osType;
        try {
            osType = OS.valueOf(value);
        } catch (NullPointerException ex) {
            throw new MojoExecutionException("Invalid Operating System specified!");
        }
        if (!this.operatingSystem.containsKey(osType)) {
            this.operatingSystem.put(osType, new BitRate());
        }
    }

    public OS returnOperatingSystem(Driver driverType) {
        if (!this.operatingSystem.containsKey(driverType)) {
            return (OS) this.operatingSystem.get(driverType);
        }
        return null;
    }
}

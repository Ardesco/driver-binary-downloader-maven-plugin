package com.lazerycode.selenium.repository;

import com.lazerycode.selenium.Bit;
import com.lazerycode.selenium.Driver;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.HashMap;
import java.util.Map;

public class BitRate {
    private Map standAloneServerBitRate = new HashMap<Driver, FileDetails>();

    public void standaloneServerBitRate(String value) throws MojoExecutionException {
        Bit bitRate;
        try {
            bitRate = Bit.valueOf(value);
        } catch (NullPointerException ex) {
            throw new MojoExecutionException("Invalid BitRate specified!");
        }
        if (!this.standAloneServerBitRate.containsKey(bitRate)) {
            this.standAloneServerBitRate.put(bitRate, new FileDetails());
        }
    }

    public FileDetails returnBitRate(Bit driverType) {
        if (!this.standAloneServerBitRate.containsKey(driverType)) {
            return (FileDetails) this.standAloneServerBitRate.get(driverType);
        }
        return null;
    }
}

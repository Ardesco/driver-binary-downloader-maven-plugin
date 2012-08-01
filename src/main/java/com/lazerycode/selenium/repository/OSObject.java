package com.lazerycode.selenium.repository;

import com.lazerycode.selenium.Driver;
import com.lazerycode.selenium.OperatingSystem;

import java.util.HashMap;
import java.util.Map;

public class OSObject {

    private Map operatingSystem = new HashMap<OperatingSystem, BitBit>();

    public void addStandaloneExecutableType(String value) {
        OperatingSystem type = OperatingSystem.WINDOWS;
        if (!this.operatingSystem.containsKey(type)) {
            this.operatingSystem.put(type, new BitBit());
        }
    }

    public BitBit returnObject(Driver driverType) {
        if (!this.operatingSystem.containsKey(driverType)) {
            return (BitBit) this.operatingSystem.get(driverType);
        }
        return null;
    }
}

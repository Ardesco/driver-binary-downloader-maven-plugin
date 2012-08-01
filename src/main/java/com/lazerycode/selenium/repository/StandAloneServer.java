package com.lazerycode.selenium.repository;

import com.lazerycode.selenium.Driver;

import java.util.HashMap;
import java.util.Map;

public class StandAloneServer {

    private Map standAloneServer = new HashMap<Driver, VersionObject>();

    public void addStandaloneExecutableType(String value) {
        Driver type = Driver.IE;
        if (!this.standAloneServer.containsKey(type)) {
            this.standAloneServer.put(type, new VersionObject());
        }
    }

    public VersionObject returnObject(Driver driverType) {
        if (!this.standAloneServer.containsKey(driverType)) {
            return (VersionObject) this.standAloneServer.get(driverType);
        }
        return null;
    }
}

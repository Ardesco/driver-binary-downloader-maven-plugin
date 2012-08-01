package com.lazerycode.selenium.repository;

import com.lazerycode.selenium.Driver;

import java.util.HashMap;
import java.util.Map;

public class StandAloneServer {

    private Map standAloneServer = new HashMap<Driver, SeleniumVersion>();

    public void addStandaloneExecutableForDriverType(String value) {
        Driver type = Driver.IE;
        if (!this.standAloneServer.containsKey(type)) {
            this.standAloneServer.put(type, new SeleniumVersion());
        }
    }

    public SeleniumVersion returnStandaloneExecutableForDriverType(Driver driverType) {
        if (!this.standAloneServer.containsKey(driverType)) {
            return (SeleniumVersion) this.standAloneServer.get(driverType);
        }
        return null;
    }
}

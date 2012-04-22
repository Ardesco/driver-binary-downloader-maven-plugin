package com.lazerycode.selenium;

public enum Driver {
    IE("internetexplorer"),
    GOOGLECHROME("googlechrome");

    private final String driverName;

    Driver(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverName() {
        return this.driverName;
    }
}

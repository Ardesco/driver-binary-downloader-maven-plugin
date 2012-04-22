package com.lazerycode.selenium;

public enum OperatingSystem {
    WINDOWS("windows"),
    LINUX("linux"),
    OSX("mac");

    private final String osName;

    OperatingSystem(String osName) {
        this.osName = osName;
    }

    public String getOsName() {
        return this.osName;
    }
}

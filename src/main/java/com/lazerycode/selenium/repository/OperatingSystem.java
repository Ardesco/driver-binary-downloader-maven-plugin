package com.lazerycode.selenium.repository;

import java.util.HashSet;

public enum OperatingSystem {

    WINDOWS("windows"),
    OSX("mac"),
    LINUX("linux");

    private String operatingSystemName;

    OperatingSystem(String operatingSystemName) {
        this.operatingSystemName = operatingSystemName;
    }

    String getOperatingSystemType() {
        return operatingSystemName;
    }

    public static OperatingSystem getOperatingSystem(String osName) {
        for (OperatingSystem operatingSystemName : values()) {
            if (osName.toLowerCase().contains(operatingSystemName.getOperatingSystemType())) {
                return operatingSystemName;
            }
        }

        throw new IllegalArgumentException("Unrecognised operating system name '" + osName + "'");
    }

    public static HashSet<OperatingSystem> getCurrentOperatingSystemAsAHashSet() {
        String currentOperatingSystemName = System.getProperties().getProperty("os.name");

        HashSet<OperatingSystem> listOfOperatingSystems = new HashSet<OperatingSystem>();
        listOfOperatingSystems.add(getOperatingSystem(currentOperatingSystemName));

        return listOfOperatingSystems;
    }

    public static OperatingSystem getCurrentOperatingSystem() {
        String currentOperatingSystemName = System.getProperties().getProperty("os.name");

        return getOperatingSystem(currentOperatingSystemName);
    }
}

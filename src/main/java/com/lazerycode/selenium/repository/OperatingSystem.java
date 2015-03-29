package com.lazerycode.selenium.repository;

import java.util.ArrayList;

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

    public static ArrayList<OperatingSystem> getCurrentOperatingSystemAsAnArrayList() {
        String currentOperatingSystemName = System.getProperties().getProperty("os.name");

        ArrayList<OperatingSystem> listOfOperatingSystems = new ArrayList<OperatingSystem>();
        listOfOperatingSystems.add(getOperatingSystem(currentOperatingSystemName));

        return listOfOperatingSystems;
    }
}

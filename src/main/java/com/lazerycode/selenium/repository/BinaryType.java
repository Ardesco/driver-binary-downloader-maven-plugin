package com.lazerycode.selenium.repository;

import java.util.ArrayList;

public enum BinaryType {
    INTERNETEXPLORER(
            new ArrayList<String>() {{
                add("IEDriverServer.exe");
            }},
            "webdriver.ie.driver"),
    GOOGLECHROME(
            new ArrayList<String>() {{
                add("chromedriver.exe");
                add("chromedriver");
            }},
            "webdriver.chrome.driver"),
    PHANTOMJS(
            new ArrayList<String>() {{
                add("phantomjs.exe");
                add("phantomjs");
            }},
            "phantomjs.binary.path"),
    OPERACHROMIUM(
            new ArrayList<String>() {{
                add("operadriver.exe");
                add("operadriver");
            }},
            "webdriver.opera.driver"),
    MARIONETTE(
            new ArrayList<String>() {{
                add("wires.*");
            }},
            "webdriver.opera.driver");

    private final ArrayList<String> binaryFilenames;
    private final String driverSystemProperty;

    BinaryType(ArrayList<String> binaryFilenames, String driverSystemProperty) {
        this.binaryFilenames = binaryFilenames;
        this.driverSystemProperty = driverSystemProperty;
    }

    public ArrayList<String> getBinaryFilenames() {
        return binaryFilenames;
    }

    public String getDriverSystemProperty() {
        return driverSystemProperty;
    }

    public String getBinaryTypeAsString() {
        return this.toString().toLowerCase();
    }
}
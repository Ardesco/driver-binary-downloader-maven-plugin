package com.lazerycode.selenium.repository;

import java.util.List;

import static java.util.Arrays.asList;

public enum BinaryType {
    INTERNETEXPLORER(
            asList(
                    "IEDriverServer.exe"
            ),
            "webdriver.ie.driver"),
    GOOGLECHROME(
            asList(
                    "chromedriver.exe",
                    "chromedriver"
            ),
            "webdriver.chrome.driver"),
    PHANTOMJS(
            asList(
                    "phantomjs.exe",
                    "phantomjs"
            ),
            "phantomjs.binary.path"),
    OPERACHROMIUM(
            asList(
                    "operadriver.exe",
                    "operadriver"
            ),
            "webdriver.opera.driver"),
    MARIONETTE(
            asList(
                    "wires",
                    "wires.exe",
                    "geckodriver",
                    "geckodriver.exe"
            ),
            "webdriver.gecko.driver"),
    EDGE(
            asList(
                    "MicrosoftWebDriver.exe"
            ),
            "webdriver.edge.driver"),
    FIREFOX(
            asList(
                "*",
                "firefox.exe",
                "firefox"
            ),
            "webdriver.firefox.bin");

    private final List<String> binaryFilenames;
    private final String driverSystemProperty;

    BinaryType(List<String> binaryFilenames, String driverSystemProperty) {
        this.binaryFilenames = binaryFilenames;
        this.driverSystemProperty = driverSystemProperty;
    }

    public List<String> getBinaryFilenames() {
        return binaryFilenames;
    }

    public String getDriverSystemProperty() {
        return driverSystemProperty;
    }

    public String getBinaryTypeAsString() {
        return this.toString().toLowerCase();
    }
}
package com.lazerycode.selenium.download;

import java.util.ArrayList;

public enum BinaryFileNames {
    INTERNETEXPLORER(new ArrayList<String>() {{
        add("IEDriverServer.exe");
    }}),
    GOOGLECHROME(new ArrayList<String>() {{
        add("chromedriver.exe");
        add("chromedriver");
    }}),
    PHANTOMJS(new ArrayList<String>() {{
        add("phantomjs.exe");
        add("phantomjs");
    }});

    private final ArrayList<String> binaryFilenames;

    BinaryFileNames(ArrayList<String> binaryFilenames) {
        this.binaryFilenames = binaryFilenames;
    }

    public ArrayList<String> getBinaryFilenames() {
        return binaryFilenames;
    }

}
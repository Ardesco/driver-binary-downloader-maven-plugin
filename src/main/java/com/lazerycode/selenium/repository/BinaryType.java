package com.lazerycode.selenium.repository;

import java.util.ArrayList;

public enum BinaryType {
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
    }}),
    OPERACHROMIUM(new ArrayList<String>() {{
        add("operadriver.exe");
        add("operadriver");
    }});

    private final ArrayList<String> binaryFilenames;

    BinaryType(ArrayList<String> binaryFilenames) {
        this.binaryFilenames = binaryFilenames;
    }

    public ArrayList<String> getBinaryFilenames() {
        return binaryFilenames;
    }

    public String getBinaryTypeAsString(){
        return this.toString().toLowerCase();
    }

}
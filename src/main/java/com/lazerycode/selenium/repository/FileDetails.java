package com.lazerycode.selenium.repository;

public class FileDetails {

    private String fileLocation;
    private String hashType;
    private String hash;

    public void setFileLocation(String value) {
        this.fileLocation = value;
    }

    public String getFileLocation() {
        return this.fileLocation;
    }

    public void setHash(String value) {
        this.hash = value;
    }

    public String getHash() {
        return this.hash;
    }

    public void setHashType(String value) {
        this.hashType = value;
    }

    public String getHashType() {
        return this.hashType;
    }
}

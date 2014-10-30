package com.lazerycode.selenium.repository;

import com.lazerycode.selenium.hash.HashType;

import java.net.MalformedURLException;
import java.net.URL;

public class FileDetails {

    private URL fileLocation;
    private HashType hashType;
    private String hash;

    public FileDetails(String fileLocation, String hashType, String hash) throws MalformedURLException, IllegalArgumentException {
        this.fileLocation = new URL(fileLocation);
        if (null != hash && null != hashType) {
            setHash(hash, hashType);
        }
    }

    public FileDetails(URL fileLocation, HashType hashType, String hash) throws IllegalArgumentException {
        this.fileLocation = fileLocation;
        this.hashType = hashType;
        this.hash = hash;
    }

    private void setHash(String hash, String hashType) {
        HashType calculatedHashType = HashType.valueOf(hashType.toUpperCase());
        if (calculatedHashType.matchesStructureOf(hash)) {
            this.hashType = calculatedHashType;
            this.hash = hash;
        } else {
            throw new IllegalArgumentException(hash + " is not a valid " + calculatedHashType.toString() + " hash!");
        }
    }

    public URL getFileLocation() {
        return this.fileLocation;
    }

    public String getHash() {
        return this.hash;
    }

    public HashType getHashType() {
        return this.hashType;
    }
}

package com.lazerycode.selenium.repository;

public class FileDetails {

    private String filelocation;
    private String hash;
    private String hashtype;

    public void fileLocation(String value){
        this.filelocation = value;
    }

    public String fileLocation(){
        return this.filelocation;
    }

    public void hash(String value){
        this.hash = value;
    }

    public String hash(){
        return this.hash;
    }

    public void hashtype(String value){
        this.hashtype = value;
    }

    public String hashtype(){
        return this.hashtype;
    }
}

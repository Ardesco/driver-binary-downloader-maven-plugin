package com.lazerycode.selenium.hash;

public enum HashType {
    MD5("[a-fA-F0-9]{32}"),
    SHA1("[a-fA-F0-9]{40}");

    private final String hashMatchingPattern;

    HashType(String hashMatchingPattern) {
        this.hashMatchingPattern = hashMatchingPattern;
    }

    public boolean matchesStructureOf(String hash) {
        return hash.matches(this.hashMatchingPattern);
    }

}
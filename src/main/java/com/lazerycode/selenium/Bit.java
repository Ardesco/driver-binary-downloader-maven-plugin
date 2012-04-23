package com.lazerycode.selenium;

public enum Bit {
    SIXTYFOURBIT("sixtyfourbit='true'"),
    THIRTYTWOBIT("thirtytwobit='true'");

    private final String bitValue;

    Bit(String bitValue) {
        this.bitValue = bitValue;
    }

    public String getBitValue() {
        return this.bitValue;
    }
}

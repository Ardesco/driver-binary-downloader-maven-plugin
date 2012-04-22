package com.lazerycode.selenium;

public enum Bit {
    SIXTYFOURBIT(64),
    THIRTYTWOBIT(32);

    private final int bitValue;

    Bit(int bitValue) {
        this.bitValue = bitValue;
    }

    public int getBitValue() {
        return this.bitValue;
    }
}

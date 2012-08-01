package com.lazerycode.selenium.repository;

import com.lazerycode.selenium.Bit;
import com.lazerycode.selenium.Driver;

import java.util.HashMap;
import java.util.Map;

public class BitBit {
    private Map standAloneServerBitRate = new HashMap<Driver, FileDetails>();

    public void addStandaloneExecutableType(String value) {
        Bit type = Bit.SIXTYFOURBIT;
        if (!this.standAloneServerBitRate.containsKey(type)) {
            this.standAloneServerBitRate.put(type, new FileDetails());
        }
    }

    public FileDetails returnObject(Bit driverType) {
        if (!this.standAloneServerBitRate.containsKey(driverType)) {
            return (FileDetails) this.standAloneServerBitRate.get(driverType);
        }
        return null;
    }
}

package com.lazerycode.selenium.configuration;

import com.lazerycode.selenium.Bit;

import java.util.ArrayList;
import java.util.List;

public class BitRate {

    private boolean sixtyFourBit = false;
    private boolean thirtyTwoBit = true;

    public List<Bit> selectedBitRate(){
        List<Bit> selectedBitRate = new ArrayList<Bit>();
        if (this.sixtyFourBit) selectedBitRate.add(Bit.SIXTYFOURBIT);
        if (this.thirtyTwoBit) selectedBitRate.add(Bit.THIRTYTWOBIT);
        return selectedBitRate;
    }
}

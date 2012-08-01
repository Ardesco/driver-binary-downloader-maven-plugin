package com.lazerycode.selenium.configuration;

import com.lazerycode.selenium.OS;

import java.util.ArrayList;
import java.util.List;

public class OperatingSystems {

    private boolean windows = true;
    private boolean linux = true;
    private boolean mac = true;

    public List<OS> selectedOperatingSystemsList(){
        List<OS> selectedOperatingSystems = new ArrayList<OS>();
        if (this.windows) selectedOperatingSystems.add(OS.WINDOWS);
        if (this.linux) selectedOperatingSystems.add(OS.LINUX);
        if (this.mac) selectedOperatingSystems.add(OS.OSX);
        return selectedOperatingSystems;
    }
}

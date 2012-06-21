package com.lazerycode.selenium.configuration;

import com.lazerycode.selenium.OperatingSystem;

import java.util.ArrayList;
import java.util.List;

public class OperatingSystems {

    private boolean windows = true;
    private boolean linux = true;
    private boolean mac = true;

    public List<OperatingSystem> selectedOperatingSystemsList(){
        List<OperatingSystem> selectedOperatingSystems = new ArrayList<OperatingSystem>();
        if (this.windows) selectedOperatingSystems.add(OperatingSystem.WINDOWS);
        if (this.linux) selectedOperatingSystems.add(OperatingSystem.LINUX);
        if (this.mac) selectedOperatingSystems.add(OperatingSystem.OSX);
        return selectedOperatingSystems;
    }
}

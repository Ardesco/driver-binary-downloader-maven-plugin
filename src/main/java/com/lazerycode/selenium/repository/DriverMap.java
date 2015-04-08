package com.lazerycode.selenium.repository;

import java.util.*;

import static com.lazerycode.selenium.repository.OperatingSystem.getCurrentOperatingSystem;
import static com.lazerycode.selenium.repository.SystemArchitecture.getCurrentSystemArcitecture;

public class DriverMap {

    protected HashMap<DriverContext, TreeMap<String, DriverDetails>> repository = new HashMap<DriverContext, TreeMap<String, DriverDetails>>();

    public TreeMap<String, DriverDetails> getMapForDriverContext(DriverContext driverContext) {
        if (!repository.containsKey(driverContext)) {
            repository.put(driverContext, new TreeMap<String, DriverDetails>());
        }

        return repository.get(driverContext);
    }

    public DriverDetails getDetailsForVersionOfDriverContext(DriverContext driverContext, String version) throws IllegalArgumentException {
        if (!repository.containsKey(driverContext)) {
            throw new IllegalArgumentException("Driver context not found in driver repository");
        }

        TreeMap<String, DriverDetails> driverVersions = repository.get(driverContext);
        DriverDetails detailsToReturn = driverVersions.get(driverVersions.lastKey());
        if (detailsToReturn.hashCode() == 0) {
            throw new NoSuchElementException("No driver version " + version + " exists for the context " + driverContext.toString());
        }

        return detailsToReturn;
    }

    public DriverDetails getDetailsForLatestVersionOfDriverContext(DriverContext driverContext) {
        if (!repository.containsKey(driverContext)) {
            throw new IllegalArgumentException("Driver context not found in driver repository");
        }

        TreeMap<String, DriverDetails> driverVersions = repository.get(driverContext);

        return driverVersions.get(driverVersions.lastKey());
    }

    public Set<DriverContext> getKeys() {
        return repository.keySet();
    }

    public Set<String> getAvailableVersionsForDriverContext(DriverContext driverContext) {
        return repository.get(driverContext).keySet();
    }

    public ArrayList<DriverContext> getDriverContextsForOperatingSystem(OperatingSystem operatingSystem, SystemArchitecture systemArchitecture) {
        ArrayList<DriverContext> matchingContexts = new ArrayList<DriverContext>();
        for (DriverContext driverContext : repository.keySet()) {
            if (driverContext.getOperatingSystem().equals(operatingSystem) && driverContext.getSystemArchitecture().equals(systemArchitecture)) {
                matchingContexts.add(driverContext);
            }
        }

        return matchingContexts;
    }

    public ArrayList<DriverContext> getDriverContextsForCurrentOperatingSystem() {
        OperatingSystem operatingSystem = getCurrentOperatingSystem();
        SystemArchitecture systemArchitecture = getCurrentSystemArcitecture();

        return getDriverContextsForOperatingSystem(operatingSystem, systemArchitecture);
    }
}
package com.lazerycode.selenium.repository;

import java.io.File;

public class DriverContext {
    private final BinaryType driverType;
    private final SystemArchitecture systemArchitecture;
    private final OperatingSystem operatingSystem;

    public SystemArchitecture getSystemArchitecture() {
        return systemArchitecture;
    }

    public OperatingSystem getOperatingSystem() {
        return operatingSystem;
    }

    private DriverContext(BinaryType driverType, OperatingSystem operatingSystem, SystemArchitecture systemArchitecture) {
        this.operatingSystem = operatingSystem;
        this.driverType = driverType;
        this.systemArchitecture = systemArchitecture;
    }

    private DriverContext(String driverType, String operatingSystem, SystemArchitecture systemArchitecture) {
        this.operatingSystem = OperatingSystem.valueOf(operatingSystem.toUpperCase());
        this.driverType = BinaryType.valueOf(driverType.toUpperCase());
        this.systemArchitecture = systemArchitecture;
    }

    public static DriverContext binaryDataFor(OperatingSystem osName, BinaryType browserType, SystemArchitecture architecture) {
        return new DriverContext(browserType, osName, architecture);
    }

    public static DriverContext binaryDataFor(String osName, String browserType, SystemArchitecture architecture) {
        return new DriverContext(browserType, osName, architecture);
    }

    public String buildExtractionPathFromDriverContext() {
        return operatingSystem.toString().toLowerCase() + File.separator + driverType.getBinaryTypeAsString() + File.separator + systemArchitecture.getSystemArchitectureType() + File.separator;
    }

    public BinaryType getBinaryTypeForContext() {
        return driverType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DriverContext that = (DriverContext) o;

        if (driverType != that.driverType) return false;
        if (operatingSystem != that.operatingSystem) return false;
        if (systemArchitecture != that.systemArchitecture) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = driverType.hashCode();
        result = 31 * result + systemArchitecture.hashCode();
        result = 31 * result + operatingSystem.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return this.operatingSystem.getOperatingSystemType() + " - " + this.driverType.toString().toLowerCase() + " - " + this.systemArchitecture.getSystemArchitectureType();
    }
}

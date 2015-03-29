package com.lazerycode.selenium.repository;

import com.lazerycode.selenium.hash.HashType;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import static com.lazerycode.selenium.repository.BinaryType.GOOGLECHROME;
import static com.lazerycode.selenium.repository.DriverContext.binaryDataFor;
import static com.lazerycode.selenium.repository.OperatingSystem.OSX;
import static com.lazerycode.selenium.repository.SystemArchitecture.ARCHITECTURE_64_BIT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

public class DriverMapTest {

    @Test
    public void willReturnEmptyMap() {
        DriverMap driverMap = new DriverMap();
        Map versionMap = driverMap.getMapForDriverContext(binaryDataFor(GOOGLECHROME, OSX, ARCHITECTURE_64_BIT));

        assertThat(versionMap,
                is(instanceOf(TreeMap.class)));
        assertThat(versionMap.size(),
                is(equalTo(0)));
    }

    @Test
    public void willReturnExistingMap() {
        DriverMap driverMap = new DriverMap();
        Map originalVersionMap = driverMap.getMapForDriverContext(binaryDataFor(GOOGLECHROME, OSX, ARCHITECTURE_64_BIT));
        originalVersionMap.put("2.13", new DriverDetails());

        Map versionMap = driverMap.getMapForDriverContext(binaryDataFor(GOOGLECHROME, OSX, ARCHITECTURE_64_BIT));

        assertThat(versionMap,
                is(instanceOf(TreeMap.class)));
        assertThat(versionMap.size(),
                is(equalTo(1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void willThrowAnExceptionWhenTryingToGetSpecificVersionMapForInvalidContext() {
        DriverMap driverMap = new DriverMap();
        driverMap.getDetailsForVersionOfDriverContext(binaryDataFor(GOOGLECHROME, OSX, ARCHITECTURE_64_BIT), "4");
    }

    @Test(expected = NoSuchElementException.class)
    public void willThrowAnExceptionWhenTryingToGetAnInvalidVersion() {
        DriverMap driverMap = new DriverMap();
        Map originalVersionMap = driverMap.getMapForDriverContext(binaryDataFor(GOOGLECHROME, OSX, ARCHITECTURE_64_BIT));
        originalVersionMap.put("2.13", new DriverDetails());

        driverMap.getDetailsForVersionOfDriverContext(binaryDataFor(GOOGLECHROME, OSX, ARCHITECTURE_64_BIT), "4");
    }

    @Test(expected = IllegalArgumentException.class)
    public void willThrowAnExceptionWhenTryingToGetLatestVersionMapForInvalidContext() {
        DriverMap driverMap = new DriverMap();
        driverMap.getDetailsForLatestVersionOfDriverContext(binaryDataFor(GOOGLECHROME, OSX, ARCHITECTURE_64_BIT));
    }

    @Test(expected = NoSuchElementException.class)
    public void willThrowAnExceptionWhenTryingToGetAnInvalidLatestVersion() {
        DriverMap driverMap = new DriverMap();
        driverMap.getMapForDriverContext(binaryDataFor(GOOGLECHROME, OSX, ARCHITECTURE_64_BIT));

        driverMap.getDetailsForLatestVersionOfDriverContext(binaryDataFor(GOOGLECHROME, OSX, ARCHITECTURE_64_BIT));
    }

    @Test
    public void correctlyReturnsLatestVersion() throws MalformedURLException {
        DriverMap driverMap = new DriverMap();
        DriverDetails oldestVersion = new DriverDetails();
        oldestVersion.hash = "oldest";
        oldestVersion.hashType = HashType.SHA1;
        oldestVersion.fileLocation = new URL("http://www.example.com/foo/bar");
        DriverDetails latestVersion = new DriverDetails();
        latestVersion.hash = "latest";
        latestVersion.hashType = HashType.MD5;
        latestVersion.fileLocation = new URL("http://www.example.com/bar/foo");
        Map originalVersionMap = driverMap.getMapForDriverContext(binaryDataFor(GOOGLECHROME, OSX, ARCHITECTURE_64_BIT));
        originalVersionMap.put("2.13", oldestVersion);
        originalVersionMap.put("4", latestVersion);

        DriverDetails returnedDetails = driverMap.getDetailsForLatestVersionOfDriverContext(binaryDataFor(GOOGLECHROME, OSX, ARCHITECTURE_64_BIT));

        assertThat(returnedDetails.hash,
                is(equalTo("latest")));
        assertThat(returnedDetails.hashType,
                is(equalTo(HashType.MD5)));
        assertThat(returnedDetails.fileLocation,
                is(equalTo(new URL("http://www.example.com/bar/foo"))));
    }
}

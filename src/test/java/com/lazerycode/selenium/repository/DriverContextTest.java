package com.lazerycode.selenium.repository;

import org.junit.Test;

import static com.lazerycode.selenium.repository.BinaryType.GOOGLECHROME;
import static com.lazerycode.selenium.repository.OperatingSystem.OSX;
import static com.lazerycode.selenium.repository.SystemArchitecture.ARCHITECTURE_64_BIT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class DriverContextTest {

    @Test
    public void driverContextWithStringsMapsTpDriverContextWithEnums() {
        assertThat(DriverContext.binaryDataFor(GOOGLECHROME, OSX, ARCHITECTURE_64_BIT),
                is(equalTo(DriverContext.binaryDataFor("googlechrome", "osx", ARCHITECTURE_64_BIT))));
    }
}

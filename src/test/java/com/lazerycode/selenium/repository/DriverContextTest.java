package com.lazerycode.selenium.repository;

import org.junit.Test;

import java.io.File;

import static com.lazerycode.selenium.repository.BinaryType.GOOGLECHROME;
import static com.lazerycode.selenium.repository.DriverContext.binaryDataFor;
import static com.lazerycode.selenium.repository.OperatingSystem.OSX;
import static com.lazerycode.selenium.repository.SystemArchitecture.ARCHITECTURE_64_BIT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class DriverContextTest {

    @Test
    public void driverContextWithStringsMapsTpDriverContextWithEnums() {
        assertThat(binaryDataFor(OSX, GOOGLECHROME, ARCHITECTURE_64_BIT),
                is(equalTo(binaryDataFor("osx", "googlechrome", ARCHITECTURE_64_BIT))));
    }

    @Test
    public void driverContextCreatesAStringThatCanBeUsedAsAFilePath() {
        String expectedFilepath = "osx" + File.separator + "googlechrome" + File.separator + "64bit" + File.separator;
        String filePath = binaryDataFor(OSX, GOOGLECHROME, ARCHITECTURE_64_BIT).buildExtractionPathFromDriverContext();

        assertThat(filePath,
                is(equalTo(expectedFilepath)));
    }

    @Test
    public void returnsCorrectBinaryTypeForContext() {
        BinaryType binaryType = BinaryType.INTERNETEXPLORER;

        assertThat(binaryDataFor(OSX, binaryType, ARCHITECTURE_64_BIT).getBinaryTypeForContext(),
                is(equalTo(binaryType)));
    }
}

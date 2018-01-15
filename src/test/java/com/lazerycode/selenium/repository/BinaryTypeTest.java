package com.lazerycode.selenium.repository;

import org.junit.Test;

import java.util.List;

import static com.lazerycode.selenium.repository.BinaryType.GOOGLECHROME;
import static com.lazerycode.selenium.repository.BinaryType.PHANTOMJS;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class BinaryTypeTest {

    @Test
    public void willReturnAListOfFilenameAssociatedWithBinary() {
        List<String> binaryFilenames = PHANTOMJS.getBinaryFilenames();

        assertThat(binaryFilenames.size(),
                is(equalTo(2)));
        assertThat(binaryFilenames.get(0),
                is(equalTo("phantomjs.exe")));
        assertThat(binaryFilenames.get(1),
                is(equalTo("phantomjs")));
    }

    @Test
    public void willReturnALowerCaseStringValueOfBinaryType() {
        assertThat(GOOGLECHROME.getBinaryTypeAsString(),
                is(equalTo("googlechrome")));
    }

    @Test
    public void willReturnCorrectSystemPropertyForABinary() {
        assertThat(GOOGLECHROME.getDriverSystemProperty(),
                is(equalTo("webdriver.chrome.driver")));
    }
}

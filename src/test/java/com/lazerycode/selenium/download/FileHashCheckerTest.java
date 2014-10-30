package com.lazerycode.selenium.download;

import org.junit.Test;

import java.io.File;
import java.net.URL;

import static com.lazerycode.selenium.hash.HashType.MD5;
import static com.lazerycode.selenium.hash.HashType.SHA1;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class FileHashCheckerTest {

    private static final String validSHA1Hash = "8604c05969a0eefa0edf0d71ae809310832afdc7";
    private static final String validMD5Hash = "20d654798f9694099cc40254c5e84a01";

    @Test
    public void checkThatFileIsValidCheckReturnsTrueForAValidFile() throws Exception {

        URL url = this.getClass().getClassLoader().getResource("jetty/files/download.zip");
        File fileToTest = new File(url.getPath());

        FileHashChecker fileHashChecker = new FileHashChecker(fileToTest);
        fileHashChecker.setExpectedHash(validSHA1Hash, SHA1);

        assertThat(fileHashChecker.fileIsValid(), is(equalTo(true)));

        fileHashChecker.setExpectedHash(validMD5Hash, MD5);

        assertThat(fileHashChecker.fileIsValid(), is(equalTo(true)));
    }

    @Test
    public void checkThatFileIsValidCheckReturnsFalseForAnInvalidFile() throws Exception {
        File fileToTest = File.createTempFile("invalid", ".zip");
        fileToTest.deleteOnExit();
        assertThat(fileToTest.exists(), is(equalTo(true)));

        FileHashChecker fileHashChecker = new FileHashChecker(fileToTest);

        fileHashChecker.setExpectedHash(validSHA1Hash, SHA1);

        assertThat(fileToTest.delete(), is(equalTo(true)));
        assertThat(fileHashChecker.fileIsValid(), is(equalTo(false)));

        fileHashChecker.setExpectedHash(validMD5Hash, MD5);

        assertThat(fileHashChecker.fileIsValid(), is(equalTo(false)));
    }

    @Test
    public void checkThatFileIsValidCheckReturnsFalseForAFileThatDoesNotExist() throws Exception {
        File fileToTest = File.createTempFile("to_be_deleted", ".zip");
        assertThat(fileToTest.delete(), is(equalTo(true)));
        assertThat(fileToTest.exists(), is(equalTo(false)));

        FileHashChecker fileHashChecker = new FileHashChecker(fileToTest);

        fileHashChecker.setExpectedHash(validSHA1Hash, SHA1);

        assertThat(fileHashChecker.fileIsValid(), is(equalTo(false)));

        fileHashChecker.setExpectedHash(validMD5Hash, MD5);

        assertThat(fileHashChecker.fileIsValid(), is(equalTo(false)));
    }
}

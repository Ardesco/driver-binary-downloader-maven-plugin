package com.lazerycode.selenium.download;

import org.junit.Test;

import java.io.File;
import java.net.URL;

import static com.lazerycode.selenium.hash.HashType.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class FileHashCheckerTest {

    private static final String validSHA512Hash = "c540ca812008cca64174d2358011bf2b0eacddbb321e70253956da5ec49804168506c16c7e3b29fa3757704103fe6e150f79195abed07d78917e4d2a120854c0";
    private static final String validSHA256Hash = "e5004369295c63e1274de8e30039415407e6124528ad5e3b10e91148fd8e57fb";
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
        
        fileHashChecker.setExpectedHash(validSHA256Hash, SHA256);

        assertThat(fileHashChecker.fileIsValid(), is(equalTo(true)));

        fileHashChecker.setExpectedHash(validSHA512Hash, SHA512);

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
        
        fileHashChecker.setExpectedHash(validSHA256Hash, SHA256);

        assertThat(fileHashChecker.fileIsValid(), is(equalTo(false)));

        fileHashChecker.setExpectedHash(validSHA512Hash, SHA512);

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
        
        fileHashChecker.setExpectedHash(validSHA256Hash, SHA256);

        assertThat(fileHashChecker.fileIsValid(), is(equalTo(false)));
        
        fileHashChecker.setExpectedHash(validSHA512Hash, SHA512);

        assertThat(fileHashChecker.fileIsValid(), is(equalTo(false)));
    }
}

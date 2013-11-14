package com.lazerycode.selenium.repository;

import com.lazerycode.selenium.download.HashType;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class FileDetailsTest {

    private static String validFileLocation = "http://localhost:8080/files/download.zip";
    private static String validSHA1Hash = "8604c05969a0eefa0edf0d71ae809310832afdc7";
    private static String validMD5Hash = "20d654798f9694099cc40254c5e84a01";

    @Test
    public void isValidMD5Hash() throws Exception {
        FileDetails fileDetails = new FileDetails(validFileLocation, "md5", validMD5Hash);

        assertThat(fileDetails.getFileLocation(), is(equalTo(new URL(validFileLocation))));
        assertThat(fileDetails.getHash(), is(equalTo(validMD5Hash)));
        assertThat(fileDetails.getHashType(), is(equalTo(HashType.MD5)));
    }

    @Test
    public void isValidSHA1Hash() throws Exception {
        FileDetails fileDetails = new FileDetails(validFileLocation, "sha1", validSHA1Hash);

        assertThat(fileDetails.getFileLocation(), is(equalTo(new URL(validFileLocation))));
        assertThat(fileDetails.getHash(), is(equalTo(validSHA1Hash)));
        assertThat(fileDetails.getHashType(), is(equalTo(HashType.SHA1)));
    }

    @Test(expected = MalformedURLException.class)
    public void invalidURLNotAccepted() throws Exception {
        new FileDetails("foo", "md5", validMD5Hash);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidHashTypeNotAccepted() throws Exception {
        new FileDetails(validFileLocation, "foo", validMD5Hash);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidMD5HashNotAccepted() throws Exception {
        new FileDetails(validFileLocation, "md5", "foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidSHA1HashNotAccepted() throws Exception {
        new FileDetails(validFileLocation, "sha1", "foo");
    }
}

package com.lazerycode.selenium.download;

import com.lazerycode.selenium.hash.CheckFileHash;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static com.lazerycode.selenium.hash.HashType.MD5;
import static com.lazerycode.selenium.hash.HashType.SHA1;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class CheckFileHashTest {

    private final URL testFile = this.getClass().getResource("/jetty/files/download.zip");

    @Test
    public void checkValidMD5Hash() throws Exception {
        CheckFileHash fileToCheck = new CheckFileHash();
        fileToCheck.fileToCheck(new File(testFile.toURI()));
        fileToCheck.hashDetails("20d654798f9694099cc40254c5e84a01", MD5);
        assertThat(fileToCheck.hasAValidHash(), is(equalTo(true)));
    }

    @Test
    public void checkValidSHA1Hash() throws Exception {
        CheckFileHash fileToCheck = new CheckFileHash();
        fileToCheck.fileToCheck(new File(testFile.toURI()));
        fileToCheck.hashDetails("8604c05969a0eefa0edf0d71ae809310832afdc7", SHA1);
        assertThat(fileToCheck.hasAValidHash(), is(equalTo(true)));
    }

    @Test
    public void checkInvalidMD5Hash() throws Exception {
        CheckFileHash fileToCheck = new CheckFileHash();
        fileToCheck.fileToCheck(new File(testFile.toURI()));
        fileToCheck.hashDetails("foo", MD5);
        assertThat(fileToCheck.hasAValidHash(), is(equalTo(false)));
    }

    @Test
    public void checkInvalidSHA1Hash() throws Exception {
        CheckFileHash fileToCheck = new CheckFileHash();
        fileToCheck.fileToCheck(new File(testFile.toURI()));
        fileToCheck.hashDetails("bar", SHA1);
        assertThat(fileToCheck.hasAValidHash(), is(equalTo(false)));
    }

    @Test(expected = MojoExecutionException.class)
    public void fileNotSet() throws Exception {
        CheckFileHash fileToCheck = new CheckFileHash();
        fileToCheck.hasAValidHash();
    }

    @Test(expected = MojoExecutionException.class)
    public void hashDetailsNotSet() throws Exception {
        CheckFileHash fileToCheck = new CheckFileHash();
        fileToCheck.fileToCheck(new File(testFile.toURI()));
        fileToCheck.hasAValidHash();
    }
}


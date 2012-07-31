package com.lazerycode.selenium;

import com.lazerycode.selenium.download.CheckFileHash;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static com.lazerycode.selenium.download.HashType.MD5;
import static com.lazerycode.selenium.download.HashType.SHA1;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class CheckFileHashTest {

    private final URL testFile = this.getClass().getResource("/jetty/download.zip");

    @Test
    public void checkValidMD5Hash() throws Exception {
        CheckFileHash fileToCheck = new CheckFileHash();
        fileToCheck.fileToCheck(new File(testFile.toURI()));
        fileToCheck.hashDetails("def3a66650822363f9e0ae6b9fbdbd6f", MD5);
        assertThat(fileToCheck.hasAValidHash(), is(equalTo(true)));
    }

    @Test
    public void checkValidSHA1Hash() throws Exception {
        CheckFileHash fileToCheck = new CheckFileHash();
        fileToCheck.fileToCheck(new File(testFile.toURI()));
        fileToCheck.hashDetails("638213e8a5290cd4d227d57459d92655e8fb1f17", SHA1);
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


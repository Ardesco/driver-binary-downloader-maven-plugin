package com.lazerycode.selenium;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class FileDownloaderTest {

    private static JettyServer localWebServer = new JettyServer();
    private static String webServerAddress = "http://localhost";
    private static int webServerPort = 8081;
    private static String downloadDirectory = System.getProperty("project.build.directory") + "/downloaded/";

    @BeforeClass
    public static void start() throws Exception {
        localWebServer.startJettyServer(webServerPort);
    }

    @AfterClass
    public static void stop() throws Exception {
        localWebServer.stopJettyServer();
    }

    @Test
    public void downloadAFile() throws Exception {
        FileDownloader downloadTestFile = new FileDownloader();
        downloadTestFile.remoteURL(new URL(webServerAddress + ":" + webServerPort + "/download.zip"));
        downloadTestFile.performSHA1HashCheck(false);
        downloadTestFile.localFilePath(downloadDirectory);
        downloadTestFile.downloadZipAndExtractFiles();

        assertThat(new File(downloadDirectory + "download.txt").exists(), is(equalTo(true)));
    }

    @Test
    public void hashCheck() throws Exception {
        FileDownloader downloadTestFile = new FileDownloader();
        downloadTestFile.remoteURL(new URL(webServerAddress + ":" + webServerPort + "/download.zip"));
        downloadTestFile.sha1Hash("638213e8a5290cd4d227d57459d92655e8fb1f17");
        downloadTestFile.localFilePath(downloadDirectory);
        downloadTestFile.downloadZipAndExtractFiles();

        assertThat(new File(downloadDirectory + "download.txt").exists(), is(equalTo(true)));
    }

    @Test(expected = MojoExecutionException.class)
    public void invalidHashCheck() throws Exception {
        FileDownloader downloadTestFile = new FileDownloader();
        downloadTestFile.remoteURL(new URL(webServerAddress + ":" + webServerPort + "/download.zip"));
        downloadTestFile.sha1Hash("invalidHash");
        downloadTestFile.localFilePath(downloadDirectory);
        downloadTestFile.downloadZipAndExtractFiles();
    }
}
package com.lazerycode.selenium.download;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class FileDownloaderTest {

    private static final Logger LOG = Logger.getLogger(FileDownloaderTest.class);
    private static JettyServer localWebServer = new JettyServer();
    private static String webServerAddress = "http://localhost";
    private static int webServerPort = 9081;
    private static String webServerURL = webServerAddress + ":" + webServerPort;
    private static String downloadDirectory = System.getProperty("project.build.directory") + "/downloaded/";

    @BeforeClass
    public static void start() throws Exception {
        localWebServer.startJettyServer(webServerPort);
    }

    @AfterClass
    public static void stop() throws Exception {
        localWebServer.stopJettyServer();
    }

    @After
    public void cleanUpFiles() throws IOException {
        LOG.info("Cleaning up test files...");
        FileUtils.deleteDirectory(new File(downloadDirectory));
        LOG.info("Test complete.");
    }

    @Test(expected = MojoExecutionException.class)
    public void downloadAFile() throws Exception {
        LOG.info("Running test: " + this.toString());
        FileDownloader downloadTestFile = new FileDownloader(new File(downloadDirectory), 1, 15000, 15000);
        downloadTestFile.remoteURL(new URL(webServerURL + "/files/download.zip"));
        downloadTestFile.downloadFile();
    }

    @Test(expected = MojoExecutionException.class)
    public void invalidHashCheck() throws Exception {
        LOG.info("Running test: " + this.toString());
        FileDownloader downloadTestFile = new FileDownloader(new File(downloadDirectory), 1, 15000, 15000);
        downloadTestFile.remoteURL(new URL(webServerURL + "/files/download.zip"));
        downloadTestFile.setHash("invalidHash", HashType.SHA1);
        downloadTestFile.downloadFile();
    }

    @Test
    public void hashCheck() throws Exception {
        LOG.info("Running test: " + this.toString());
        FileDownloader downloadTestFile = new FileDownloader(new File(downloadDirectory), 1, 15000, 15000);
        downloadTestFile.remoteURL(new URL(webServerURL + "/files/download.zip"));
        downloadTestFile.setHash("638213e8a5290cd4d227d57459d92655e8fb1f17", HashType.SHA1);
        downloadTestFile.downloadFile();

        assertThat(new File(downloadDirectory + "download.zip").exists(), is(equalTo(true)));
    }

    //TODO start with invalid file and check valid one is downloaded

    //TODO check that if valid file exists we don't download anything

}
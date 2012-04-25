package com.lazerycode.selenium;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.UUID;

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
        downloadTestFile.setRemoteURL(new URL(webServerAddress + ":" + webServerPort + "/download.zip"));

        downloadTestFile.setLocalFilePath(downloadDirectory);
        downloadTestFile.download();

        assertThat(new File(downloadDirectory + "download.txt").exists(), is(equalTo(true)));
    }
}
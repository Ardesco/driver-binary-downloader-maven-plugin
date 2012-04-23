package com.lazerycode.selenium;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URL;
import java.util.UUID;

public class FileDownloaderTest {

    private static JettyServer localWebServer = new JettyServer();
    private static String webServerAddress = "http://localhost";
    private static int webServerPort = 8081;

    private String randomFilename() {
        return "/tmp/" + UUID.randomUUID().toString().replaceAll("-", "") + ".txt";
    }

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
        downloadTestFile.setRemoteURL(new URL(webServerAddress + webServerPort + "/download.txt"));
        downloadTestFile.download();
    }
}
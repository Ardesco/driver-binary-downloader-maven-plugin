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

    private static JettyServer localWebServer = new JettyServer();
    private static String webServerAddress = "http://localhost";
    private static int webServerPort = 9081;
    private static String webServerURL = webServerAddress + ":" + webServerPort;
    private static String downloadDirectory = System.getProperty("java.io.tmpdir") + File.separator + "download_test_file_dir";

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
        File downloadDirectory = new File(FileDownloaderTest.downloadDirectory);
        if (downloadDirectory.exists()) FileUtils.deleteDirectory(downloadDirectory);
    }

    @Test(expected = MojoExecutionException.class)
    public void downloadAFile() throws Exception {
        FileDownloader downloadTestFile = new FileDownloader(new File(downloadDirectory), 1, 15000, 15000);
        downloadTestFile.remoteURL(new URL(webServerURL + "/files/download.zip"));
        downloadTestFile.downloadFile();
    }

    @Test(expected = MojoExecutionException.class)
    public void invalidHashCheck() throws Exception {
        FileDownloader downloadTestFile = new FileDownloader(new File(downloadDirectory), 1, 15000, 15000);
        downloadTestFile.remoteURL(new URL(webServerURL + "/files/download.zip"));
        downloadTestFile.setHash("invalidHash", HashType.SHA1);
        downloadTestFile.downloadFile();
    }

    @Test
    public void hashCheck() throws Exception {
        FileDownloader downloadTestFile = new FileDownloader(new File(downloadDirectory), 1, 15000, 15000);
        downloadTestFile.remoteURL(new URL(webServerURL + "/files/download.zip"));
        downloadTestFile.setHash("8604c05969a0eefa0edf0d71ae809310832afdc7", HashType.SHA1);
        downloadTestFile.downloadFile();

        assertThat(new File(downloadDirectory + File.separator + "download.zip").exists(), is(equalTo(true)));
    }

    @Test
    public void invalidNumberOfRetriesResultsInOneRetry() throws Exception {
        FileDownloader downloadTestFile = new FileDownloader(new File(downloadDirectory), -10, 15000, 15000);

        assertThat(downloadTestFile.totalNumberOfRetryAttempts , is(equalTo(1)));
    }

    @Test(expected = MojoExecutionException.class)
    public void tryToDownloadAnInvalidFile() throws Exception {
        FileDownloader downloadTestFile = new FileDownloader(new File(downloadDirectory), 3, 15000, 15000);
        downloadTestFile.remoteURL(new URL(webServerURL + "/files/null/download.zip"));
        downloadTestFile.downloadFile();
    }

    @Test
    public void checkThatFileIsValidCheckReturnsFalseIfFileIsNotThere() throws Exception {
        FileDownloader downloadTestFile = new FileDownloader(new File(downloadDirectory), 1, 15000, 15000);
        downloadTestFile.remoteURL(new URL(webServerURL + "/files/download.zip"));
        downloadTestFile.setHash("8604c05969a0eefa0edf0d71ae809310832afdc7", HashType.SHA1);
        downloadTestFile.downloadFile();

        File downloadedFile = new File(downloadDirectory + File.separator + "download.zip");

        assertThat(downloadedFile.exists(), is(equalTo(true)));
        assertThat(downloadTestFile.fileExistsAndIsValid(downloadedFile), is(equalTo(true)));

        downloadedFile.delete();

        assertThat(downloadedFile.exists(), is(equalTo(false)));
        assertThat(downloadTestFile.fileExistsAndIsValid(downloadedFile), is(equalTo(false)));
    }

    //TODO start with invalid file and check valid one is downloaded

    //TODO check that if valid file exists we don't download anything

}
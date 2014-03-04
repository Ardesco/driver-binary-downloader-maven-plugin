package com.lazerycode.selenium.download;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static com.lazerycode.selenium.hash.HashType.SHA1;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class DownloadHandlerTest {

    private static final String webServerAddress = "http://localhost";
    private static final int webServerPort = 9081;
    private final String downloadDirectoryPath = System.getProperty("java.io.tmpdir") + File.separator + "download_test_file_dir";
    private final String expectedDownloadedFilePath = downloadDirectoryPath + File.separator + "download.zip";
    private final String validHash = "8604c05969a0eefa0edf0d71ae809310832afdc7";
    private final int retryAttempts = 1;
    private final int connectTimeout = 15000;
    private final int readTimeout = 15000;
    private final boolean overwriteFilesThatExist = false;

    private static JettyServer localWebServer = new JettyServer();
    private static String webServerURL = webServerAddress + ":" + webServerPort;
    private static File downloadDirectory;
    private static URL downloadZipURL;

    @BeforeClass
    public static void start() throws Exception {
        localWebServer.startJettyServer(webServerPort);
        downloadZipURL = new URL(webServerURL + "/files/download.zip");
    }

    @AfterClass
    public static void stop() throws Exception {
        localWebServer.stopJettyServer();
    }

    @Before
    public void instantiateTestFiles() throws Exception {
        downloadDirectory = new File(downloadDirectoryPath);
    }

    @After
    public void cleanUpFiles() throws IOException {
        if (downloadDirectory.exists()) FileUtils.deleteDirectory(downloadDirectory);
    }

    @Test(expected = MojoExecutionException.class)
    public void downloadAFile() throws Exception {
        DownloadHandler downloadTestFile = new DownloadHandler(null, new File(downloadDirectoryPath), retryAttempts, connectTimeout, readTimeout, null, overwriteFilesThatExist);
        downloadTestFile.downloadFile(downloadZipURL, null, null);
    }

    @Test(expected = MojoExecutionException.class)
    public void invalidHashCheck() throws Exception {
        DownloadHandler downloadTestFile = new DownloadHandler(null, new File(downloadDirectoryPath), retryAttempts, connectTimeout, readTimeout, null, overwriteFilesThatExist);
        downloadTestFile.downloadFile(downloadZipURL, "invalidHash", SHA1);
    }

    @Test
    public void hashCheck() throws Exception {
        DownloadHandler downloadTestFile = new DownloadHandler(null, new File(downloadDirectoryPath), retryAttempts, connectTimeout, readTimeout, null, overwriteFilesThatExist);
        downloadTestFile.downloadFile(downloadZipURL, validHash, SHA1);

        assertThat(new File(expectedDownloadedFilePath).exists(), is(equalTo(true)));
    }

    @Test
    public void invalidNumberOfRetriesResultsInOneRetry() throws Exception {
        DownloadHandler downloadTestFile = new DownloadHandler(null, new File(downloadDirectoryPath), -10, connectTimeout, readTimeout, null, overwriteFilesThatExist);

        assertThat(downloadTestFile.fileDownloadRetryAttempts, is(equalTo(1)));
    }

    @Test(expected = MojoExecutionException.class)
    public void tryToDownloadAnInvalidFile() throws Exception {
        DownloadHandler downloadTestFile = new DownloadHandler(null, new File(downloadDirectoryPath), 3, connectTimeout, readTimeout, null, overwriteFilesThatExist);
        downloadTestFile.downloadFile(new URL(webServerURL + "/files/null/download.zip"), validHash, SHA1);
    }

    @Test(expected = MojoFailureException.class)
    public void specifyAFileInsteadOfADirectory() throws Exception {
        DownloadHandler downloadTestFile = new DownloadHandler(null, File.createTempFile("foo", "bar"), 3, connectTimeout, readTimeout, null, overwriteFilesThatExist);
        downloadTestFile.downloadFile(new URL(webServerURL + "/files/null/download.zip"), validHash, SHA1);
    }

    @Test
    public void checkThatFileIsValidCheckReturnsFalseIfFileIsNotThere() throws Exception {
        DownloadHandler downloadTestFile = new DownloadHandler(null, new File(downloadDirectoryPath), retryAttempts, connectTimeout, readTimeout, null, overwriteFilesThatExist);
        downloadTestFile.downloadFile(downloadZipURL, validHash, SHA1);

        File downloadedFile = new File(expectedDownloadedFilePath);

        assertThat(downloadedFile.exists(), is(equalTo(true)));
        assertThat(downloadTestFile.fileExistsAndIsValid(downloadedFile, validHash, SHA1), is(equalTo(true)));

        assertThat(downloadedFile.delete(), is(equalTo(true)));

        assertThat(downloadedFile.exists(), is(equalTo(false)));
        assertThat(downloadTestFile.fileExistsAndIsValid(downloadedFile, validHash, SHA1), is(equalTo(false)));
    }

    //TODO start with invalid file and check valid one is downloaded

    //TODO check that if valid file exists we don't download anything

    //TODO check overwrite file that exists functionality +ve & -ve

    //TODO add tests for getStandaloneExecutableFiles()
}
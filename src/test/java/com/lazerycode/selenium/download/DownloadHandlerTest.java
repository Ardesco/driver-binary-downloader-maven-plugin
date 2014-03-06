package com.lazerycode.selenium.download;

import com.lazerycode.selenium.repository.FileDetails;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import static com.lazerycode.selenium.hash.HashType.MD5;
import static com.lazerycode.selenium.hash.HashType.SHA1;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class DownloadHandlerTest {

    private static final String webServerAddress = "http://localhost";
    private static final int webServerPort = 9081;
    private final String downloadDirectoryPath = System.getProperty("java.io.tmpdir") + File.separator + "download_test_file_dir";
    private final String rootStandaloneServerDirectoryPath = System.getProperty("java.io.tmpdir") + File.separator + "server_test_file_dir";
    private final String expectedDownloadedFilePath = downloadDirectoryPath + File.separator + "download.zip";
    private final int oneRetryAttempt = 1;
    private final int connectTimeout = 15000;
    private final int readTimeout = 15000;
    private final boolean doNotOverwriteFilesThatExist = false;
    private final boolean doNotCheckFileHashes = false;

    private static final String validSHA1Hash = "8604c05969a0eefa0edf0d71ae809310832afdc7";
    private static final String validMD5Hash = "20d654798f9694099cc40254c5e84a01";
    private static final JettyServer localWebServer = new JettyServer();
    private static final String webServerURL = webServerAddress + ":" + webServerPort;
    private static FileDetails validFileDetails;
    private static File downloadDirectory;
    private static File rootStandaloneServerDirectory;
    private static URL downloadZipURL;

    @BeforeClass
    public static void start() throws Exception {
        localWebServer.startJettyServer(webServerPort);
        downloadZipURL = new URL(webServerURL + "/files/download.zip");
        validFileDetails = new FileDetails(downloadZipURL, SHA1, validSHA1Hash);
    }

    @AfterClass
    public static void stop() throws Exception {
        localWebServer.stopJettyServer();
    }

    @Before
    public void instantiateTestFiles() throws Exception {
        downloadDirectory = new File(downloadDirectoryPath);
        rootStandaloneServerDirectory = new File(rootStandaloneServerDirectoryPath);
    }

    @After
    public void cleanUpFiles() throws IOException {
        if (downloadDirectory.exists()) FileUtils.deleteDirectory(downloadDirectory);
        if (rootStandaloneServerDirectory.exists()) FileUtils.deleteDirectory(rootStandaloneServerDirectory);
    }

    @Test(expected = MojoExecutionException.class)
    public void downloadAFile() throws Exception {
        DownloadHandler downloadTestFile = new DownloadHandler(null, downloadDirectory, oneRetryAttempt, connectTimeout, readTimeout, null, doNotOverwriteFilesThatExist, doNotCheckFileHashes);
        downloadTestFile.downloadFile(new FileDetails(downloadZipURL, SHA1, ""));
    }

    @Test(expected = MojoExecutionException.class)
    public void invalidHashCheck() throws Exception {
        DownloadHandler downloadTestFile = new DownloadHandler(null, downloadDirectory, oneRetryAttempt, connectTimeout, readTimeout, null, doNotOverwriteFilesThatExist, doNotCheckFileHashes);
        downloadTestFile.downloadFile(new FileDetails(downloadZipURL, SHA1, "invalidHash"));
    }

    @Test
    public void hashCheck() throws Exception {
        DownloadHandler downloadTestFile = new DownloadHandler(null, downloadDirectory, oneRetryAttempt, connectTimeout, readTimeout, null, doNotOverwriteFilesThatExist, doNotCheckFileHashes);
        downloadTestFile.downloadFile(validFileDetails);

        assertThat(new File(expectedDownloadedFilePath).exists(), is(equalTo(true)));
    }

    @Test
    public void invalidNumberOfRetriesResultsInOneRetry() throws Exception {
        DownloadHandler downloadTestFile = new DownloadHandler(null, downloadDirectory, -10, connectTimeout, readTimeout, null, doNotOverwriteFilesThatExist, doNotCheckFileHashes);

        assertThat(downloadTestFile.fileDownloadRetryAttempts, is(equalTo(1)));
    }

    @Test(expected = MojoExecutionException.class)
    public void tryToDownloadAnInvalidFile() throws Exception {
        DownloadHandler downloadTestFile = new DownloadHandler(null, downloadDirectory, 3, connectTimeout, readTimeout, null, doNotOverwriteFilesThatExist, doNotCheckFileHashes);
        downloadTestFile.downloadFile(new FileDetails(new URL(webServerURL + "/files/null/download.zip"), SHA1, validSHA1Hash));
    }

    @Test(expected = MojoFailureException.class)
    public void specifyAFileInsteadOfADirectory() throws Exception {
        DownloadHandler downloadTestFile = new DownloadHandler(null, File.createTempFile("foo", "bar"), 3, connectTimeout, readTimeout, null, doNotOverwriteFilesThatExist, doNotCheckFileHashes);
        downloadTestFile.downloadFile(new FileDetails(new URL(webServerURL + "/files/null/download.zip"), SHA1, validSHA1Hash));
    }

    @Test
    public void checkThatFileIsValidCheckReturnsFalseIfFileIsNotThere() throws Exception {
        DownloadHandler downloadTestFile = new DownloadHandler(null, downloadDirectory, oneRetryAttempt, connectTimeout, readTimeout, null, doNotOverwriteFilesThatExist, doNotCheckFileHashes);
        downloadTestFile.downloadFile(validFileDetails);

        File downloadedFile = new File(expectedDownloadedFilePath);

        assertThat(downloadedFile.exists(), is(equalTo(true)));
        assertThat(downloadTestFile.fileExistsAndIsValid(downloadedFile, validSHA1Hash, SHA1), is(equalTo(true)));
        assertThat(downloadTestFile.fileExistsAndIsValid(downloadedFile, validMD5Hash, MD5), is(equalTo(true)));

        assertThat(downloadedFile.delete(), is(equalTo(true)));

        assertThat(downloadedFile.exists(), is(equalTo(false)));
        assertThat(downloadTestFile.fileExistsAndIsValid(downloadedFile, validSHA1Hash, SHA1), is(equalTo(false)));
        assertThat(downloadTestFile.fileExistsAndIsValid(downloadedFile, validMD5Hash, MD5), is(equalTo(false)));
    }

    @Test
    public void checkThatFileIsValidCheckReturnsTrueIfHashInvalidButNotChecked() throws Exception {
        DownloadHandler downloadTestFile = new DownloadHandler(null, downloadDirectory, oneRetryAttempt, connectTimeout, readTimeout, null, doNotOverwriteFilesThatExist, true);
        downloadTestFile.downloadFile(validFileDetails);

        File downloadedFile = new File(expectedDownloadedFilePath);

        assertThat(downloadedFile.exists(), is(equalTo(true)));
        assertThat(downloadTestFile.fileExistsAndIsValid(downloadedFile, "Invalid_hash", SHA1), is(equalTo(true)));
        assertThat(downloadTestFile.fileExistsAndIsValid(downloadedFile, "Invalid_hash", MD5), is(equalTo(true)));

        assertThat(downloadedFile.delete(), is(equalTo(true)));

        assertThat(downloadedFile.exists(), is(equalTo(false)));
        assertThat(downloadTestFile.fileExistsAndIsValid(downloadedFile, "Invalid_hash", SHA1), is(equalTo(false)));
        assertThat(downloadTestFile.fileExistsAndIsValid(downloadedFile, "Invalid_hash", MD5), is(equalTo(false)));
    }

    @Test
    public void filesAreExtractedIntoTheCorrectStandaloneServerPathAndCanBeOverwritten() throws Exception {
        String downloadPath = "os/phantomjs/32bit/1";
        File expectedDownloadedFile = new File(rootStandaloneServerDirectoryPath + File.separator + downloadPath + File.separator + "phantomjs");
        FileDetails testFileDetails = new FileDetails(downloadZipURL, SHA1, validSHA1Hash);
        HashMap<String, FileDetails> fileDownloadList = new HashMap<String, FileDetails>();
        fileDownloadList.put(downloadPath, testFileDetails);

        assertThat(expectedDownloadedFile.exists(), is(equalTo(false)));

        DownloadHandler downloadTestFile = new DownloadHandler(new File(rootStandaloneServerDirectoryPath), new File(downloadDirectoryPath), oneRetryAttempt, connectTimeout, readTimeout, fileDownloadList, true, doNotCheckFileHashes);
        downloadTestFile.getStandaloneExecutableFiles();

        assertThat(expectedDownloadedFile.exists(), is(equalTo(true)));

        long lastModified = expectedDownloadedFile.lastModified();

        Thread.sleep(1000);  //Wait 1 second so that the file isn't copied and then overwritten in the same second
        downloadTestFile.getStandaloneExecutableFiles();

        assertThat(expectedDownloadedFile.lastModified(), is(not(equalTo(lastModified))));
    }
}
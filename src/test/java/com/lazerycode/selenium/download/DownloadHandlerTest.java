package com.lazerycode.selenium.download;

import com.lazerycode.selenium.repository.DriverContext;
import com.lazerycode.selenium.repository.DriverDetails;
import com.lazerycode.selenium.repository.DriverMap;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static com.lazerycode.selenium.hash.HashType.SHA1;
import static com.lazerycode.selenium.repository.BinaryType.GOOGLECHROME;
import static com.lazerycode.selenium.repository.BinaryType.PHANTOMJS;
import static com.lazerycode.selenium.repository.DriverContext.binaryDataFor;
import static com.lazerycode.selenium.repository.OperatingSystem.OSX;
import static com.lazerycode.selenium.repository.SystemArchitecture.ARCHITECTURE_64_BIT;
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
    private final boolean overwriteFilesThatExist = true;
    private final boolean doNotOverwriteFilesThatExist = false;
    private final boolean doNotCheckFileHashes = false;
    private final boolean checkFileHashes = true;
    private final boolean doNotUseSystemProxy = false;
    private final boolean getAllVersions = false;

    private static final String validSHA1Hash = "8604c05969a0eefa0edf0d71ae809310832afdc7";
    private static final JettyServer localWebServer = new JettyServer();
    private static final String webServerURL = webServerAddress + ":" + webServerPort;
    private static DriverMap validDriverMap = new DriverMap();
    private static DriverDetails validFileDetails = new DriverDetails();
    private static File downloadDirectory;
    private static File rootStandaloneServerDirectory;
    private static URL downloadZipURL;

    @BeforeClass
    public static void start() throws Exception {
        localWebServer.startJettyServer(webServerPort);
        downloadZipURL = new URL(webServerURL + "/files/download.zip");
        validFileDetails.fileLocation = downloadZipURL;
        validFileDetails.hashType = SHA1;
        validFileDetails.hash = validSHA1Hash;
        validDriverMap.getMapForDriverContext(binaryDataFor(OSX, GOOGLECHROME, ARCHITECTURE_64_BIT)).put("2.13", validFileDetails);
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
        DriverDetails fileDetails = new DriverDetails();
        fileDetails.fileLocation = downloadZipURL;
        fileDetails.hashType = SHA1;
        fileDetails.hash = "";
        DriverMap driverMap = new DriverMap();
        driverMap.getMapForDriverContext(binaryDataFor(OSX, GOOGLECHROME, ARCHITECTURE_64_BIT)).put("2.13", fileDetails);
        DownloadHandler downloadTestFile = new DownloadHandler(null, downloadDirectory, oneRetryAttempt, connectTimeout, readTimeout, null, doNotOverwriteFilesThatExist, checkFileHashes, doNotUseSystemProxy, getAllVersions);
        downloadTestFile.downloadFile(fileDetails, checkFileHashes);
    }

    @Test(expected = MojoExecutionException.class)
    public void invalidHashCheck() throws Exception {
        DriverDetails fileDetails = new DriverDetails();
        fileDetails.fileLocation = downloadZipURL;
        fileDetails.hashType = SHA1;
        fileDetails.hash = "invalidHash";
        DriverMap driverMap = new DriverMap();
        driverMap.getMapForDriverContext(binaryDataFor(OSX, GOOGLECHROME, ARCHITECTURE_64_BIT)).put("2.13", fileDetails);
        DownloadHandler downloadTestFile = new DownloadHandler(null, downloadDirectory, oneRetryAttempt, connectTimeout, readTimeout, null, doNotOverwriteFilesThatExist, checkFileHashes, doNotUseSystemProxy, getAllVersions);
        downloadTestFile.downloadFile(fileDetails, checkFileHashes);
    }

    @Test
    public void hashCheck() throws Exception {
        DownloadHandler downloadTestFile = new DownloadHandler(null, downloadDirectory, oneRetryAttempt, connectTimeout, readTimeout, null, doNotOverwriteFilesThatExist, checkFileHashes, doNotUseSystemProxy, getAllVersions);
        downloadTestFile.downloadFile(validFileDetails, checkFileHashes);

        assertThat(new File(expectedDownloadedFilePath).exists(), is(equalTo(true)));
    }

    @Test(expected = MojoExecutionException.class)
    public void tryToDownloadAnInvalidFile() throws Exception {
        DriverDetails fileDetails = new DriverDetails();
        fileDetails.fileLocation = new URL(webServerURL + "/files/null/download.zip");
        fileDetails.hashType = SHA1;
        fileDetails.hash = validSHA1Hash;
        DriverMap driverMap = new DriverMap();
        driverMap.getMapForDriverContext(binaryDataFor(OSX, GOOGLECHROME, ARCHITECTURE_64_BIT)).put("2.13", fileDetails);
        DownloadHandler downloadTestFile = new DownloadHandler(null, downloadDirectory, 3, connectTimeout, readTimeout, null, doNotOverwriteFilesThatExist, checkFileHashes, doNotUseSystemProxy, getAllVersions);
        downloadTestFile.downloadFile(fileDetails, checkFileHashes);
    }

    @Test(expected = MojoFailureException.class)
    public void specifyAFileInsteadOfADirectory() throws Exception {
        DownloadHandler downloadTestFile = new DownloadHandler(null, File.createTempFile("foo", "bar"), 3, connectTimeout, readTimeout, null, doNotOverwriteFilesThatExist, checkFileHashes, doNotUseSystemProxy, getAllVersions);
        downloadTestFile.downloadFile(validFileDetails, checkFileHashes);
    }

    @Test
    public void filesAreExtractedIntoTheCorrectStandaloneServerPathAndCanBeOverwritten() throws Exception {
        DriverDetails fileDetails = new DriverDetails();
        fileDetails.fileLocation = downloadZipURL;
        fileDetails.hashType = SHA1;
        fileDetails.hash = validSHA1Hash;
        DriverContext driverContext = binaryDataFor(OSX, PHANTOMJS, ARCHITECTURE_64_BIT);
        DriverMap driverMap = new DriverMap();
        driverMap.getMapForDriverContext(driverContext).put("2.13", fileDetails);
        File expectedDownloadedFile = new File(rootStandaloneServerDirectoryPath + File.separator + "osx" + File.separator + "phantomjs" + File.separator + "64bit" + File.separator + "phantomjs");

        assertThat(expectedDownloadedFile.exists(), is(equalTo(false)));

        DownloadHandler downloadTestFile = new DownloadHandler(new File(rootStandaloneServerDirectoryPath), new File(downloadDirectoryPath), oneRetryAttempt, connectTimeout, readTimeout, driverMap, overwriteFilesThatExist, checkFileHashes, doNotUseSystemProxy, getAllVersions);
        downloadTestFile.ensureStandaloneExecutableFilesExist();

        assertThat(expectedDownloadedFile.exists(), is(equalTo(true)));

        long lastModified = expectedDownloadedFile.lastModified();

        Thread.sleep(1000);  //Wait 1 second so that the file isn't copied and then overwritten in the same second
        downloadTestFile.ensureStandaloneExecutableFilesExist();

        assertThat(expectedDownloadedFile.lastModified(), is(not(equalTo(lastModified))));
        assertThat(driverMap.getDetailsForLatestVersionOfDriverContext(driverContext).extractedLocation,
                is(equalTo(expectedDownloadedFile.getAbsolutePath())));
    }

    @Test
    public void fileCanBeDownloadedIfThereIsNoHashInFileDownloadListFileDetails() throws Exception {
        DriverDetails fileDetails = new DriverDetails();
        fileDetails.fileLocation = downloadZipURL;
        fileDetails.hashType = null;
        fileDetails.hash = null;
        DriverMap driverMap = new DriverMap();
        driverMap.getMapForDriverContext(binaryDataFor(OSX, PHANTOMJS, ARCHITECTURE_64_BIT)).put("2.13", fileDetails);
        File expectedDownloadedFile = new File(rootStandaloneServerDirectoryPath + File.separator + "osx" + File.separator + "phantomjs" + File.separator + "64bit" + File.separator + "phantomjs");

        assertThat(expectedDownloadedFile.exists(), is(equalTo(false)));

        DownloadHandler downloadTestFile = new DownloadHandler(new File(rootStandaloneServerDirectoryPath), new File(downloadDirectoryPath), oneRetryAttempt, connectTimeout, readTimeout, driverMap, overwriteFilesThatExist, doNotCheckFileHashes, doNotUseSystemProxy, getAllVersions);
        downloadTestFile.ensureStandaloneExecutableFilesExist();

        assertThat(expectedDownloadedFile.exists(), is(equalTo(true)));
    }

    @Ignore
    @Test(expected = MojoExecutionException.class)
    public void errorThrownIfThereIsNoHashInFileDownloadListFileDetailsAndHashShouldBeChecked() throws Exception {
        DriverDetails fileDetails = new DriverDetails();
        fileDetails.fileLocation = downloadZipURL;
        fileDetails.hashType = SHA1;
        fileDetails.hash = null;
        DriverMap driverMap = new DriverMap();
        driverMap.getMapForDriverContext(binaryDataFor(OSX, GOOGLECHROME, ARCHITECTURE_64_BIT)).put("2.13", fileDetails);
        DownloadHandler downloadTestFile = new DownloadHandler(new File(rootStandaloneServerDirectoryPath), new File(downloadDirectoryPath), oneRetryAttempt, connectTimeout, readTimeout, driverMap, overwriteFilesThatExist, checkFileHashes, doNotUseSystemProxy, getAllVersions);
        downloadTestFile.ensureStandaloneExecutableFilesExist();
    }

    @Test(expected = NullPointerException.class)
    public void errorThrownIfThereIsNoHashTypeInFileDownloadListFileDetailsAndHashShouldBeChecked() throws Exception {
        DriverDetails fileDetails = new DriverDetails();
        fileDetails.fileLocation = downloadZipURL;
        fileDetails.hashType = null;
        fileDetails.hash = validSHA1Hash;
        DriverMap driverMap = new DriverMap();
        driverMap.getMapForDriverContext(binaryDataFor(OSX, GOOGLECHROME, ARCHITECTURE_64_BIT)).put("2.13", fileDetails);
        DownloadHandler downloadTestFile = new DownloadHandler(new File(rootStandaloneServerDirectoryPath), new File(downloadDirectoryPath), oneRetryAttempt, connectTimeout, readTimeout, driverMap, overwriteFilesThatExist, checkFileHashes, doNotUseSystemProxy, getAllVersions);
        downloadTestFile.ensureStandaloneExecutableFilesExist();
    }
}
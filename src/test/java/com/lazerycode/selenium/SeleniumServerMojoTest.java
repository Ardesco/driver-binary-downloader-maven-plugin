package com.lazerycode.selenium;

import com.lazerycode.selenium.download.DownloadHandler;
import com.lazerycode.selenium.download.JettyServer;
import com.lazerycode.selenium.repository.DriverMap;
import com.lazerycode.selenium.repository.OperatingSystem;
import com.lazerycode.selenium.repository.SystemArchitecture;
import com.lazerycode.selenium.repository.XMLParser;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;

import static com.lazerycode.selenium.repository.FileRepository.buildDownloadableFileRepository;
import static com.lazerycode.selenium.repository.OperatingSystem.getCurrentOperatingSystem;
import static com.lazerycode.selenium.repository.SystemArchitecture.getCurrentSystemArcitecture;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class SeleniumServerMojoTest {

    private static final int webServerPort = 9081;

    private static final JettyServer localWebServer = new JettyServer();

    private static String downloadDirectoryPath;
    private static String rootStandaloneServerDirectoryPath;
    private static boolean getThirtyTwoBitBinaries = false;
    private static boolean getSixtyFourBitBinaries = false;
    private static boolean getArmBinaries = false;
    private final int oneRetryAttempt = 1;
    private final int connectTimeout = 15000;
    private final int readTimeout = 15000;
    private final boolean overwriteFilesThatExist = true;
    private final boolean checkFileHashes = true;
    private final boolean doNotUseSystemProxy = false;
    private final boolean getAllVersions = true;


    @BeforeClass
    public static void start() throws Exception {
        localWebServer.startJettyServer(webServerPort);
        String tempDirectory = System.getProperty("java.io.tmpdir");
        if (tempDirectory.endsWith("/")) {
            downloadDirectoryPath = System.getProperty("java.io.tmpdir") + "download_test_file_dir";
            rootStandaloneServerDirectoryPath = System.getProperty("java.io.tmpdir") + "server_test_file_dir";
        } else {
            downloadDirectoryPath = System.getProperty("java.io.tmpdir") + File.separator + "download_test_file_dir";
            rootStandaloneServerDirectoryPath = System.getProperty("java.io.tmpdir") + File.separator + "server_test_file_dir";
        }
        if (getCurrentSystemArcitecture().equals(SystemArchitecture.ARCHITECTURE_64_BIT)) {
            getSixtyFourBitBinaries = true;
        } else {
            getThirtyTwoBitBinaries = true;
        }
    }

    @AfterClass
    public static void stop() throws Exception {
        localWebServer.stopJettyServer();
    }

    //TODO write some proper mojo tests
    @Ignore
    @Test
    public void systemPropertiesAreSet() throws Exception {
        SeleniumServerMojo mojo = new SeleniumServerMojo();
        String currentOperatingSystem = getCurrentOperatingSystem().toString().toLowerCase();
        String currentArchitecture = getCurrentSystemArcitecture().getSystemArchitectureType();
        String relativeExecutablePath = File.separator + currentOperatingSystem + File.separator + "phantomjs" + File.separator + currentArchitecture + File.separator + "phantomjs";
        InputStream xmlRepositoryMap = this.getClass().getResourceAsStream("/TestRepoMap2.xml");
        XMLParser parser = new XMLParser(xmlRepositoryMap, OperatingSystem.getCurrentOperatingSystemAsAHashSet(), null, getThirtyTwoBitBinaries, getSixtyFourBitBinaries);
        DriverMap driverMap = buildDownloadableFileRepository(parser.getAllNodesInScope(), getThirtyTwoBitBinaries, getSixtyFourBitBinaries, getArmBinaries);
        DownloadHandler standaloneExecutableDownloader = new DownloadHandler(
                new File(rootStandaloneServerDirectoryPath),
                new File(downloadDirectoryPath),
                oneRetryAttempt,
                connectTimeout,
                readTimeout,
                driverMap,
                overwriteFilesThatExist,
                checkFileHashes,
                doNotUseSystemProxy,
                getAllVersions);
        DriverMap driverRepository = standaloneExecutableDownloader.ensureStandaloneExecutableFilesExist();
        mojo.setSystemProperties(driverRepository);

        assertThat(System.getProperty("phantomjs.binary.path"),
                is(equalTo(rootStandaloneServerDirectoryPath + relativeExecutablePath)));
    }
}

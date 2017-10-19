package com.lazerycode.selenium.download;

import com.lazerycode.selenium.extract.FileExtractor;
import com.lazerycode.selenium.hash.HashType;
import com.lazerycode.selenium.repository.DriverContext;
import com.lazerycode.selenium.repository.DriverDetails;
import com.lazerycode.selenium.repository.DriverMap;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class DownloadHandler {

    private static final Logger LOG = Logger.getLogger(DownloadHandler.class);
    private final DriverMap filesToDownload;
    private final File rootStandaloneServerDirectory;
    private final File downloadedZipFileDirectory;
    protected final int fileDownloadRetryAttempts;
    private boolean overwriteFilesThatExist = false;
    private boolean checkFileHash = true;
    private boolean onlyGetLatestVersions = true;
    private FileDownloader fileDownloader;

    public DownloadHandler(File rootStandaloneServerDirectory, File downloadedZipFileDirectory, int fileDownloadRetryAttempts, int fileDownloadConnectTimeout, int fileDownloadReadTimeout, DriverMap filesToDownload, boolean overwriteFilesThatExist, boolean checkFileHash, boolean useSystemProxy, boolean onlyGetLatestVersions) throws MojoFailureException {
        this.rootStandaloneServerDirectory = rootStandaloneServerDirectory;
        this.downloadedZipFileDirectory = downloadedZipFileDirectory;
        this.filesToDownload = filesToDownload;
        this.fileDownloadRetryAttempts = fileDownloadRetryAttempts;
        this.overwriteFilesThatExist = overwriteFilesThatExist;
        this.checkFileHash = checkFileHash;
        this.onlyGetLatestVersions = onlyGetLatestVersions;

        fileDownloader = new FileDownloader(downloadedZipFileDirectory, useSystemProxy);
        fileDownloader.setReadTimeout(fileDownloadReadTimeout);
        fileDownloader.setConnectTimeout(fileDownloadConnectTimeout);
    }

    private boolean checkFileHash(File fileToCheck, String hash, HashType hashType) throws IOException, MojoExecutionException {
        FileHashChecker fileHashChecker = new FileHashChecker(fileToCheck);
        fileHashChecker.setExpectedHash(hash, hashType);

        return fileHashChecker.fileIsValid();
    }

    /**
     * Perform the file download
     *
     * @param driverDetails         Driver details extracted from Repositorymap.xml
     * @param shouldWeCheckFileHash true if file hash should be checked
     * @return File
     * @throws MojoExecutionException Unable to download file
     * @throws IOException            Error writing to file system
     * @throws URISyntaxException     Invalid URI
     */
    protected File downloadFile(DriverDetails driverDetails, boolean shouldWeCheckFileHash) throws MojoExecutionException, IOException, URISyntaxException {

        URL remoteFileLocation = driverDetails.fileLocation;

        final String filename = FilenameUtils.getName(remoteFileLocation.getFile());
        for (int retryAttempts = 1; retryAttempts <= this.fileDownloadRetryAttempts; retryAttempts++) {
            File downloadedFile = fileDownloader.attemptToDownload(remoteFileLocation);
            if (null != downloadedFile) {
                if (!shouldWeCheckFileHash || checkFileHash(downloadedFile, driverDetails.hash, driverDetails.hashType)) {
                    LOG.info("Archive file '" + downloadedFile.getName() + "' is valid : true");
                    return downloadedFile;
                } else {
                    LOG.info("Archive file '" + downloadedFile.getName() + "' is valid : false");
                }
            }
            LOG.info("Problem downloading '" + filename + "'... ");
            if (retryAttempts < this.fileDownloadRetryAttempts) {
                LOG.info("Retry attempt " + (retryAttempts) + " for '" + filename + "'");
            }
        }

        throw new MojoExecutionException("Unable to successfully download '" + filename + "'!");
    }

    public DriverMap ensureStandaloneExecutableFilesExist() throws MojoFailureException, MojoExecutionException, IOException, URISyntaxException {
        LOG.info("Archives will be downloaded to '" + this.downloadedZipFileDirectory.getAbsolutePath() + "'");
        LOG.info("Standalone executable files will be extracted to '" + this.rootStandaloneServerDirectory + "'");
        LOG.info(" ");
        LOG.info("Preparing to download Selenium Standalone Executable Binaries...");

        for (final DriverContext driverContext : filesToDownload.getKeys()) {
            if (onlyGetLatestVersions) {
                DriverDetails driverDetails = filesToDownload.getDetailsForLatestVersionOfDriverContext(driverContext);
                downloadAndExtractExecutableFiles(driverContext, driverDetails);
            } else {
                for (String version : filesToDownload.getAvailableVersionsForDriverContext(driverContext)) {
                    DriverDetails driverDetails = filesToDownload.getDetailsForVersionOfDriverContext(driverContext, version);
                    downloadAndExtractExecutableFiles(driverContext, driverDetails);
                }
            }
        }

        return filesToDownload;
    }

    private void downloadAndExtractExecutableFiles(DriverContext driverContext, DriverDetails driverDetails) throws IOException, MojoExecutionException, URISyntaxException, MojoFailureException {
        String localZipFileAbsolutePath = this.downloadedZipFileDirectory + File.separator + FilenameUtils.getName(driverDetails.fileLocation.getFile());
        File localZipFile = new File(localZipFileAbsolutePath);
        boolean fileNeedsToBeDownloaded = true;

        if (localZipFile.exists()) {
            if (checkFileHash) {
                if (checkFileHash(localZipFile, driverDetails.hash, driverDetails.hashType)) {
                    fileNeedsToBeDownloaded = false;
                } else {
                    fileNeedsToBeDownloaded = true;
                }
            }
        }

        if (fileNeedsToBeDownloaded) {
            localZipFile = downloadFile(driverDetails, checkFileHash);
        }

        String extractedFileLocation = this.rootStandaloneServerDirectory.getAbsolutePath() + File.separator + driverContext.buildExtractionPathFromDriverContext();
        FileExtractor fileExtractor = new FileExtractor(this.overwriteFilesThatExist);
        driverDetails.extractedLocation = fileExtractor.extractFileFromArchive(localZipFile, extractedFileLocation, driverContext.getBinaryTypeForContext());
    }

}

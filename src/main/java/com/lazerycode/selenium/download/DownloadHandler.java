package com.lazerycode.selenium.download;

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
import java.util.Map;

import static com.lazerycode.selenium.extract.ExtractFilesFromArchive.extractFileFromArchive;

public class DownloadHandler {

    private static final Logger LOG = Logger.getLogger(DownloadHandler.class);
    private final DriverMap filesToDownload;
    private final File rootStandaloneServerDirectory;
    private final File downloadedZipFileDirectory;
    protected final int fileDownloadRetryAttempts;
    private boolean overwriteFilesThatExist = false;
    private boolean checkFileHash = true;
    private FileDownloader fileDownloader;

    public DownloadHandler(File rootStandaloneServerDirectory, File downloadedZipFileDirectory, int fileDownloadRetryAttempts, int fileDownloadConnectTimeout, int fileDownloadReadTimeout, DriverMap filesToDownload, boolean overwriteFilesThatExist, boolean checkFileHash, boolean useSystemProxy) throws MojoFailureException {
        this.rootStandaloneServerDirectory = rootStandaloneServerDirectory;
        this.downloadedZipFileDirectory = downloadedZipFileDirectory;
        this.filesToDownload = filesToDownload;
        this.fileDownloadRetryAttempts = fileDownloadRetryAttempts;
        this.overwriteFilesThatExist = overwriteFilesThatExist;
        this.checkFileHash = checkFileHash;

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
     * @return File
     * @throws MojoExecutionException
     */
    protected File downloadFile(DriverDetails driverDetails, boolean shouldWeCheckFileHash) throws MojoExecutionException, IOException, URISyntaxException {

        URL remoteFileLocation = driverDetails.fileLocation;

        final String filename = FilenameUtils.getName(remoteFileLocation.getFile());
        for (int retryAttempts = 1; retryAttempts <= this.fileDownloadRetryAttempts; retryAttempts++) {
            File downloadedFile = fileDownloader.attemptToDownload(remoteFileLocation);
            if (null != downloadedFile) {
                if (!shouldWeCheckFileHash || (shouldWeCheckFileHash && checkFileHash(downloadedFile, driverDetails.hash, driverDetails.hashType))) {
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

    public void ensureStandaloneExecutableFilesExist() throws MojoFailureException, MojoExecutionException, IOException, URISyntaxException {
        LOG.info("Archives will be downloaded to '" + this.downloadedZipFileDirectory.getAbsolutePath() + "'");
        LOG.info("Standalone executable files will be extracted to '" + this.rootStandaloneServerDirectory + "'");
        LOG.info(" ");
        LOG.info("Preparing to download Selenium Standalone Executable Binaries...");

        for (final DriverContext driverContext : filesToDownload.getKeys()) {
            Map<String, DriverDetails> driverDetails = filesToDownload.getMapForDriverContext(driverContext);
            for (String version : driverDetails.keySet()) {
                URL fileLocation = driverDetails.get(version).fileLocation;
                String hash = driverDetails.get(version).hash;
                HashType hashType = driverDetails.get(version).hashType;
                String localZipFileAbsolutePath = this.downloadedZipFileDirectory + File.separator + FilenameUtils.getName(fileLocation.getFile());
                File localZipFile = new File(localZipFileAbsolutePath);
                boolean fileNeedsToBeDownloaded = true;

                if (localZipFile.exists()) {
                    if (checkFileHash) {
                        if (checkFileHash(localZipFile, hash, hashType)) {
                            fileNeedsToBeDownloaded = false;
                        } else {
                            fileNeedsToBeDownloaded = false;
                        }
                    }
                }

                if (fileNeedsToBeDownloaded) {
                    localZipFile = downloadFile(driverDetails.get(version), checkFileHash);
                }

                String extractedFileLocation = this.rootStandaloneServerDirectory.getAbsolutePath() + File.separator + driverContext.buildExtractionPathFromDriverContext();
                if (extractFileFromArchive(localZipFile, extractedFileLocation, this.overwriteFilesThatExist, driverContext.getBinaryTypeForContext())) {
                    LOG.info("File(s) copied to " + extractedFileLocation);
                }
            }
        }
    }
}
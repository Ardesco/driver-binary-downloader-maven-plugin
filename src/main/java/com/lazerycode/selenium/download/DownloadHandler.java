package com.lazerycode.selenium.download;

import com.lazerycode.selenium.repository.BinaryType;
import com.lazerycode.selenium.extract.ExtractFilesFromArchive;
import com.lazerycode.selenium.repository.FileDetails;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class DownloadHandler {

    private static final Logger LOG = Logger.getLogger(DownloadHandler.class);
    private final Map<String, FileDetails> filesToDownload;
    private final File rootStandaloneServerDirectory;
    private final File downloadedZipFileDirectory;
    protected final int fileDownloadRetryAttempts;
    private boolean overwriteFilesThatExist = false;
    private boolean checkFileHash = true;
    private FileDownloader fileDownloader;

    public DownloadHandler(File rootStandaloneServerDirectory, File downloadedZipFileDirectory, int fileDownloadRetryAttempts, int fileDownloadConnectTimeout, int fileDownloadReadTimeout, Map<String, FileDetails> filesToDownload, boolean overwriteFilesThatExist, boolean checkFileHash, boolean useSystemProxy) throws MojoFailureException {
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

    public void ensureStandaloneExecutableFilesExist() throws MojoFailureException, MojoExecutionException, IOException, URISyntaxException {
        LOG.info("Archives will be downloaded to '" + this.downloadedZipFileDirectory.getAbsolutePath() + "'");
        LOG.info("Standalone executable files will be extracted to '" + this.rootStandaloneServerDirectory + "'");
        LOG.info(" ");
        LOG.info("Preparing to download Selenium Standalone Executable Binaries...");
        for (Map.Entry<String, FileDetails> fileToDownload : this.filesToDownload.entrySet()) {
            LOG.info(" ");
            String currentFileAbsolutePath = this.downloadedZipFileDirectory + File.separator + FilenameUtils.getName(fileToDownload.getValue().getFileLocation().getFile());
            File desiredFile = new File(currentFileAbsolutePath);
            File fileToUnzip = null;
            LOG.info("Archive file '" + desiredFile.getName() + "' exists   : " + desiredFile.exists());
            if (desiredFile.exists()) {
                if (checkFileHash) {
                    FileHashChecker fileHashChecker = new FileHashChecker(desiredFile);
                    fileHashChecker.setExpectedHash(fileToDownload.getValue().getHash(), fileToDownload.getValue().getHashType());
                    boolean fileIsValid = fileHashChecker.fileIsValid();
                    LOG.info("Archive file '" + desiredFile.getName() + "' is valid : " + fileIsValid);
                    if (fileIsValid) {
                        fileToUnzip = desiredFile;
                    }
                } else {
                    fileToUnzip = desiredFile;
                }
            }
            if (fileToUnzip == null) {
                fileToUnzip = downloadValidFile(fileToDownload.getValue());
            }
            String extractedFileLocation = this.rootStandaloneServerDirectory.getAbsolutePath() + File.separator + fileToDownload.getKey();
            String binaryForOperatingSystem = fileToDownload.getKey().replace("\\", "/").split("/")[1].toUpperCase();  //TODO should really store the OSType we have extracted somewhere rather than doing this hack!
            LOG.debug("Detected a binary for OSType: " + binaryForOperatingSystem);
            if (ExtractFilesFromArchive.extractFileFromArchive(fileToUnzip, extractedFileLocation, this.overwriteFilesThatExist, BinaryType.valueOf(binaryForOperatingSystem))) {
                LOG.info("File(s) copied to " + extractedFileLocation);
            }
        }
    }

    /**
     * Perform the file download
     *
     * @return File
     * @throws MojoExecutionException
     */
    File downloadValidFile(FileDetails fileDetails) throws MojoExecutionException, IOException, URISyntaxException {
        final String filename = FilenameUtils.getName(fileDetails.getFileLocation().getFile());
        for (int retryAttempts = 1; retryAttempts <= this.fileDownloadRetryAttempts; retryAttempts++) {

            File downloadedFile = fileDownloader.attemptToDownload(fileDetails.getFileLocation());

            if (null != downloadedFile) {
                if (!checkFileHash) {
                    return downloadedFile;
                }

                FileHashChecker fileHashChecker = new FileHashChecker(downloadedFile);
                fileHashChecker.setExpectedHash(fileDetails.getHash(), fileDetails.getHashType());
                boolean isFileValid = fileHashChecker.fileIsValid();
                LOG.info("Archive file '" + downloadedFile.getName() + "' is valid : " + isFileValid);
                if (isFileValid) {
                    return downloadedFile;
                }
            }

            LOG.info("Problem downloading '" + filename + "'... ");

            if (retryAttempts < this.fileDownloadRetryAttempts) {
                LOG.info("Retry attempt " + (retryAttempts) + " for '" + filename + "'");
            }
        }

        throw new MojoExecutionException("Unable to successfully download '" + filename + "'!");
    }
}

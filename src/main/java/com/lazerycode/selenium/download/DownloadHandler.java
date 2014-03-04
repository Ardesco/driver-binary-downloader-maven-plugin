package com.lazerycode.selenium.download;

import com.lazerycode.selenium.extract.BinaryFileNames;
import com.lazerycode.selenium.extract.ExtractFilesFromArchive;
import com.lazerycode.selenium.hash.CheckFileHash;
import com.lazerycode.selenium.hash.HashType;
import com.lazerycode.selenium.repository.FileDetails;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import static org.apache.commons.io.FileUtils.copyURLToFile;

public class DownloadHandler {

    private static final Logger LOG = Logger.getLogger(DownloadHandler.class);
    private File rootStandaloneServerDirectory;
    private File downloadedZipFileDirectory;
    private int fileDownloadReadTimeout;
    private int fileDownloadConnectTimeout;
    protected int fileDownloadRetryAttempts;
    private Map<String, FileDetails> filesToDownload;
    private boolean overwriteFilesThatExist = false;

    public DownloadHandler(File rootStandaloneServerDirectory, File downloadedZipFileDirectory, int fileDownloadRetryAttempts, int fileDownloadConnectTimeout, int fileDownloadReadTimeout, Map<String, FileDetails> filesToDownload, boolean overwriteFilesThatExist) {
        this.rootStandaloneServerDirectory = rootStandaloneServerDirectory;
        this.downloadedZipFileDirectory = downloadedZipFileDirectory;
        if (fileDownloadRetryAttempts < 1) {
            LOG.warn("Invalid number of retry attempts specified, defaulting to '1'...");
            this.fileDownloadRetryAttempts = 1;
        } else {
            this.fileDownloadRetryAttempts = fileDownloadRetryAttempts;
        }
        this.fileDownloadConnectTimeout = fileDownloadConnectTimeout;
        this.fileDownloadReadTimeout = fileDownloadReadTimeout;
        this.filesToDownload = filesToDownload;
        this.overwriteFilesThatExist = overwriteFilesThatExist;
    }

    public void getStandaloneExecutableFiles() throws Exception {
        LOG.info("Archives will be downloaded to '" + this.downloadedZipFileDirectory.getAbsolutePath() + "'");
        LOG.info("Standalone executable files will be extracted to '" + this.rootStandaloneServerDirectory + "'");
        LOG.info(" ");
        LOG.info("Preparing to download Selenium Standalone Executable Binaries...");
        LOG.info(" ");
        File fileToUnzip;
        for (Map.Entry<String, FileDetails> fileToDownload : this.filesToDownload.entrySet()) {
            LOG.info(" ");
            String currentFileAbsolutePath = this.downloadedZipFileDirectory + File.separator + FilenameUtils.getName(fileToDownload.getValue().getFileLocation().getFile());
            LOG.info("Checking to see if archive file '" + currentFileAbsolutePath + "' already exists and is valid.");
            boolean existsAndIsValid = fileExistsAndIsValid(new File(currentFileAbsolutePath), fileToDownload.getValue().getHash(), fileToDownload.getValue().getHashType());
            if (!existsAndIsValid) {
                fileToUnzip = downloadFile(fileToDownload.getValue().getFileLocation(), fileToDownload.getValue().getHash(), fileToDownload.getValue().getHashType());
            } else {
                fileToUnzip = new File(currentFileAbsolutePath);
            }
            String extractionDirectory = this.rootStandaloneServerDirectory.getAbsolutePath() + File.separator + fileToDownload.getKey();
            String binaryType = fileToDownload.getKey().replace("\\", "/").split("/")[1].toUpperCase();  //TODO this is a real hack, do this a better way!
            LOG.debug("Detected a binary type of: " + binaryType);
            if (ExtractFilesFromArchive.extractFileFromArchive(fileToUnzip, extractionDirectory, this.overwriteFilesThatExist, BinaryFileNames.valueOf(binaryType))) {
                LOG.info("File(s) copied to " + extractionDirectory);
            }
        }
    }

    /**
     * Perform the file download
     *
     * @return File
     * @throws MojoExecutionException
     */
    protected File downloadFile(URL remoteFile, String expectedHash, HashType expectedHashType) throws Exception {
        String filename = FilenameUtils.getName(remoteFile.getFile());
        File fileToDownload = new File(localFilePath(this.downloadedZipFileDirectory) + File.separator + filename);
        for (int n = 0; n < this.fileDownloadRetryAttempts; n++) {
            try {
                LOG.info("Downloading '" + filename + "'...");
                copyURLToFile(remoteFile, fileToDownload, this.fileDownloadConnectTimeout, this.fileDownloadReadTimeout);
                LOG.info("Checking to see if downloaded copy of '" + fileToDownload.getName() + "' is valid.");
                if (fileExistsAndIsValid(fileToDownload, expectedHash, expectedHashType)) return fileToDownload;
            } catch (IOException ex) {
                LOG.info("Problem downloading '" + fileToDownload.getName() + "'... " + ex.getLocalizedMessage());
                if (n + 1 < this.fileDownloadRetryAttempts)
                    LOG.info("Trying to download'" + fileToDownload.getName() + "' again...");
            }
        }

        LOG.error("Unable to successfully downloaded '" + fileToDownload.getName() + "'!");
        throw new MojoExecutionException("Unable to successfully downloaded '" + fileToDownload.getName() + "'!");
    }

    /**
     * Set the location directory where files will be downloaded to/
     *
     * @param downloadDirectory The directory that the file will be downloaded to.
     */
    private String localFilePath(File downloadDirectory) throws MojoFailureException {
        if (!downloadDirectory.exists()) {
            if (!downloadDirectory.mkdirs()) {
                throw new MojoFailureException("Unable to create download directory!");
            }
        }
        if (!downloadDirectory.isDirectory()) {
            throw new MojoFailureException("'" + downloadDirectory.getAbsolutePath() + "' is not a directory!");
        }
        return downloadDirectory.getAbsolutePath();
    }

    /**
     * Check if the file exists and perform a Hash check on it to see if it is valid
     *
     * @param fileToCheck the file to perform a hash validation against
     * @return true if the file exists and is valid
     * @throws IOException
     */
    protected boolean fileExistsAndIsValid(File fileToCheck, String expectedHash, HashType expectedHashType) throws IOException, MojoExecutionException {
        if (fileToCheck.exists()) {
            CheckFileHash hashChecker = new CheckFileHash();
            hashChecker.hashDetails(expectedHash, expectedHashType);
            hashChecker.fileToCheck(fileToCheck);
            return hashChecker.hasAValidHash();
        }
        return false;
    }

}

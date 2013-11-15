package com.lazerycode.selenium.download;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.apache.commons.io.FileUtils.copyURLToFile;

public class FileDownloader {

    private static final Logger LOG = Logger.getLogger(FileDownloader.class);
    private URL remoteFile;
    private String filename;
    private String expectedHash;
    private HashType expectedHashType;
    private String fileDownloadDirectory;
    private int readTimeout = 15000;
    private int connectTimeout = 15000;
    protected int totalNumberOfRetryAttempts = 1;

    public FileDownloader(File downloadDirectory, int retries, int connectTimeout, int readTimeout) throws MojoFailureException {
        localFilePath(downloadDirectory);
        specifyTotalNumberOfRetryAttempts(retries);
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    /**
     * Set the number of retry attempts to use when downloading the file
     *
     * @param retries
     */
    private void specifyTotalNumberOfRetryAttempts(int retries) {
        if (retries < 1) {
            LOG.warn("Invalid number of retry attempts specified, defaulting to '1'...");
            this.totalNumberOfRetryAttempts = 1;
        } else {
            this.totalNumberOfRetryAttempts = retries;
        }
    }

    /**
     * Set the location directory where files will be downloaded to/
     *
     * @param downloadDirectory The directory that the file will be downloaded to.
     */
    private void localFilePath(File downloadDirectory) throws MojoFailureException {
        if (!downloadDirectory.exists()) downloadDirectory.mkdirs();
        if (!downloadDirectory.isDirectory()) throw new MojoFailureException("'" + downloadDirectory.getAbsolutePath() + "' is not a directory!");
        this.fileDownloadDirectory = downloadDirectory.getAbsolutePath();
    }

    /**
     * Check if the file exists and perform a getHash check on it to see if it is valid
     *
     * @param fileToCheck
     * @return
     * @throws IOException
     */
    public boolean fileExistsAndIsValid(File fileToCheck) throws IOException, MojoExecutionException {
        if (fileToCheck.exists()) {
            CheckFileHash hashChecker = new CheckFileHash();
            hashChecker.hashDetails(this.expectedHash, this.expectedHashType);
            hashChecker.fileToCheck(fileToCheck);
            return hashChecker.hasAValidHash();
        }
        return false;
    }

    /**
     * Set the URL that will be used to download the remote file.
     *
     * @param value
     * @throws MojoExecutionException
     */
    public void remoteURL(URL value) throws MojoExecutionException {
        this.remoteFile = value;
        this.filename = FilenameUtils.getName(value.getFile());
    }

    /**
     * Set the getHash and getHash type that will be used to check that the file is valid
     *
     * @param hashValue
     * @param hashType
     */
    public void setHash(String hashValue, HashType hashType) {
        this.expectedHash = hashValue;
        this.expectedHashType = hashType;
    }

    /**
     * Perform the file download
     *
     * @return File
     * @throws MojoExecutionException
     */
    public File downloadFile() throws MojoExecutionException {
        File fileToDownload = new File(this.fileDownloadDirectory + File.separator + this.filename);
        for (int n = 0; n < this.totalNumberOfRetryAttempts; n++) {
            try {
                LOG.info("Downloading '" + this.filename + "'...");
                copyURLToFile(this.remoteFile, fileToDownload, this.connectTimeout, this.readTimeout);
                LOG.info("Checking to see if downloaded copy of '" + fileToDownload.getName() + "' is valid.");
                if (fileExistsAndIsValid(fileToDownload)) return fileToDownload;
            } catch (IOException ex) {
                LOG.info("Problem downloading '" + fileToDownload.getName() + "'... " + ex.getLocalizedMessage());
                if (n + 1 < this.totalNumberOfRetryAttempts) LOG.info("Trying to download'" + fileToDownload.getName() + "' again...");
            }
        }

        LOG.error("Unable to successfully downloaded '" + fileToDownload.getName() + "'!");
        throw new MojoExecutionException("Unable to successfully downloaded '" + fileToDownload.getName() + "'!");
    }
}
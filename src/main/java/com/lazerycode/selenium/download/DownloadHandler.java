package com.lazerycode.selenium.download;

import com.lazerycode.selenium.extract.BinaryFileNames;
import com.lazerycode.selenium.extract.ExtractFilesFromArchive;
import com.lazerycode.selenium.repository.FileDetails;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;

public class DownloadHandler {

    private static final Logger LOG = Logger.getLogger(DownloadHandler.class);
    private final File rootStandaloneServerDirectory;
    private final File downloadedZipFileDirectory;
    private final int fileDownloadReadTimeout;
    private final int fileDownloadConnectTimeout;
    private final Map<String, FileDetails> filesToDownload;
    final int fileDownloadRetryAttempts;
    private boolean overwriteFilesThatExist = false;
    private boolean checkFileHash = true;
    private boolean useSystemProxy = true;

    public DownloadHandler(File rootStandaloneServerDirectory, File downloadedZipFileDirectory, int fileDownloadRetryAttempts, int fileDownloadConnectTimeout, int fileDownloadReadTimeout, Map<String, FileDetails> filesToDownload, boolean overwriteFilesThatExist, boolean checkFileHash, boolean useSystemProxy) {
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
        this.checkFileHash = checkFileHash;
        this.useSystemProxy = useSystemProxy;
    }

    public void getStandaloneExecutableFiles() throws Exception {
        LOG.info("Archives will be downloaded to '" + this.downloadedZipFileDirectory.getAbsolutePath() + "'");
        LOG.info("Standalone executable files will be extracted to '" + this.rootStandaloneServerDirectory + "'");
        LOG.info(" ");
        LOG.info("Preparing to download Selenium Standalone Executable Binaries...");
        for (Map.Entry<String, FileDetails> fileToDownload : this.filesToDownload.entrySet()) {
            LOG.info(" ");
            String currentFileAbsolutePath = this.downloadedZipFileDirectory + File.separator + FilenameUtils.getName(fileToDownload.getValue().getFileLocation().getFile());
            File desiredFile = new File(currentFileAbsolutePath);
            File fileToUnzip = downloadFile(fileToDownload.getValue());
            LOG.info("Checking to see if archive file '" + currentFileAbsolutePath + "' exists  : " + desiredFile.exists());
            if (desiredFile.exists()) {
                if (checkFileHash) {
                    FileHashChecker fileHashChecker = new FileHashChecker(desiredFile);
                    fileHashChecker.setExpectedHash(fileToDownload.getValue().getHash(), fileToDownload.getValue().getHashType());
                    boolean fileIsValid = fileHashChecker.fileIsValid();
                    LOG.info("Checking to see if archive file '" + currentFileAbsolutePath + "' is valid: " + fileIsValid);
                    if (fileIsValid) {
                        fileToUnzip = new File(currentFileAbsolutePath);
                    }
                } else {
                    fileToUnzip = new File(currentFileAbsolutePath);
                }
            }
            String extractionDirectory = this.rootStandaloneServerDirectory.getAbsolutePath() + File.separator + fileToDownload.getKey();
            String binaryForOperatingSystem = fileToDownload.getKey().replace("\\", "/").split("/")[1].toUpperCase();  //TODO should really store the OSType we have extracted somewhere rather than doing this hack!
            LOG.debug("Detected a binary for OSType: " + binaryForOperatingSystem);
            if (ExtractFilesFromArchive.extractFileFromArchive(fileToUnzip, extractionDirectory, this.overwriteFilesThatExist, BinaryFileNames.valueOf(binaryForOperatingSystem))) {
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
    File downloadFile(FileDetails fileDetails) throws Exception {
        String filename = FilenameUtils.getName(fileDetails.getFileLocation().getFile());
        File fileToDownload = new File(localFilePath(this.downloadedZipFileDirectory) + File.separator + filename);
        for (int n = 0; n < this.fileDownloadRetryAttempts; n++) {
            try {
                LOG.debug("Downloading '" + filename + "'...");
                SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(this.fileDownloadReadTimeout).build();
                RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(fileDownloadConnectTimeout).build();
                HttpClientBuilder httpClientBuilder = HttpClients.custom()
                        .setDefaultSocketConfig(socketConfig)
                        .setDefaultRequestConfig(requestConfig)
                        .disableContentCompression();
                if (this.useSystemProxy) {
                    DetectProxyConfig proxyConfig = new DetectProxyConfig();
                    if (proxyConfig.isProxyAvailable()) {
                        LOG.info("Setting http proxy to: " + proxyConfig.getHost() + ":" + proxyConfig.getPort());
                        httpClientBuilder.setProxy(new HttpHost(proxyConfig.getHost(), proxyConfig.getPort()));
                    }
                }
                CloseableHttpClient httpClient = httpClientBuilder.build();
                CloseableHttpResponse fileDownloadResponse = httpClient.execute(new HttpGet(fileDetails.getFileLocation().toURI()));
                try {
                    HttpEntity remoteFileStream = fileDownloadResponse.getEntity();
                    copyInputStreamToFile(remoteFileStream.getContent(), fileToDownload);
                } finally {
                    fileDownloadResponse.close();
                }

                if (!checkFileHash) {
                    return fileToDownload;
                }

                LOG.info("Checking to see if downloaded copy of '" + fileToDownload.getName() + "' is valid.");
                FileHashChecker fileHashChecker = new FileHashChecker(fileToDownload);
                fileHashChecker.setExpectedHash(fileDetails.getHash(), fileDetails.getHashType());
                if (fileHashChecker.fileIsValid()) {
                    return fileToDownload;
                }
            } catch (IOException ex) {
                LOG.info("Problem downloading '" + fileToDownload.getName() + "'... " + ex.getCause().getLocalizedMessage());
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
        if (downloadDirectory.exists()) {
            if (downloadDirectory.isDirectory()) {
                return downloadDirectory.getAbsolutePath();
            } else {
                throw new MojoFailureException("'" + downloadDirectory.getAbsolutePath() + "' is not a directory!");
            }
        }

        if (downloadDirectory.mkdirs()) {
            return downloadDirectory.getAbsolutePath();
        } else {
            throw new MojoFailureException("Unable to create download directory!");
        }
    }
}

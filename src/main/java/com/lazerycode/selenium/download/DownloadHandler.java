package com.lazerycode.selenium.download;

import com.lazerycode.selenium.extract.BinaryFileNames;
import com.lazerycode.selenium.extract.ExtractFilesFromArchive;
import com.lazerycode.selenium.hash.HashType;
import com.lazerycode.selenium.repository.FileDetails;
import org.apache.commons.codec.digest.DigestUtils;
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
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
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
        LOG.info(" ");
        File fileToUnzip;
        for (Map.Entry<String, FileDetails> fileToDownload : this.filesToDownload.entrySet()) {
            LOG.info(" ");
            String currentFileAbsolutePath = this.downloadedZipFileDirectory + File.separator + FilenameUtils.getName(fileToDownload.getValue().getFileLocation().getFile());
            LOG.info("Checking to see if archive file '" + currentFileAbsolutePath + "' already exists and is valid.");
            boolean existsAndIsValid = fileExistsAndIsValid(new File(currentFileAbsolutePath), fileToDownload.getValue().getHash(), fileToDownload.getValue().getHashType());
            if (!existsAndIsValid) {
                fileToUnzip = downloadFile(fileToDownload.getValue());
            } else {
                fileToUnzip = new File(currentFileAbsolutePath);
            }
            String extractionDirectory = this.rootStandaloneServerDirectory.getAbsolutePath() + File.separator + fileToDownload.getKey();
            String binaryForOperatingSystem = fileToDownload.getKey().replace("\\", "/").split("/")[1].toUpperCase();  //TODO should really store the OS we have extracted somewhere rather than doing this hack!
            LOG.debug("Detected a binary for OS: " + binaryForOperatingSystem);
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
                    String httpProxy = System.getenv("http_proxy");
                    if (StringUtils.isNotEmpty(httpProxy)) {
                        LOG.info("Setting http proxy to: " + httpProxy);
                        URL url = new URL(httpProxy);
                        httpClientBuilder.setProxy(new HttpHost(url.getHost(), url.getPort()));
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
                LOG.info("Checking to see if downloaded copy of '" + fileToDownload.getName() + "' is valid.");
                if (fileExistsAndIsValid(fileToDownload, fileDetails.getHash(), fileDetails.getHashType())) return fileToDownload;
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
    boolean fileExistsAndIsValid(File fileToCheck, String expectedHash, HashType hashType) throws IOException, MojoExecutionException {
        if (!fileToCheck.exists()) return false;
        if (!checkFileHash) return true;
        if (null == expectedHash) {
            throw new MojoExecutionException("The hash for " + fileToCheck.getName() + " is missing from your RepositoryMap.xml");
        } else if (null == hashType) {
            throw new MojoExecutionException("The hashtype for " + fileToCheck.getName() + " is missing from your RepositoryMap.xml");
        }
        String actualFileHash;
        FileInputStream fileToHashCheck = new FileInputStream(fileToCheck);
        switch (hashType) {
            case MD5:
                actualFileHash = DigestUtils.md5Hex(fileToHashCheck);
                break;
            case SHA1:
            default:
                actualFileHash = DigestUtils.shaHex(fileToHashCheck);
                break;
        }
        fileToHashCheck.close();
        boolean result = actualFileHash.equals(expectedHash);
        if (!result) {
            LOG.info("Expected file hash to be '" + expectedHash + "'.");
            LOG.info("Actual file hash was '" + actualFileHash + "'.");
        }
        return result;
    }

}

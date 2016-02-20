package com.lazerycode.selenium.download;

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
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;

public class FileDownloader {

    private static final Logger LOG = Logger.getLogger(FileDownloader.class);
    private int fileDownloadReadTimeout;
    private int fileDownloadConnectTimeout;
    private boolean useSystemProxy = true;
    String downloadDirectory;
    DetectProxyConfig proxyConfig;


    FileDownloader(File downloadedZipFileDirectory, boolean useSystemProxy) throws MojoFailureException {
        this.useSystemProxy = useSystemProxy;
        if (this.useSystemProxy) {
            proxyConfig = new DetectProxyConfig();
        }
        this.downloadDirectory = localFilePath(downloadedZipFileDirectory);
    }

    public void setReadTimeout(int fileDownloadReadTimeout) {
        this.fileDownloadReadTimeout = fileDownloadReadTimeout;
    }

    public void setConnectTimeout(int fileDownloadConnectTimeout) {
        this.fileDownloadConnectTimeout = fileDownloadConnectTimeout;
    }

    /**
     * Attempt to download a file
     *
     * @param fileLocation URL of the file to download
     *
     * @return File
     * @throws URISyntaxException Invalid URI
     * @throws IOException Unable to interact with file system
     */
    public File attemptToDownload(URL fileLocation) throws URISyntaxException, IOException {
        String filename = FilenameUtils.getName(fileLocation.getFile());
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(fileDownloadReadTimeout).build();

        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(fileDownloadConnectTimeout).build();

        HttpClientBuilder httpClientBuilder = HttpClients.custom()
                .setDefaultSocketConfig(socketConfig)
                .setDefaultRequestConfig(requestConfig)
                .disableContentCompression();

        if (useSystemProxy && proxyConfig.isProxyAvailable()) {
            LOG.info("Using http proxy: " + proxyConfig.getHost() + ":" + proxyConfig.getPort());
            httpClientBuilder.setProxy(new HttpHost(proxyConfig.getHost(), proxyConfig.getPort()));
        }

        CloseableHttpClient httpClient = httpClientBuilder.build();

        LOG.info("Downloading  '" + filename + "'...");
        CloseableHttpResponse fileDownloadResponse = httpClient.execute(new HttpGet(fileLocation.toURI()));

        HttpEntity remoteFileStream = fileDownloadResponse.getEntity();
        File fileToDownload = new File(downloadDirectory + File.separator + filename);
        try {
            copyInputStreamToFile(remoteFileStream.getContent(), fileToDownload);
        } catch (IOException ex) {
            LOG.error("Problem downloading '" + filename + "'... " + ex.getCause().getLocalizedMessage());
            fileToDownload = null;
        } finally {
            fileDownloadResponse.close();
        }
        return fileToDownload;
    }

    /**
     * Set the location directory where files will be downloaded to
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

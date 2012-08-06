package com.lazerycode.selenium.download;

import com.lazerycode.selenium.repository.FileDetails;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

public class DownloadHandler {

    private static final Logger LOG = Logger.getLogger(DownloadHandler.class);
    private File rootStandaloneServerDirectory;
    private File downloadedZipFileDirectory;
    private String downloadedZipFileDirectoryPath;
    private int fileDownloadRetryAttempts;
    private int fileDownloadConnectTimeout;
    private int fileDownloadReadTimeout;
    private Map<String, FileDetails> filesToDownload;

    public DownloadHandler(File rootStandaloneServerDirectory, File downloadedZipFileDirectory, int fileDownloadRetryAttempts, int fileDownloadConnectTimeout, int fileDownloadReadTimeout, Map<String, FileDetails> filesToDownload) throws Exception {
        this.rootStandaloneServerDirectory = rootStandaloneServerDirectory;
        this.downloadedZipFileDirectory = downloadedZipFileDirectory;
        this.downloadedZipFileDirectoryPath = downloadedZipFileDirectory.getAbsolutePath();
        this.fileDownloadRetryAttempts = fileDownloadRetryAttempts;
        this.fileDownloadConnectTimeout = fileDownloadConnectTimeout;
        this.fileDownloadReadTimeout = fileDownloadReadTimeout;
        this.filesToDownload = filesToDownload;
    }

    public void getStandaloneExecutables() throws Exception {
        LOG.info("Preparing to download Selenium Standalone Executable Binaries...");
        FileDownloader downloader = new FileDownloader(this.downloadedZipFileDirectory, this.fileDownloadRetryAttempts, this.fileDownloadConnectTimeout, this.fileDownloadReadTimeout);
        ExtractFilesFromZip fileExtractor = new ExtractFilesFromZip(this.downloadedZipFileDirectoryPath);
        for (Iterator iterator = this.filesToDownload.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, FileDetails> fileToDownload = (Map.Entry<String, FileDetails>) iterator.next();
            downloader.remoteURL(fileToDownload.getValue().getFileLocation());
            downloader.setHash(fileToDownload.getValue().getHash(), fileToDownload.getValue().getHashType());
            //TODO pass in root standalone server directory, extration path should be relative to this
            fileExtractor.unzipFile(downloader.downloadFile(), fileToDownload.getKey());
            LOG.info("File(s) copied to " + fileToDownload.getKey());
        }
    }

}

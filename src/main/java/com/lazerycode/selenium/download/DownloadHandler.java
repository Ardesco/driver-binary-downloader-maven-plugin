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
    private int fileDownloadRetryAttempts;
    private int fileDownloadConnectTimeout;
    private int fileDownloadReadTimeout;
    private Map<String, FileDetails> filesToDownload;
    private boolean overwriteFilesThatExist = false;

    public DownloadHandler(File rootStandaloneServerDirectory, File downloadedZipFileDirectory, int fileDownloadRetryAttempts, int fileDownloadConnectTimeout, int fileDownloadReadTimeout, Map<String, FileDetails> filesToDownload, boolean overwriteFilesThatExist) throws Exception {
        this.rootStandaloneServerDirectory = rootStandaloneServerDirectory;
        this.downloadedZipFileDirectory = downloadedZipFileDirectory;
        this.fileDownloadRetryAttempts = fileDownloadRetryAttempts;
        this.fileDownloadConnectTimeout = fileDownloadConnectTimeout;
        this.fileDownloadReadTimeout = fileDownloadReadTimeout;
        this.filesToDownload = filesToDownload;
        this.overwriteFilesThatExist = overwriteFilesThatExist;
    }

    public void getStandaloneExecutableFiles() throws Exception {
        LOG.info("Preparing to download Selenium Standalone Executable Binaries...");
        File fileToUnzip = null;
        FileDownloader downloader = new FileDownloader(this.downloadedZipFileDirectory, this.fileDownloadRetryAttempts, this.fileDownloadConnectTimeout, this.fileDownloadReadTimeout);
        for (Iterator iterator = this.filesToDownload.entrySet().iterator(); iterator.hasNext(); ) {
            boolean extractFilesFromZip = this.overwriteFilesThatExist;
            Map.Entry<String, FileDetails> fileToDownload = (Map.Entry<String, FileDetails>) iterator.next();
            downloader.remoteURL(fileToDownload.getValue().getFileLocation());
            downloader.setHash(fileToDownload.getValue().getHash(), fileToDownload.getValue().getHashType());
            LOG.info(" ");
            LOG.info("Checking to see if zip file '" + fileToDownload.getValue().getFileLocation().getFile() + "' already exists and is valid.");
            boolean existsAndIsValid = downloader.fileExistsAndIsValid(new File(this.downloadedZipFileDirectory + File.separator + fileToDownload.getValue().getFileLocation().getFile()));
            if (!existsAndIsValid) {
                extractFilesFromZip = true;
                fileToUnzip = downloader.downloadFile();
            }
            if (extractFilesFromZip) {
                ExtractFilesFromZip.unzipFile(fileToUnzip, this.rootStandaloneServerDirectory.getAbsolutePath() + File.separator + fileToDownload.getKey(), extractFilesFromZip);
                LOG.info("File(s) copied to " + fileToDownload.getKey());
            }

        }
    }

}

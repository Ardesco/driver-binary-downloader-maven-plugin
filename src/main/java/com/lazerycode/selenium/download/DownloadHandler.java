package com.lazerycode.selenium.download;

import com.lazerycode.selenium.repository.FileDetails;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.codehaus.plexus.util.FileUtils;

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
        LOG.info(" ");
        LOG.info("Zip files will be downloaded to '" + this.downloadedZipFileDirectory.getAbsolutePath() + "'");
        LOG.info("Standalone executable files will be extracted to '" + this.rootStandaloneServerDirectory + "'");
        LOG.info(" ");
        File fileToUnzip = null;
        FileDownloader downloader = new FileDownloader(this.downloadedZipFileDirectory, this.fileDownloadRetryAttempts, this.fileDownloadConnectTimeout, this.fileDownloadReadTimeout);
        for (Iterator iterator = this.filesToDownload.entrySet().iterator(); iterator.hasNext(); ) {
            boolean extractFilesFromZip = this.overwriteFilesThatExist;
            Map.Entry<String, FileDetails> fileToDownload = (Map.Entry<String, FileDetails>) iterator.next();
            downloader.remoteURL(fileToDownload.getValue().getFileLocation());
            downloader.setHash(fileToDownload.getValue().getHash(), fileToDownload.getValue().getHashType());
            LOG.info(" ");
            String currentFileAbsolutePath = this.downloadedZipFileDirectory + File.separator + FilenameUtils.getName(fileToDownload.getValue().getFileLocation().getFile());
            LOG.info("Checking to see if zip file '" + currentFileAbsolutePath + "' already exists and is valid.");
            boolean existsAndIsValid = downloader.fileExistsAndIsValid(new File(currentFileAbsolutePath));
            if (!existsAndIsValid) {
                extractFilesFromZip = true;
                fileToUnzip = downloader.downloadFile();
            }
            if (extractFilesFromZip) {
                String extractionDirectory = this.rootStandaloneServerDirectory.getAbsolutePath() + File.separator + fileToDownload.getKey();
                ExtractFilesFromZip.unzipFile(fileToUnzip, extractionDirectory, extractFilesFromZip);
                LOG.info("File(s) copied to " + extractionDirectory);
            }

        }
    }

}

package com.lazerycode.selenium.download;

import com.lazerycode.selenium.repository.FileDetails;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
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

  public DownloadHandler(File rootStandaloneServerDirectory, File downloadedZipFileDirectory, int fileDownloadRetryAttempts, int fileDownloadConnectTimeout, int fileDownloadReadTimeout, Map<String, FileDetails> filesToDownload, boolean overwriteFilesThatExist) {
    this.rootStandaloneServerDirectory = rootStandaloneServerDirectory;
    this.downloadedZipFileDirectory = downloadedZipFileDirectory;
    this.fileDownloadRetryAttempts = fileDownloadRetryAttempts;
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
    FileDownloader downloader = new FileDownloader(this.downloadedZipFileDirectory, this.fileDownloadRetryAttempts, this.fileDownloadConnectTimeout, this.fileDownloadReadTimeout);
    for (Map.Entry<String, FileDetails> fileToDownload : this.filesToDownload.entrySet()) {
      downloader.remoteURL(fileToDownload.getValue().getFileLocation());
      downloader.setHash(fileToDownload.getValue().getHash(), fileToDownload.getValue().getHashType());
      LOG.info(" ");
      String currentFileAbsolutePath = this.downloadedZipFileDirectory + File.separator + FilenameUtils.getName(fileToDownload.getValue().getFileLocation().getFile());
      LOG.info("Checking to see if archive file '" + currentFileAbsolutePath + "' already exists and is valid.");
      boolean existsAndIsValid = downloader.fileExistsAndIsValid(new File(currentFileAbsolutePath));
      if (!existsAndIsValid) {
        fileToUnzip = downloader.downloadFile();
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

}

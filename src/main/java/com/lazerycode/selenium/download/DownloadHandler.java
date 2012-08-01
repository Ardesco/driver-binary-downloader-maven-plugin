package com.lazerycode.selenium.download;

import com.lazerycode.selenium.Bit;
import com.lazerycode.selenium.Driver;
import com.lazerycode.selenium.OS;
import com.lazerycode.selenium.configuration.BitRate;
import com.lazerycode.selenium.repository.RepositoryParser;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

public class DownloadHandler {

    private static final Logger LOG = Logger.getLogger(DownloadHandler.class);
    private Map<String, String> browserAndVersion;
    private File repositoryMap;
    private RepositoryParser getStandaloneExecutible;
    private int fileDownloadRetryAttempts;
    private File rootStandaloneServerDirectory;
    private File downloadedZipFileDirectory;
    private OS osList;
    private BitRate bitRate;
    private int fileDownloadConnectTimeout;
    private int fileDownloadReadTimeout;

    public DownloadHandler(Map<String, String> browserAndVersion, File repositoryMap, File rootStandaloneServerDirectory, File downloadedZipFileDirectory, int fileDownloadRetryAttempts, BitRate bitRate, OS operatingSystems, int fileDownloadConnectTimeout, int fileDownloadReadTimeout) throws Exception {
        this.browserAndVersion = browserAndVersion;
        this.repositoryMap = repositoryMap;
        this.getStandaloneExecutible = new RepositoryParser(this.repositoryMap);
        this.rootStandaloneServerDirectory = rootStandaloneServerDirectory;
        this.downloadedZipFileDirectory = downloadedZipFileDirectory;
        this.fileDownloadRetryAttempts = fileDownloadRetryAttempts;
        this.bitRate = bitRate;
        this.osList = operatingSystems;
        this.fileDownloadConnectTimeout = fileDownloadConnectTimeout;
        this.fileDownloadReadTimeout = fileDownloadReadTimeout;
    }

    public void getStandaloneExecutables() throws Exception {
        LOG.info("Preparing to download Selenium Standalone Executables");
        Iterator it = this.browserAndVersion.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            //TODO build XPATH to getStandaloneExecutible location
            //root/${BROWSER}/${VERSION}/${OS}[${BITRATE}/filelocation
            //TODO log the executable we are trying to download.
            FileDownloader downloader = new FileDownloader(this.downloadedZipFileDirectory, this.fileDownloadRetryAttempts, this.fileDownloadConnectTimeout, this.fileDownloadReadTimeout);
            downloader.remoteURL(new URL(this.getStandaloneExecutible.forDriver(Driver.IE, "2.21.0").andOS(Bit.SIXTYFOURBIT, OS.WINDOWS).returnFilePath()));
            ExtractFilesFromZip fileExtractor = new ExtractFilesFromZip("Path to extract to");
            fileExtractor.unzipFile(downloader.downloadFile());
            LOG.info("File copied to " + fileExtractor.getExtractedFileAbsolutePath());
        }
    }

}

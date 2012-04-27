package com.lazerycode.selenium;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.apache.commons.io.FileUtils.copyURLToFile;
import static org.codehaus.plexus.util.FileUtils.cleanDirectory;
import static org.codehaus.plexus.util.FileUtils.deleteDirectory;

public class FileDownloader extends SeleniumServerMojo {

    private URL remoteFile;
    private String filename;
    private String downloadPath = System.getProperty("java.io.tmpdir") + "/" + UUID.randomUUID().toString().replaceAll("-", "") + "/";
    private String localFilePath;
    private int timeout = 30000;

    public FileDownloader() {
    }

    /**
     * Get the current location that files will be downloaded to.
     *
     * @return The filepath that the file will be downloaded to.
     */
    public String getLocalFilePath() {
        return this.localFilePath;
    }

    /**
     * Set the path that files will be downloaded to.
     *
     * @param value The filepath that the file will be downloaded to.
     */
    public void setLocalFilePath(String value) {
        this.localFilePath = value;
    }

    public void setRemoteURL(URL value) throws Exception {
        this.remoteFile = value;
        this.filename = value.getFile();
    }

    public URL getRemoteURL() {
        return this.remoteFile;
    }

    public String getFilename() {
        return this.filename;
    }

    public void downloadZipAndExtractFiles() throws Exception {
        File zipToDownload = new File(this.downloadPath + File.separator + this.filename);
        copyURLToFile(this.remoteFile, zipToDownload, this.timeout, this.timeout);
        //TODO Throw exception if file cannot be downloaded (enable a way to suppress this)
        //TODO Check SHA1 hash to ensure downloaded file is valid (if check turned on)
        //Extract files from zip file and copy them to correct location
        ZipFile zip = new ZipFile(zipToDownload);
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry zipFileEntry = entries.nextElement();
            File extractedFile = new File(this.localFilePath, zipFileEntry.getName());
            if (zipFileEntry.isDirectory()) {
                continue;
            } else if (!extractedFile.exists()) {
                extractedFile.getParentFile().mkdirs();
                extractedFile.createNewFile();
            }
            InputStream is = zip.getInputStream(zipFileEntry);
            OutputStream os = new FileOutputStream(extractedFile);
            while (is.available() > 0) {
                os.write(is.read());
            }
            os.close();
            is.close();
        }
        zip.close();
        cleanDirectory(this.downloadPath);
        deleteDirectory(this.downloadPath);
        getLog().info("File copied to " + this.localFilePath);
    }
}
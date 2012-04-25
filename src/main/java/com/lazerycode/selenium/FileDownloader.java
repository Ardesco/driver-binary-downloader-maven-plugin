package com.lazerycode.selenium;

import org.apache.commons.io.FileUtils;

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

    public void download() throws Exception {
        //Download zip file
        File downloadedZip = new File(this.downloadPath + File.separator + this.filename);
        copyURLToFile(this.remoteFile, downloadedZip, this.timeout, this.timeout);
        //Extract files from zip file and copy them to correct location
        ZipFile zip = new ZipFile(downloadedZip);
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry zipFileEntry = entries.nextElement();
            File extractedFile = new File(this.localFilePath, zipFileEntry.getName());
            if (zipFileEntry.isDirectory()) { // if its a directory, create it
                continue;
            }

            if (!extractedFile.exists()) {
                extractedFile.getParentFile().mkdirs();
                extractedFile.createNewFile();
            }

            InputStream is = zip.getInputStream(zipFileEntry); // get the input stream
            OutputStream os = new FileOutputStream(extractedFile);
            byte[] buffer = new byte[4096];
            int readData;
            while ((readData = is.read(buffer)) != -1) {
                os.write(buffer, 0, readData);
            }
            os.close();
            is.close();
        }
        zip.close();
        //Clean up temp dir
        cleanDirectory(this.downloadPath);
        deleteDirectory(this.downloadPath);
        getLog().info("File copied to " + new File(this.localFilePath).getAbsolutePath());
    }
}
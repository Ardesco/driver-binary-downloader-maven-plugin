package com.lazerycode.selenium.download;

import com.lazerycode.selenium.SeleniumServerMojo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.*;
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
    private boolean performHashCheck = true;
    private String SHA1Hash;
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
    public String localFilePath() {
        return this.localFilePath;
    }

    /**
     * Set the SHA1 hash that will be used to check that the file is valid
     *
     * @param value The SHA1 Hash
     */
    public void sha1Hash(String value) {
        this.SHA1Hash = value;
    }

    /**
     * Perform a SHA1 Hash check on downloaded files to ensure they are not corrupted.
     *
     * @param value
     */
    public void performSHA1HashCheck(boolean value) {
        this.performHashCheck = value;
    }

    /**
     * Set the path that files will be downloaded to.
     *
     * @param value The filepath that the file will be downloaded to.
     */
    public void localFilePath(String value) {
        this.localFilePath = value;
    }

    public void remoteURL(URL value) throws MojoExecutionException {
        this.remoteFile = value;
        this.filename = value.getFile();
    }

    public URL remoteURL() {
        return this.remoteFile;
    }

    public String localfilename() {
        return this.filename;
    }

    public void downloadZipAndExtractFiles() throws Exception {
        File zipToDownload = new File(this.downloadPath + File.separator + this.filename);
        copyURLToFile(this.remoteFile, zipToDownload, this.timeout, this.timeout);
        //TODO Will Throw an IOException if file cannot be downloaded (enable a way to suppress this?)
        //Perform hash check, throw exception if invalid
        //TODO retry if invalid?  If so how many retries (1 retry maybe, chance's of corrupting two times in a row is minimal more likely file has changed)?
        if(this.performHashCheck){
            if(!this.SHA1Hash.equals(DigestUtils.shaHex(new FileInputStream(zipToDownload)))){
                throw new MojoExecutionException("Hash for downloaded file does not match, '" + zipToDownload.getName() + "' is invalid!");
            }
        }
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
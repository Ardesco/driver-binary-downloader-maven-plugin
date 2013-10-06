package com.lazerycode.selenium.download;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CheckFileHash {

    private static final Logger LOG = Logger.getLogger(CheckFileHash.class);
    private HashType typeOfHash = null;
    private String expectedFileHash = null;
    private File fileToCheck = null;

    /**
     * The File to perform a Hash check upon
     *
     * @param fileToCheck check to see if file exists
     * @throws java.io.FileNotFoundException
     */
    public void fileToCheck(File fileToCheck) throws FileNotFoundException {
        boolean doesFileExist = fileToCheck.exists();
        LOG.info("Zip File Exists: " + doesFileExist);
        if (!doesFileExist) throw new FileNotFoundException(fileToCheck + " does not exist!");
        this.fileToCheck = fileToCheck;
    }

    /**
     * Hash details used to perform the Hash check
     *
     * @param hash set file hash
     * @param hashType set hash type
     */
    public void hashDetails(String hash, HashType hashType) {
        this.expectedFileHash = hash;
        this.typeOfHash = hashType;
    }

    /**
     * Performs a expectedFileHash check on a File.
     *
     * @return boolean
     * @throws java.io.IOException
     */
    public boolean hasAValidHash() throws IOException, MojoExecutionException {
        if (this.fileToCheck == null) throw new MojoExecutionException("File to check has not been set!");
        if (this.expectedFileHash == null || this.typeOfHash == null) throw new MojoExecutionException("Hash details have not been set!");
        if (!this.fileToCheck.exists()) return false;
        String actualFileHash = "";
        boolean isHashValid = false;
        LOG.debug("Expected Hash: '" + this.expectedFileHash + "'");
        FileInputStream fileToHashCheck;
        switch (this.typeOfHash) {
            case MD5:
                fileToHashCheck = new FileInputStream(this.fileToCheck);
                actualFileHash = DigestUtils.md5Hex(fileToHashCheck);
                fileToHashCheck.close();
                if (this.expectedFileHash.equals(actualFileHash)) isHashValid = true;
                break;
            case SHA1:
                fileToHashCheck = new FileInputStream(this.fileToCheck);
                actualFileHash = DigestUtils.shaHex(fileToHashCheck);
                fileToHashCheck.close();
                if (this.expectedFileHash.equals(actualFileHash)) isHashValid = true;
                break;
        }
        LOG.debug("Actual Hash: '" + actualFileHash + "'");
        LOG.info("Hashes Match: " + isHashValid);

        return isHashValid;
    }

}
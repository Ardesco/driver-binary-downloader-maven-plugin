package com.lazerycode.selenium.download;

import com.lazerycode.selenium.hash.HashType;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileHashChecker {

    private static final Logger LOG = Logger.getLogger(FileHashChecker.class);

    private File fileToCheck;
    private HashType hashType;
    private String expectedHash;

    FileHashChecker(File fileToCheck) {
        this.fileToCheck = fileToCheck;
    }

    public void setExpectedHash(String expectedHash, HashType hashType) {
        this.expectedHash = expectedHash;
        this.hashType = hashType;
    }

    public boolean fileIsValid() throws IOException, MojoExecutionException {
        if (!fileToCheck.exists()) return false;
        String actualFileHash;
        FileInputStream fileToHashCheck = new FileInputStream(fileToCheck);

        switch (hashType) {
            case MD5:
                actualFileHash = DigestUtils.md5Hex(fileToHashCheck);
                break;
            case SHA1:
            default:
                actualFileHash = DigestUtils.shaHex(fileToHashCheck);
                break;
        }

        fileToHashCheck.close();
        if (actualFileHash.equals(expectedHash)) {
            return true;
        }

        LOG.error("File : '" + fileToCheck.getName() + "'.");
        LOG.error("Expected file hash : '" + expectedHash + "'.");
        LOG.error("Actual file hash   : '" + actualFileHash + "'.");

        return false;
    }
}

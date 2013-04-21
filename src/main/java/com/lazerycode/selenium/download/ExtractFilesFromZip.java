package com.lazerycode.selenium.download;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ExtractFilesFromZip {

    private static final Logger LOG = Logger.getLogger(ExtractFilesFromZip.class);

    /**
     * Unzip a downloaded zip file (this will implicitly overwrite any existing files)
     *
     * @param downloadedZip           The downloaded zip file
     * @param extractedToFilePath     Path to extracted file
     * @param overwriteFilesThatExist Overwrite any existing files
     * @return boolean
     * @throws IOException
     */
    public static boolean unzipFile(File downloadedZip, String extractedToFilePath, boolean overwriteFilesThatExist) throws IOException {
        Boolean filesExtracted = false;
        ZipFile zip = new ZipFile(downloadedZip);
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry zipFileEntry = entries.nextElement();
            LOG.debug("Found: " + zipFileEntry.getName());
            if (zipFileEntry.isDirectory()) {
                LOG.debug(zipFileEntry.getName() + " is a directory, moving to next file...");
                LOG.debug(" ");
                continue;
            }
            File extractedFile = new File(extractedToFilePath, zipFileEntry.getName());
            LOG.info("File '" + extractedFile.getName() + "' Exists: " + extractedFile.exists());
            LOG.debug("Overwrite files that exist: " + overwriteFilesThatExist);
            if (extractedFile.exists() && !overwriteFilesThatExist) {
                LOG.debug("Skipping file: " + extractedFile.getName());
                continue;
            }
            extractedFile.getParentFile().mkdirs();
            extractedFile.createNewFile();
            LOG.info("Extracting '" + extractedFile.getName() + "'...");
            InputStream is = zip.getInputStream(zipFileEntry);
            OutputStream os = new FileOutputStream(extractedFile);
            byte[] buf = new byte[4096];
            int r;
            while ((r = is.read(buf)) != -1) {
                os.write(buf, 0, r);
            }
            os.close();
            is.close();
            extractedFile.setExecutable(true);
            if (!extractedFile.canExecute())
                LOG.warn("Unable to set the executable flag for '" + extractedFile.getName() + "'!");
            filesExtracted = true;
        }
        zip.close();

        return filesExtracted;
    }
}

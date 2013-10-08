package com.lazerycode.selenium.download;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static org.apache.commons.io.IOUtils.copy;

public class ExtractFilesFromArchive {

    private static final Logger LOG = Logger.getLogger(ExtractFilesFromArchive.class);

    /**
     * Unzip a downloaded zip file (this will implicitly overwrite any existing files)
     *
     * @param downloadedZip           The downloaded zip file
     * @param extractedToFilePath     Path to extracted file
     * @param overwriteFilesThatExist Overwrite any existing files
     * @return boolean
     * @throws IOException
     */

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean unzipFile(File downloadedZip, String extractedToFilePath, boolean overwriteFilesThatExist, BinaryFileNames possibleFilenames) throws IOException {
        Boolean fileExtracted = false;
        ZipFile zip = new ZipFile(downloadedZip);
        for (int i = 0; i < possibleFilenames.getBinaryFilenames().size(); i++) {
            ZipArchiveEntry zipFileEntry = zip.getEntry(possibleFilenames.getBinaryFilenames().get(i));
            if (zipFileEntry != null) {
                LOG.debug("Found: " + zipFileEntry.getName());
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
                copy(zip.getInputStream(zipFileEntry), new FileOutputStream(extractedFile));
                extractedFile.setExecutable(true);
                if (!extractedFile.canExecute())
                    LOG.warn("Unable to set the executable flag for '" + extractedFile.getName() + "'!");
                fileExtracted = true;
            }
        }
        zip.close();

        return fileExtracted;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean untarFile(File downloadedGZip, String extractedToFilePath, boolean overwriteFilesThatExist, BinaryFileNames possibleFilenames) throws IOException {
        Boolean fileExtracted = false;
        ArrayList<String> filenamesWeAreSearchingFor = possibleFilenames.getBinaryFilenames();
        ArchiveInputStream fileInArchive = new TarArchiveInputStream(new GzipCompressorInputStream((new FileInputStream(downloadedGZip))));
        ArchiveEntry currentFile;
        extractionLoop:
        while ((currentFile = fileInArchive.getNextEntry()) != null) {
            for (String aFilenamesWeAreSearchingFor : filenamesWeAreSearchingFor) {
                if (currentFile.getName().equals(aFilenamesWeAreSearchingFor)) {
                    LOG.debug("Found: " + currentFile.getName());
                    File extractedFile = new File(extractedToFilePath, currentFile.getName());
                    LOG.info("File '" + extractedFile.getName() + "' Exists: " + extractedFile.exists());
                    LOG.debug("Overwrite files that exist: " + overwriteFilesThatExist);
                    if (extractedFile.exists() && !overwriteFilesThatExist) {
                        LOG.debug("Skipping file: " + extractedFile.getName());
                        continue;
                    }
                    extractedFile.getParentFile().mkdirs();
                    extractedFile.createNewFile();
                    LOG.info("Extracting '" + extractedFile.getName() + "'...");
                    copy(fileInArchive, new FileOutputStream(extractedFile));
                    extractedFile.setExecutable(true);
                    if (!extractedFile.canExecute())
                        LOG.warn("Unable to set the executable flag for '" + extractedFile.getName() + "'!");
                    fileExtracted = true;
                    break extractionLoop;
                }
            }
        }
        return fileExtracted;
    }
}

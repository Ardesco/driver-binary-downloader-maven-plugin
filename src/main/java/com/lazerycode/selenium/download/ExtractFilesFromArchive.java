package com.lazerycode.selenium.download;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import static org.apache.commons.io.IOUtils.copy;

public class ExtractFilesFromArchive {

    private static final Logger LOG = Logger.getLogger(ExtractFilesFromArchive.class);

    /**
     * Extract binary from a downloaded archive file
     *
     * @param downloadedZip           The downloaded zip file
     * @param extractedToFilePath     Path to extracted file
     * @param overwriteFilesThatExist Overwrite any existing files
     * @param possibleFilenames       Names of the files we want to extract
     * @return boolean
     * @throws IOException
     */
    public static boolean extractFileFromArchive(File downloadedZip, String extractedToFilePath, boolean overwriteFilesThatExist, BinaryFileNames possibleFilenames) throws IOException, IllegalArgumentException {
        String fileType = FilenameUtils.getExtension(downloadedZip.getAbsolutePath());
        LOG.debug("Determined archive type: " + fileType);
        if(fileType.equals("zip")){
            LOG.debug("Extracting binary from .zip file");
            return unzipFile(downloadedZip, extractedToFilePath, overwriteFilesThatExist, possibleFilenames);
        } else if(fileType.equals("gz")){
            LOG.debug("Extracting binary from .tar.gz file");
            return untarFile(downloadedZip, extractedToFilePath, overwriteFilesThatExist, possibleFilenames);
        }
        throw new IllegalArgumentException("." + fileType + " is an unsupported archive type");
    }

    /**
     * Unzip a downloaded zip file (this will implicitly overwrite any existing files)
     *
     * @param downloadedZip           The downloaded zip file
     * @param extractedToFilePath     Path to extracted file
     * @param overwriteFilesThatExist Overwrite any existing files
     * @param possibleFilenames       Names of the files we want to extract
     * @return boolean
     * @throws IOException
     */

    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected static boolean unzipFile(File downloadedZip, String extractedToFilePath, boolean overwriteFilesThatExist, BinaryFileNames possibleFilenames) throws IOException {
        Boolean fileExtracted = false;
        ZipFile zip = new ZipFile(downloadedZip);

        ArrayList<String> filenamesWeAreSearchingFor = possibleFilenames.getBinaryFilenames();
        Enumeration<ZipArchiveEntry> zipFile = zip.getEntries();
        extractionLoop:
        while (zipFile.hasMoreElements()) {
            ZipArchiveEntry zipFileEntry =  zipFile.nextElement();
            for (String aFilenameWeAreSearchingFor : filenamesWeAreSearchingFor) {
                if (zipFileEntry.getName().endsWith(aFilenameWeAreSearchingFor)) {
                    LOG.debug("Found: " + zipFileEntry.getName());
                    File extractedFile = new File(extractedToFilePath, aFilenameWeAreSearchingFor);
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
                    break extractionLoop;
                }
            }
        }

        return fileExtracted;
    }

    /**
     * Unzip a downloaded tar.gz file (this will implicitly overwrite any existing files)
     *
     * @param downloadedGZip          The downloaded tar.gz file
     * @param extractedToFilePath     Path to extracted file
     * @param overwriteFilesThatExist Overwrite any existing files
     * @param possibleFilenames       Names of the files we want to extract
     * @return boolean
     * @throws IOException
     */

    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected static boolean untarFile(File downloadedGZip, String extractedToFilePath, boolean overwriteFilesThatExist, BinaryFileNames possibleFilenames) throws IOException {
        Boolean fileExtracted = false;
        ArrayList<String> filenamesWeAreSearchingFor = possibleFilenames.getBinaryFilenames();
        ArchiveInputStream fileInArchive = new TarArchiveInputStream(new GzipCompressorInputStream((new FileInputStream(downloadedGZip))));
        ArchiveEntry currentFile;
        extractionLoop:
        while ((currentFile = fileInArchive.getNextEntry()) != null) {
            LOG.debug("Examining " + currentFile.getName());
            for (String aFilenameWeAreSearchingFor : filenamesWeAreSearchingFor) {
                LOG.debug("Searching for " + aFilenameWeAreSearchingFor +"...");
                if (currentFile.getName().endsWith(aFilenameWeAreSearchingFor)) {
                    LOG.debug("Found: " + currentFile.getName());
                    File extractedFile = new File(extractedToFilePath, aFilenameWeAreSearchingFor);
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

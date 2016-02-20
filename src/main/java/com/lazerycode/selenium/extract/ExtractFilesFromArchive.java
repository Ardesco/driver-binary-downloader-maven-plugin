package com.lazerycode.selenium.extract;

import com.lazerycode.selenium.repository.BinaryType;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoFailureException;

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
     * @param downloadedCompressedFile The downloaded compressed file
     * @param extractedToFilePath      Path to extracted file
     * @param overwriteFilesThatExist  Overwrite any existing files
     * @param possibleFilenames        Names of the files we want to extract
     *
     * @return boolean
     *
     * @throws IOException Unable to write to filesystem
     * @throws IllegalArgumentException Unsupported archive
     * @throws MojoFailureException Error running plugin
     */
    public static String extractFileFromArchive(File downloadedCompressedFile, String extractedToFilePath, boolean overwriteFilesThatExist, BinaryType possibleFilenames) throws IOException, IllegalArgumentException, MojoFailureException {
        String fileType = FilenameUtils.getExtension(downloadedCompressedFile.getAbsolutePath());
        LOG.debug("Determined archive type: " + fileType);
        LOG.debug("Overwrite files that exist: " + overwriteFilesThatExist);

        if (!overwriteFilesThatExist) {
            File[] existingFiles = new File(extractedToFilePath).listFiles();
            if (null != existingFiles && existingFiles.length > 0) {
                for (File existingFile : existingFiles) {
                    String existingFileName = existingFile.getName();
                    if (possibleFilenames.getBinaryFilenames().contains(existingFileName)) {
                        LOG.info("Binary '" + existingFileName + "' Exists: true");
                        return existingFile.getAbsolutePath();
                    }
                }
            }
        }

        if (fileType.equals("zip")) {
            return unzipFile(downloadedCompressedFile, extractedToFilePath, possibleFilenames);
        } else if (fileType.equals("gz") || fileType.equals("bz2")) {
            return untarFile(downloadedCompressedFile, extractedToFilePath, possibleFilenames);
        }
        throw new IllegalArgumentException("." + fileType + " is an unsupported archive type");
    }

    /**
     * Unzip a downloaded zip file (this will implicitly overwrite any existing files)
     *
     * @param downloadedCompressedFile The downloaded zip file
     * @param extractedToFilePath      Path to extracted file
     * @param possibleFilenames        Names of the files we want to extract
     * @return boolean
     * @throws IOException
     */

    static String unzipFile(File downloadedCompressedFile, String extractedToFilePath, BinaryType possibleFilenames) throws IOException {
        String fileExtractedAbsolutePath = null;
        LOG.debug("Extracting binary from .zip file");
        ZipFile zip = new ZipFile(downloadedCompressedFile);
        ArrayList<String> filenamesWeAreSearchingFor = possibleFilenames.getBinaryFilenames();
        Enumeration<ZipArchiveEntry> zipFile = zip.getEntries();
        extractionLoop:
        while (zipFile.hasMoreElements()) {
            ZipArchiveEntry zipFileEntry = zipFile.nextElement();
            for (String aFilenameWeAreSearchingFor : filenamesWeAreSearchingFor) {
                if (zipFileEntry.getName().endsWith(aFilenameWeAreSearchingFor)) {
                    LOG.debug("Found: " + zipFileEntry.getName());
                    File extractedFile = new File(extractedToFilePath, aFilenameWeAreSearchingFor);
                    boolean doesfileAlreadyExist = extractedFile.exists();
                    LOG.info("Binary '" + extractedFile.getName() + "' Exists: " + extractedFile.exists());
                    if (!doesfileAlreadyExist && !extractedFile.getParentFile().mkdirs() && !extractedFile.createNewFile()) {
                        throw new IOException("Unable to create " + extractedFile.getAbsolutePath());
                    }
                    LOG.info("Extracting binary '" + extractedFile.getName() + "'...");
                    copy(zip.getInputStream(zipFileEntry), new FileOutputStream(extractedFile));
                    if (!extractedFile.setExecutable(true) && !extractedFile.canExecute())
                        LOG.warn("Unable to set the executable flag for '" + extractedFile.getName() + "'!");
                    fileExtractedAbsolutePath = extractedFile.getAbsolutePath();
                    LOG.info("File(s) copied to " + fileExtractedAbsolutePath);
                    break extractionLoop;
                }
            }
        }
        zip.close();

        return fileExtractedAbsolutePath;
    }

    /**
     * Unzip a downloaded tar.gz/tar.bz2 file (this will implicitly overwrite any existing files)
     *
     * @param downloadedCompressedFile The downloaded tar.gz/tar.bz2 file
     * @param extractedToFilePath      Path to extracted file
     * @param possibleFilenames        Names of the files we want to extract
     * @return boolean
     * @throws IOException MojoFailureException
     */

    static String untarFile(File downloadedCompressedFile, String extractedToFilePath, BinaryType possibleFilenames) throws IOException, MojoFailureException {
        String fileExtractedAbsolutePath = null;
        ArrayList<String> filenamesWeAreSearchingFor = possibleFilenames.getBinaryFilenames();
        ArchiveInputStream fileInArchive;
        String fileType = FilenameUtils.getExtension(downloadedCompressedFile.getAbsolutePath());
        if (fileType.equals("gz")) {
            LOG.debug("Extracting binary from .tar.gz file");
            fileInArchive = new TarArchiveInputStream(new GzipCompressorInputStream((new FileInputStream(downloadedCompressedFile))));
        } else if (fileType.equals("bz2")) {
            LOG.debug("Extracting binary from .tar.bz2 file");
            fileInArchive = new TarArchiveInputStream(new BZip2CompressorInputStream((new FileInputStream(downloadedCompressedFile))));
        } else {
            throw new MojoFailureException("Unrecognised zip format!");
        }
        ArchiveEntry currentFile;
        extractionLoop:
        while ((currentFile = fileInArchive.getNextEntry()) != null) {
            LOG.debug("Examining " + currentFile.getName());
            for (String aFilenameWeAreSearchingFor : filenamesWeAreSearchingFor) {
                LOG.debug("Searching for " + aFilenameWeAreSearchingFor + "...");
                if (currentFile.getName().endsWith(aFilenameWeAreSearchingFor)) {
                    LOG.debug("Found: " + currentFile.getName());
                    File extractedFile = new File(extractedToFilePath, aFilenameWeAreSearchingFor);
                    boolean doesfileAlreadyExist = extractedFile.exists();
                    LOG.info("Binary '" + extractedFile.getName() + "' Exists: " + extractedFile.exists());
                    if (!doesfileAlreadyExist && !extractedFile.getParentFile().mkdirs() && !extractedFile.createNewFile()) {
                        throw new IOException("Unable to create " + extractedFile.getAbsolutePath());
                    }
                    LOG.info("Extracting binary '" + extractedFile.getName() + "'...");
                    FileOutputStream extractedFileStream = new FileOutputStream(extractedFile);
                    copy(fileInArchive, extractedFileStream);
                    extractedFileStream.close();
                    if (!extractedFile.setExecutable(true) && !extractedFile.canExecute())
                        LOG.warn("Unable to set the executable flag for '" + extractedFile.getName() + "'!");
                    fileExtractedAbsolutePath = extractedFile.getAbsolutePath();
                    LOG.info("File(s) copied to " + fileExtractedAbsolutePath);
                    break extractionLoop;
                }
            }
        }
        fileInArchive.close();

        return fileExtractedAbsolutePath;
    }
}

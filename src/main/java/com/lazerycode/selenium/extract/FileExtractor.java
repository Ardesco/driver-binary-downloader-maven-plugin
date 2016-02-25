package com.lazerycode.selenium.extract;

import com.lazerycode.selenium.exceptions.ExpectedFileNotFoundException;
import com.lazerycode.selenium.repository.BinaryType;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;

public class FileExtractor {

    private static final Logger LOG = Logger.getLogger(FileExtractor.class);
    private final boolean overwriteFilesThatExist;

    /**
     * @param overwriteFilesThatExist Overwrite any existing files
     */
    public FileExtractor(boolean overwriteFilesThatExist) {
        this.overwriteFilesThatExist = overwriteFilesThatExist;
    }

    /**
     * Extract binary from a downloaded archive file
     *
     * @param downloadedCompressedFile The downloaded compressed file
     * @param extractedToFilePath      Path to extracted file
     * @param possibleFilenames        Names of the files we want to extract
     * @return boolean
     * @throws IOException              Unable to write to filesystem
     * @throws IllegalArgumentException Unsupported archive
     * @throws MojoFailureException     Error running plugin
     */
    public String extractFileFromArchive(File downloadedCompressedFile, String extractedToFilePath, BinaryType possibleFilenames) throws IOException, IllegalArgumentException, MojoFailureException {
        ArchiveType fileType = ArchiveType.valueOf(FilenameUtils.getExtension(downloadedCompressedFile.getName()).toUpperCase());
        LOG.debug("Determined archive type: " + fileType);
        LOG.debug("Overwrite files that exist: " + overwriteFilesThatExist);

        switch (fileType) {
            case GZ:
            case BZ2:
                CompressedFile compressedFile = new CompressedFile(downloadedCompressedFile);
                switch (compressedFile.getArchiveType()) {
                    case TAR:
                        return untarFile(compressedFile.getInputStream(), extractedToFilePath, possibleFilenames);
                    default:
                        return copyFileToDisk(compressedFile.getInputStream(), extractedToFilePath, compressedFile.getDecompressedFilename());
                }
            case ZIP:
                return unzipFile(downloadedCompressedFile, extractedToFilePath, possibleFilenames);
            default:
                throw new IllegalArgumentException("." + fileType + " is an unsupported archive type");
        }
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

    protected String unzipFile(File downloadedCompressedFile, String extractedToFilePath, BinaryType possibleFilenames) throws IOException, ExpectedFileNotFoundException {
        LOG.debug("Attempting to extract binary from .zip file...");
        ArrayList<String> filenamesWeAreSearchingFor = possibleFilenames.getBinaryFilenames();
        ZipFile zip = new ZipFile(downloadedCompressedFile);
        try {
            Enumeration<ZipArchiveEntry> zipFile = zip.getEntries();
            while (zipFile.hasMoreElements()) {
                ZipArchiveEntry zipFileEntry = zipFile.nextElement();
                for (String aFilenameWeAreSearchingFor : filenamesWeAreSearchingFor) {
                    if (zipFileEntry.getName().endsWith(aFilenameWeAreSearchingFor)) {
                        LOG.debug("Found: " + zipFileEntry.getName());
                        return copyFileToDisk(zip.getInputStream(zipFileEntry), extractedToFilePath, aFilenameWeAreSearchingFor);
                    }
                }
            }
        } finally {
            zip.close();
        }

        throw new ExpectedFileNotFoundException("Unable to find any expected files for " + possibleFilenames.getBinaryTypeAsString());
    }

    /**
     * Untar a decompressed tar file (this will implicitly overwrite any existing files)
     *
     * @param compressedFileInputStream The expanded tar file
     * @param extractedToFilePath       Path to extracted file
     * @param possibleFilenames         Names of the files we want to extract
     * @return boolean
     * @throws IOException MojoFailureException
     */

    protected String untarFile(InputStream compressedFileInputStream, String extractedToFilePath, BinaryType possibleFilenames) throws IOException, ExpectedFileNotFoundException {
        LOG.debug("Attempting to extract binary from a .tar file...");
        ArchiveEntry currentFile;
        ArrayList<String> filenamesWeAreSearchingFor = possibleFilenames.getBinaryFilenames();
        ArchiveInputStream archiveInputStream = new TarArchiveInputStream(compressedFileInputStream);
        try {
            while ((currentFile = archiveInputStream.getNextEntry()) != null) {
                for (String aFilenameWeAreSearchingFor : filenamesWeAreSearchingFor) {
                    if (currentFile.getName().endsWith(aFilenameWeAreSearchingFor)) {
                        LOG.debug("Found: " + currentFile.getName());
                        return copyFileToDisk(archiveInputStream, extractedToFilePath, aFilenameWeAreSearchingFor);
                    }
                }
            }
        } finally {
            compressedFileInputStream.close();
        }

        throw new ExpectedFileNotFoundException("Unable to find any expected filed for " + possibleFilenames.getBinaryTypeAsString());
    }

    /**
     * Copy a file from an inputsteam to disk
     *
     * @param inputStream     A valid iput stream to read
     * @param pathToExtractTo Path of the file we want to create
     * @param filename        Filename of the file we want to create
     * @return Absolute path of the newly created file (Or existing file if overwriteFilesThatExist is set to false)
     * @throws IOException
     */
    protected String copyFileToDisk(InputStream inputStream, String pathToExtractTo, String filename) throws IOException {
        if (!overwriteFilesThatExist) {
            File[] existingFiles = new File(pathToExtractTo).listFiles();
            if (null != existingFiles && existingFiles.length > 0) {
                for (File existingFile : existingFiles) {
                    String existingFilename = existingFile.getName();
                    if (existingFilename.equals(filename)) {
                        LOG.info("Binary '" + existingFilename + "' Exists: true");
                        LOG.info("Using existing '" + existingFilename + "'binary.");
                        return existingFile.getAbsolutePath();
                    }
                }
            }
        }

        File outputFile = new File(pathToExtractTo, filename);
        try {
            if (!outputFile.exists() && !outputFile.getParentFile().mkdirs() && !outputFile.createNewFile()) {
                throw new IOException("Unable to create " + outputFile.getAbsolutePath());
            }
            LOG.info("Extracting binary '" + filename + "'...");
            FileUtils.copyInputStreamToFile(inputStream, outputFile);
            LOG.info("Binary copied to " + outputFile.getAbsolutePath());
            if (!outputFile.setExecutable(true) && !outputFile.canExecute()) {
                LOG.warn("Unable to set executable flag (+x) on " + filename);
            }
        } finally {
            inputStream.close();
        }

        return outputFile.getAbsolutePath();
    }
}

package com.lazerycode.selenium.extract;

import com.lazerycode.selenium.exceptions.InvalidFileTypeException;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.lazerycode.selenium.extract.ArchiveType.BZ2;
import static com.lazerycode.selenium.extract.ArchiveType.GZ;

public class CompressedFile {

    private static final Logger LOG = Logger.getLogger(FileExtractor.class);
    File compressedFile;
    String decompressedFilename;
    ArchiveType filetype = null;

    /**
     * A class that will take a compressed file.
     *
     * @param compressedFile A compressed file of type .gz or .bz2
     * @throws InvalidFileTypeException
     */
    public CompressedFile(File compressedFile) throws InvalidFileTypeException {
        filetype = ArchiveType.valueOf(FilenameUtils.getExtension(compressedFile.getName()).toUpperCase());
        if (filetype != GZ && filetype != BZ2) {
            throw new InvalidFileTypeException(compressedFile.getName() + " is an archive, not a known compressed file type");
        }
        this.compressedFile = compressedFile;
        decompressedFilename = FilenameUtils.getBaseName(compressedFile.getName());
    }

    /**
     * Get the filename of the decompressed file.
     *
     * @return The decompressed filename as a String.
     */
    public String getDecompressedFilename() {
        return decompressedFilename;
    }

    /**
     * Get an InputStream for the decompressed file.
     *
     * @return An InputStream, the type could be BZip2CompressorInputStream, or GzipCompressorInputStream depending on what type of file was initially supplied
     * @throws IOException
     */
    public InputStream getInputStream() throws IOException {
        switch (filetype) {
            case GZ:
                LOG.debug("Decompressing .gz file");
                return new GzipCompressorInputStream(new FileInputStream(compressedFile));
            case BZ2:
                LOG.debug("Decompressing .bz2 file");
                return new BZip2CompressorInputStream(new FileInputStream(compressedFile));
        }
        return null;
    }

    /**
     * Find out if the uncompressed file is an archive
     *
     * @return ArchiveType.TAR if a tar, or null if not an archive
     */
    public ArchiveType getArchiveType() {
        try {
            return ArchiveType.valueOf(FilenameUtils.getExtension(decompressedFilename).toUpperCase());
        } catch (IllegalArgumentException e) {
            LOG.debug("Not a recognised Archive type");
            return null;
        }
    }
}
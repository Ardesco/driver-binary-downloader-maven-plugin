package com.lazerycode.selenium.extract;

import com.lazerycode.selenium.hash.CheckFileHash;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static com.lazerycode.selenium.hash.HashType.MD5;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class ExtractFilesFromArchiveTest {

    private final String validHash = "add36bb347a987b56e533c2034fd37b1";
    private final URL test7ZipFile = this.getClass().getResource("/jetty/files/download.7z");
    private final URL testZipFile = this.getClass().getResource("/jetty/files/download.zip");
    private final URL testTarGZFile = this.getClass().getResource("/jetty/files/download.tar.gz");
    private final URL testTarBZ2File = this.getClass().getResource("/jetty/files/download.tar.bz2");
    private final String tempDir = System.getProperty("java.io.tmpdir");

    @Test
    public void successfullyExtractFileFromZipArchive() throws Exception {
        CheckFileHash fileToCheck = new CheckFileHash();
        ExtractFilesFromArchive.unzipFile(new File(testZipFile.getFile()), tempDir, true, BinaryFileNames.PHANTOMJS);
        fileToCheck.fileToCheck(new File(tempDir + File.separator + "phantomjs"));
        fileToCheck.hashDetails(validHash, MD5);
        assertThat(fileToCheck.hasAValidHash(), is(equalTo(true)));
    }

    @Test
    public void successfullyExtractFileFromTarGZipArchive() throws Exception {
        CheckFileHash fileToCheck = new CheckFileHash();
        ExtractFilesFromArchive.untarFile(new File(testTarGZFile.getFile()), tempDir, true, BinaryFileNames.PHANTOMJS, "gz");
        fileToCheck.fileToCheck(new File(tempDir + File.separator + "phantomjs"));
        fileToCheck.hashDetails(validHash, MD5);
        assertThat(fileToCheck.hasAValidHash(), is(equalTo(true)));
    }

    @Test
    public void successfullyExtractFileFromTarBZip2Archive() throws Exception {
        CheckFileHash fileToCheck = new CheckFileHash();
        ExtractFilesFromArchive.untarFile(new File(testTarBZ2File.getFile()), tempDir, true, BinaryFileNames.PHANTOMJS, "bz2");
        fileToCheck.fileToCheck(new File(tempDir + File.separator + "phantomjs"));
        fileToCheck.hashDetails(validHash, MD5);
        assertThat(fileToCheck.hasAValidHash(), is(equalTo(true)));
    }

    @Test
    public void successfullyWorkOutArchiveTypeAndExtractFileFromZipArchive() throws Exception {
        CheckFileHash fileToCheck = new CheckFileHash();
        ExtractFilesFromArchive.extractFileFromArchive(new File(testZipFile.getFile()), tempDir, true, BinaryFileNames.PHANTOMJS);
        fileToCheck.fileToCheck(new File(tempDir + File.separator + "phantomjs"));
        fileToCheck.hashDetails(validHash, MD5);
        assertThat(fileToCheck.hasAValidHash(), is(equalTo(true)));
    }

    @Test
    public void successfullyWorkOutArchiveTypeAndExtractFileFromTarGZipArchive() throws Exception {
        CheckFileHash fileToCheck = new CheckFileHash();
        ExtractFilesFromArchive.extractFileFromArchive(new File(testTarGZFile.getFile()), tempDir, true, BinaryFileNames.PHANTOMJS);
        fileToCheck.fileToCheck(new File(tempDir + File.separator + "phantomjs"));
        fileToCheck.hashDetails(validHash, MD5);
        assertThat(fileToCheck.hasAValidHash(), is(equalTo(true)));
    }

    @Test
    public void successfullyWorkOutArchiveTypeAndExtractFileFromTarBZip2Archive() throws Exception {
        CheckFileHash fileToCheck = new CheckFileHash();
        ExtractFilesFromArchive.extractFileFromArchive(new File(testTarBZ2File.getFile()), tempDir, true, BinaryFileNames.PHANTOMJS);
        fileToCheck.fileToCheck(new File(tempDir + File.separator + "phantomjs"));
        fileToCheck.hashDetails(validHash, MD5);
        assertThat(fileToCheck.hasAValidHash(), is(equalTo(true)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryAndExtractFromAnUnsupportedArchive() throws Exception {
        ExtractFilesFromArchive.extractFileFromArchive(new File(test7ZipFile.getFile()), tempDir, true, BinaryFileNames.PHANTOMJS);
    }
}

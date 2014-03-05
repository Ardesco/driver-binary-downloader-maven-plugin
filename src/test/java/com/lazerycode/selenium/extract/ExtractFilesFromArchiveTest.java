package com.lazerycode.selenium.extract;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

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
    private static File phantomJSTestFile;

    @Before
    public void initialiseFile() {
        phantomJSTestFile = new File(tempDir + File.separator + "phantomjs");
    }

    @After
    public void cleanUp() {
        if (phantomJSTestFile.exists()) {
            assertThat(phantomJSTestFile.delete(), is(equalTo(true)));
        }
    }

    @Test
    public void successfullyExtractFileFromZipArchive() throws Exception {
        ExtractFilesFromArchive.unzipFile(new File(testZipFile.getFile()), tempDir, true, BinaryFileNames.PHANTOMJS);
        FileInputStream fileToCheck = new FileInputStream(phantomJSTestFile);

        assertThat(DigestUtils.md5Hex(fileToCheck), is(equalTo(validHash)));

        fileToCheck.close();
    }

    @Test
    public void successfullyExtractFileFromTarGZipArchive() throws Exception {
        ExtractFilesFromArchive.untarFile(new File(testTarGZFile.getFile()), tempDir, true, BinaryFileNames.PHANTOMJS, "gz");
        FileInputStream fileToCheck = new FileInputStream(phantomJSTestFile);

        assertThat(DigestUtils.md5Hex(fileToCheck), is(equalTo(validHash)));

        fileToCheck.close();
    }

    @Test
    public void successfullyExtractFileFromTarBZip2Archive() throws Exception {
        ExtractFilesFromArchive.untarFile(new File(testTarBZ2File.getFile()), tempDir, true, BinaryFileNames.PHANTOMJS, "bz2");
        FileInputStream fileToCheck = new FileInputStream(phantomJSTestFile);

        assertThat(DigestUtils.md5Hex(fileToCheck), is(equalTo(validHash)));

        fileToCheck.close();
    }

    @Test
    public void successfullyWorkOutArchiveTypeAndExtractFileFromZipArchive() throws Exception {
        ExtractFilesFromArchive.extractFileFromArchive(new File(testZipFile.getFile()), tempDir, true, BinaryFileNames.PHANTOMJS);
        FileInputStream fileToCheck = new FileInputStream(phantomJSTestFile);

        assertThat(DigestUtils.md5Hex(fileToCheck), is(equalTo(validHash)));

        fileToCheck.close();
    }

    @Test
    public void successfullyWorkOutArchiveTypeAndExtractFileFromTarGZipArchive() throws Exception {
        ExtractFilesFromArchive.extractFileFromArchive(new File(testTarGZFile.getFile()), tempDir, true, BinaryFileNames.PHANTOMJS);
        FileInputStream fileToCheck = new FileInputStream(phantomJSTestFile);

        assertThat(DigestUtils.md5Hex(fileToCheck), is(equalTo(validHash)));

        fileToCheck.close();
    }

    @Test
    public void successfullyWorkOutArchiveTypeAndExtractFileFromTarBZip2Archive() throws Exception {
        ExtractFilesFromArchive.extractFileFromArchive(new File(testTarBZ2File.getFile()), tempDir, true, BinaryFileNames.PHANTOMJS);
        FileInputStream fileToCheck = new FileInputStream(phantomJSTestFile);

        assertThat(DigestUtils.md5Hex(fileToCheck), is(equalTo(validHash)));

        fileToCheck.close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryAndExtractFromAnUnsupportedArchive() throws Exception {
        ExtractFilesFromArchive.extractFileFromArchive(new File(test7ZipFile.getFile()), tempDir, true, BinaryFileNames.PHANTOMJS);
    }
}

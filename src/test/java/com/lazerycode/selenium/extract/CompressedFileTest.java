package com.lazerycode.selenium.extract;

import com.lazerycode.selenium.exceptions.InvalidFileTypeException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static com.lazerycode.selenium.extract.DownloadableFileType.TAR;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class CompressedFileTest {

    @Test(expected = IllegalArgumentException.class)
    public void passingInAFileThatDoesNotHaveAFileNameEndingInGZOrBZ2ThrowsError() throws Exception {
        File somefile = File.createTempFile("somefile", ".tmp");
        CompressedFile compressedFile = new CompressedFile(somefile);
    }

    @Test(expected = InvalidFileTypeException.class)
    public void passingInAFileThatDoesNotHaveAFileNameEndingInGZOrBZ2ButIsAKnownArchiveTypeThrowsError() throws Exception {
        File somefile = File.createTempFile("somefile", ".zip");
        CompressedFile compressedFile = new CompressedFile(somefile);
    }

    @Test(expected = IOException.class)
    public void passingInAFileThatIsNotAGZFileThrowsErrorWhenYouTryToGetAnInputStream() throws Exception {
        File somefile = File.createTempFile("somefile", ".tmp.gz");
        CompressedFile compressedFile = new CompressedFile(somefile);
        compressedFile.getInputStream();
    }

    @Test
    public void willReturnAnUncompressedArchiveFileNameForAGZFile() throws Exception {
        File testFile = new File(String.valueOf(this.getClass().getResource("/jetty/files/download.tar.gz").getFile()));
        CompressedFile compressedFile = new CompressedFile(testFile);

        assertThat(compressedFile.getDecompressedFilename(), is(equalTo("download.tar")));
    }

    @Test
    public void willReturnAnUncompressedFileNameForAGZFile() throws Exception {
        File testFile = new File(String.valueOf(this.getClass().getResource("/jetty/files/test.gz").getFile()));
        CompressedFile compressedFile = new CompressedFile(testFile);

        assertThat(compressedFile.getDecompressedFilename(), is(equalTo("test")));
    }

    @Test
    public void willReturnANullForArchiveTypeForAGZFile() throws Exception {
        File testFile = new File(String.valueOf(this.getClass().getResource("/jetty/files/test.gz").getFile()));
        CompressedFile compressedFile = new CompressedFile(testFile);

        assertThat(compressedFile.getArchiveType(), is(equalTo(null)));
    }

    @Test
    public void willReturnAnArchiveTypeOfTARForAGZFile() throws Exception {
        File testFile = new File(String.valueOf(this.getClass().getResource("/jetty/files/download.tar.gz").getFile()));
        CompressedFile compressedFile = new CompressedFile(testFile);

        assertThat(compressedFile.getArchiveType(), is(equalTo(TAR)));
    }

    @Test(expected = IOException.class)
    public void passingInAFileThatIsNotABZ2FileThrowsErrorWhenYouTryToGetAnInputStream() throws Exception {
        File somefile = File.createTempFile("somefile", ".tmp.bz2");
        CompressedFile compressedFile = new CompressedFile(somefile);
        compressedFile.getInputStream();
    }

    @Test
    public void willReturnAnUncompressedArchiveFileNameForABZ2File() throws Exception {
        File testFile = new File(String.valueOf(this.getClass().getResource("/jetty/files/download.tar.bz2").getFile()));
        CompressedFile compressedFile = new CompressedFile(testFile);

        assertThat(compressedFile.getDecompressedFilename(), is(equalTo("download.tar")));
    }

    @Test
    public void willReturnAnUncompressedFileNameForABZ2File() throws Exception {
        File testFile = new File(String.valueOf(this.getClass().getResource("/jetty/files/test.bz2").getFile()));
        CompressedFile compressedFile = new CompressedFile(testFile);

        assertThat(compressedFile.getDecompressedFilename(), is(equalTo("test")));
    }

    @Test
    public void willReturnANullForArchiveTypeForABZ2File() throws Exception {
        File testFile = new File(String.valueOf(this.getClass().getResource("/jetty/files/test.bz2").getFile()));
        CompressedFile compressedFile = new CompressedFile(testFile);

        assertThat(compressedFile.getArchiveType(), is(equalTo(null)));
    }

    @Test
    public void willReturnAnArchiveTypeOfTARForABZ2File() throws Exception {
        File testFile = new File(String.valueOf(this.getClass().getResource("/jetty/files/download.tar.bz2").getFile()));
        CompressedFile compressedFile = new CompressedFile(testFile);

        assertThat(compressedFile.getArchiveType(), is(equalTo(TAR)));
    }
}

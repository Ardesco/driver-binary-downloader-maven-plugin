package com.lazerycode.selenium.repository;

import org.apache.maven.plugin.MojoFailureException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class RepositoryParserTest {

    private final URL repositoryMap = this.getClass().getResource("/TestRepoMap.xml");
    private final URL repositoryMapWithNoHashs = this.getClass().getResource("/noHash.xml");
    private final URL invalidRepositoryMap = this.getClass().getResource("/InvalidTestRepoMap.xml");
    private static final ArrayList<OperatingSystem> OS_TYPE_LIST = new ArrayList<OperatingSystem>();

    @BeforeClass
    public static void populateOSList() {
        OS_TYPE_LIST.add(OperatingSystem.LINUX);
        OS_TYPE_LIST.add(OperatingSystem.WINDOWS);
        OS_TYPE_LIST.add(OperatingSystem.OSX);
    }

    @Test
    public void getLatestVersions() throws Exception {
        RepositoryParser executableBinaryMapping = new RepositoryParser(this.repositoryMap.openStream(), OS_TYPE_LIST, true, true, true, false);
        HashMap<String, FileDetails> downloadableFileList = executableBinaryMapping.getFilesToDownload();

        assertThat(downloadableFileList.size(), is(equalTo(8)));
    }

    @Test
    public void getLatestVersionsWithNoHash() throws Exception {
        RepositoryParser executableBinaryMapping = new RepositoryParser(this.repositoryMapWithNoHashs.openStream(), OS_TYPE_LIST, true, true, true, false);
        HashMap<String, FileDetails> downloadableFileList = executableBinaryMapping.getFilesToDownload();

        assertThat(downloadableFileList.size(), is(equalTo(6)));
    }

    @Test
    public void getSpecificVersions() throws Exception {
        Map<String, String> versionsToFind = new HashMap<String, String>();
        versionsToFind.put("googlechrome", "21");

        RepositoryParser executableBinaryMapping = new RepositoryParser(this.repositoryMap.openStream(), OS_TYPE_LIST, true, true, true, false);
        executableBinaryMapping.specifySpecificExecutableVersions(versionsToFind);
        HashMap<String, FileDetails> downloadableFileList = executableBinaryMapping.getFilesToDownload();

        assertThat(downloadableFileList.size(), is(equalTo(6)));
        assertThat(downloadableFileList.get("windows/googlechrome/32bit/21").getHash(), is(equalTo("f2c25d144dc9d71473700706179b8aa989baec32")));
        assertThat(downloadableFileList.get("windows/googlechrome/64bit/21").getHash(), is(equalTo("f2c25d144dc9d71473700706179b8aa989baec32")));
        assertThat(downloadableFileList.get("linux/googlechrome/32bit/21").getHash(), is(equalTo("384a3d0033a688db7f41d0037889367b078cd969")));
        assertThat(downloadableFileList.get("linux/googlechrome/64bit/21").getHash(), is(equalTo("4f8f043f3893ca0969176c8cf4868117b47e3781")));
        assertThat(downloadableFileList.get("osx/googlechrome/32bit/21").getHash(), is(equalTo("ea6f2f45c835d3413fde3a7b08e5e3e4db6dc3f9")));
        assertThat(downloadableFileList.get("osx/googlechrome/64bit/21").getHash(), is(equalTo("ea6f2f45c835d3413fde3a7b08e5e3e4db6dc3f9")));
    }

    @Test
    public void getAllVersions() throws Exception {
        RepositoryParser executableBinaryMapping = new RepositoryParser(this.repositoryMap.openStream(), OS_TYPE_LIST, true, true, false, false);
        HashMap<String, FileDetails> downloadableFileList = executableBinaryMapping.getFilesToDownload();

        assertThat(downloadableFileList.size(), is(equalTo(16)));
    }

    @Test(expected = MojoFailureException.class)
    public void throwErrorOnInvalidVersion() throws Exception {
        Map<String, String> versionsToFind = new HashMap<String, String>();
        versionsToFind.put("googlechrome", "one");

        RepositoryParser executableBinaryMapping = new RepositoryParser(this.repositoryMap.openStream(), OS_TYPE_LIST, true, true, true, true);
        executableBinaryMapping.specifySpecificExecutableVersions(versionsToFind);
        executableBinaryMapping.getFilesToDownload();
    }

    @Test
    public void ignoreInvalidVersion() throws Exception {
        Map<String, String> versionsToFind = new HashMap<String, String>();
        versionsToFind.put("googlechrome", "one");

        RepositoryParser executableBinaryMapping = new RepositoryParser(this.repositoryMap.openStream(), OS_TYPE_LIST, true, true, true, false);
        executableBinaryMapping.specifySpecificExecutableVersions(versionsToFind);
        HashMap<String, FileDetails> downloadableFileList = executableBinaryMapping.getFilesToDownload();

        assertThat(downloadableFileList.size(), is(equalTo(0)));
    }

    @Test(expected = MojoFailureException.class)
    public void throwExceptionIfRepositoryMapIsInvalid() throws Exception {
        new RepositoryParser(this.invalidRepositoryMap.openStream(), OS_TYPE_LIST, true, true, true, false);
    }
}


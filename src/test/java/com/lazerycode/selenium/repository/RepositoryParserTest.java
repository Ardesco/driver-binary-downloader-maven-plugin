package com.lazerycode.selenium.repository;

import com.lazerycode.selenium.OS;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class RepositoryParserTest {

    private final URL repositoryMap = this.getClass().getResource("/TestRepoMap.xml");
    private static ArrayList<OS> osList = new ArrayList<OS>();

    @BeforeClass
    public static void populateOSList() {
        osList.add(OS.LINUX);
        osList.add(OS.WINDOWS);
        osList.add(OS.OSX);
    }

    @Test
    public void getLatestVersions() throws Exception {
        RepositoryParser executableBinaryMapping = new RepositoryParser(this.repositoryMap.openStream(), this.osList, true, true, true);
        HashMap<String, FileDetails> downloadableFileList = executableBinaryMapping.getFilesToDownload();

        assertThat(downloadableFileList.size(), is(equalTo(8)));
        //TODO check all the keys are correct
    }

    @Test
    public void getSpecificVersions() throws Exception {
        Map<String, String> versionsToFind = new HashMap<String, String>();
        versionsToFind.put("googlechrome", "18");

        RepositoryParser executableBinaryMapping = new RepositoryParser(this.repositoryMap.openStream(), this.osList, true, true, true);
        executableBinaryMapping.specifySpecificExecutableVersions(versionsToFind);
        HashMap<String, FileDetails> downloadableFileList = executableBinaryMapping.getFilesToDownload();

        assertThat(downloadableFileList.size(), is(equalTo(6)));
        //TODO check all the keys are correct
    }

    @Test
    public void getAllVersions() throws Exception {
        RepositoryParser executableBinaryMapping = new RepositoryParser(this.repositoryMap.openStream(), this.osList, true, true, false);
        HashMap<String, FileDetails> downloadableFileList = executableBinaryMapping.getFilesToDownload();

        assertThat(downloadableFileList.size(), is(equalTo(16)));
        //TODO check all the keys are correct
    }

    //TODO implement error on invalid version
    @Ignore
    @Test(expected = MojoFailureException.class)
    public void throwErrorOnInvalidVersion() throws Exception {
        Map<String, String> versionsToFind = new HashMap<String, String>();
        versionsToFind.put("googlechrome", "one");

        RepositoryParser executableBinaryMapping = new RepositoryParser(this.repositoryMap.openStream(), this.osList, true, true, true);
        executableBinaryMapping.specifySpecificExecutableVersions(versionsToFind);
        HashMap<String, FileDetails> downloadableFileList = executableBinaryMapping.getFilesToDownload();
    }

    @Test
    public void ignoreInvalidVersion() throws Exception {
        Map<String, String> versionsToFind = new HashMap<String, String>();
        versionsToFind.put("googlechrome", "one");

        RepositoryParser executableBinaryMapping = new RepositoryParser(this.repositoryMap.openStream(), this.osList, true, true, true);
        executableBinaryMapping.specifySpecificExecutableVersions(versionsToFind);
        HashMap<String, FileDetails> downloadableFileList = executableBinaryMapping.getFilesToDownload();

        assertThat(downloadableFileList.size(), is(equalTo(0)));
    }
}


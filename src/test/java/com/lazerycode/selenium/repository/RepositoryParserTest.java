package com.lazerycode.selenium.repository;

import com.lazerycode.selenium.OS;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.BeforeClass;
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

    private final URL repositoryMap = this.getClass().getResource("/RepositoryMap.xml");
    private static ArrayList<OS> osList = new ArrayList<OS>();

    @BeforeClass
    public static void populateOSList(){
        osList.add(OS.LINUX);
        osList.add(OS.WINDOWS);
        osList.add(OS.OSX);
    }

    @Test
    public void getLatestVersions() throws Exception{
        RepositoryParser executableBinaryMapping = new RepositoryParser(
                new File(this.repositoryMap.toURI()),
                this.osList,
                true,
                true,
                true);
        HashMap<String, FileDetails> downloadableFileList = executableBinaryMapping.getFilesToDownload();

        System.out.println("foo");
//        assertThat(versionsFound.get("internetexplorer"), is(equalTo("2.21.0")));
//        assertThat(versionsFound.get("googlechrome"), is(equalTo("19")));
    }
//
//    @Test
//    public void getSpecificVersions() throws Exception{
//        Map<String, String> versionsToFind = new HashMap<String, String>();
//        versionsToFind.put("googlechrome", "18");
//        RepositoryHandler versions = new RepositoryHandler(versionsToFind, false, new File(repositoryMap.toURI()), false);
//        Map<String, String> versionsFound = versions.parseRequiredBrowserAndVersion();
//
//        assertThat(versionsFound.size(), is(equalTo(1)));
//        assertThat(versionsFound.get("googlechrome"), is(equalTo("18")));
//    }
//
//    @Test(expected = MojoFailureException.class)
//    public void throwErrorOnInvalidVersion() throws Exception{
//        Map<String, String> versionsToFind = new HashMap<String, String>();
//        versionsToFind.put("googlechrome", "foo");
//        RepositoryHandler versions = new RepositoryHandler(versionsToFind, false, new File(repositoryMap.toURI()), false);
//        Map<String, String> versionsFound = versions.parseRequiredBrowserAndVersion();
//    }
//
//    @Test
//    public void ignoreInvalidVersion() throws Exception{
//        Map<String, String> versionsToFind = new HashMap<String, String>();
//        versionsToFind.put("googlechrome", "foo");
//        versionsToFind.put("internetexplorer", "2.21.0");
//        RepositoryHandler versions = new RepositoryHandler(versionsToFind, false, new File(repositoryMap.toURI()), true);
//        Map<String, String> versionsFound = versions.parseRequiredBrowserAndVersion();
//
//        assertThat(versionsFound.size(), is(equalTo(1)));
//        assertThat(versionsFound.get("internetexplorer"), is(equalTo("2.21.0")));
//    }
}


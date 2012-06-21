package com.lazerycode.selenium.repository;

import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class RepositoryHandlerTest {

    private URL repositoryMap = this.getClass().getResource("/RepositoryMap.xml");

    @Test
    public void getLatestVersions() throws Exception{
        Map<String, String> versionsFound = new HashMap<String, String>();
        RepositoryHandler versions = new RepositoryHandler(new HashMap<String, String>(), true, new File(repositoryMap.toURI()), false);
        versionsFound = versions.parseRequiredBrowserAndVersion();

        assertThat(versionsFound.get("internetexplorer"), is(equalTo("2.21.0")));
        assertThat(versionsFound.get("googlechrome"), is(equalTo("19")));
    }

    @Test
    public void getSpecificVersions() throws Exception{
        Map<String, String> versionsToFind = new HashMap<String, String>();
        versionsToFind.put("googlechrome", "18");
        RepositoryHandler versions = new RepositoryHandler(versionsToFind, false, new File(repositoryMap.toURI()), false);
        Map<String, String> versionsFound = versions.parseRequiredBrowserAndVersion();

        assertThat(versionsFound.size(), is(equalTo(1)));
        assertThat(versionsFound.get("googlechrome"), is(equalTo("18")));
    }

    @Test(expected = MojoFailureException.class)
    public void throwErrorOnInvalidVersion() throws Exception{
        Map<String, String> versionsToFind = new HashMap<String, String>();
        versionsToFind.put("googlechrome", "foo");
        RepositoryHandler versions = new RepositoryHandler(versionsToFind, false, new File(repositoryMap.toURI()), false);
        Map<String, String> versionsFound = versions.parseRequiredBrowserAndVersion();
    }

    @Test
    public void ignoreInvalidVersion() throws Exception{
        Map<String, String> versionsToFind = new HashMap<String, String>();
        versionsToFind.put("googlechrome", "foo");
        versionsToFind.put("internetexplorer", "2.21.0");
        RepositoryHandler versions = new RepositoryHandler(versionsToFind, false, new File(repositoryMap.toURI()), true);
        Map<String, String> versionsFound = versions.parseRequiredBrowserAndVersion();

        assertThat(versionsFound.size(), is(equalTo(1)));
        assertThat(versionsFound.get("internetexplorer"), is(equalTo("2.21.0")));
    }
}


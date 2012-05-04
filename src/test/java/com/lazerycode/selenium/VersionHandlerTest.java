package com.lazerycode.selenium;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class VersionHandlerTest {

    @Test
    public void getHighestVersion() {
        VersionHandler versionSorter = new VersionHandler();
        versionSorter.addVersion("2.3.1");
        versionSorter.addVersion("2.3.2");

        assertThat(versionSorter.calculateHighestVersion(), is(equalTo("2.3.2")));
    }
}

package com.lazerycode.selenium.download;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ClearSystemProperties;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class DetectProxyConfigTest {

    @Rule
    public final ClearSystemProperties proxyProperties = new ClearSystemProperties("http.proxyHost", "http.proxyPort", "java.net.useSystemProxies");

    @Test
    public void proxySettingsUsedIfTheyAreSet() {
        String host = "localhost";
        Integer port = 8080;

        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxyPort", "" + port);

        DetectProxyConfig proxyConfig = new DetectProxyConfig();
        assertThat(proxyConfig.getHost(), is(equalTo(host)));
        assertThat(proxyConfig.getPort(), is(equalTo(port)));
        assertThat(proxyConfig.isProxyAvailable(), is(equalTo(true)));
    }
}

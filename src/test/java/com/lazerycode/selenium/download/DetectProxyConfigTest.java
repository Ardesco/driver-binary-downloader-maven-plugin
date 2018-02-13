package com.lazerycode.selenium.download;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ClearSystemProperties;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class DetectProxyConfigTest {

    private static final String envHost = "localhost-env";
    private static final Integer envPort = 8081;

    @Rule
    public final EnvironmentVariables envVars = new EnvironmentVariables();

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

    @Test
    public void proxySettingsUsesEnvVariablesIfTheyAreSet() throws Exception {
        envVars.set("http.proxyHost", envHost);
        envVars.set("http.proxyPort", Integer.toString(envPort));

        DetectProxyConfig proxyConfig = new DetectProxyConfig();

        assertThat(proxyConfig.getHost(), is(equalTo(envHost)));
        assertThat(proxyConfig.getPort(), is(equalTo(envPort)));
        assertThat(proxyConfig.isProxyAvailable(), is(equalTo(true)));
    }

    @Test
    public void propertyOverridesEnvVariable() throws Exception {
        String propHost = "localhost";
        Integer propPort = 8080;
        System.setProperty("http.proxyHost", propHost);
        System.setProperty("http.proxyPort", "" + propPort);
        envVars.set("http.proxyHost", envHost);
        envVars.set("http.proxyPort", Integer.toString(envPort));

        DetectProxyConfig proxyConfig = new DetectProxyConfig();

        assertThat(proxyConfig.getHost(), is(equalTo(propHost)));
        assertThat(proxyConfig.getPort(), is(equalTo(propPort)));
        assertThat(proxyConfig.isProxyAvailable(), is(equalTo(true)));
    }

    @Test
    public void nullProxyHostOrProxyPortDoesNotCauseANullPointerErrorToBeThrown() throws Exception {
        DetectProxyConfig proxyConfig = new DetectProxyConfig();
    }
}

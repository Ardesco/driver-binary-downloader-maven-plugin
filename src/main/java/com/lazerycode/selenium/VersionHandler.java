package com.lazerycode.selenium;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.ArrayList;
import java.util.List;

public class VersionHandler {
    public VersionHandler() {

    }

    private List<DefaultArtifactVersion> versionContainer = new ArrayList<DefaultArtifactVersion>();

    DefaultArtifactVersion version = new DefaultArtifactVersion("1.11");
    DefaultArtifactVersion version2 = new DefaultArtifactVersion("1.11");

    public void addVersion(String version) {
        this.versionContainer.add(new DefaultArtifactVersion(version));
    }

    public String calculateHighestVersion() {
        DefaultArtifactVersion highestVersion = null;
        for (DefaultArtifactVersion version : this.versionContainer) {
            if (highestVersion == null || version.compareTo(highestVersion) > 0) {
                highestVersion = version;
            }
        }
        return highestVersion.toString();
    }

}

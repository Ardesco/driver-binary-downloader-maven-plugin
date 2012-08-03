package com.lazerycode.selenium;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.ArrayList;
import java.util.List;

//TODO obselete?

public class VersionHandler {
    public VersionHandler() {
    }

    private List<DefaultArtifactVersion> versionContainer = new ArrayList<DefaultArtifactVersion>();

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

package com.lazerycode.selenium.repository;

import com.lazerycode.selenium.VersionHandler;
import nu.xom.*;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.SystemStreamLog;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RepositoryHandler {

    private Map<String, String> versionMap;
    private boolean getLatest;
    private File xmlFileMap;
    private boolean ignoreInvalidVersions;
    private static final SystemStreamLog logger = new SystemStreamLog();

    public RepositoryHandler(Map<String, String> versionMap, boolean getLatest, File xmlFileMap, boolean ignoreInvalidVersions) {
        this.versionMap = versionMap;
        this.getLatest = getLatest;
        this.xmlFileMap = xmlFileMap;
        this.ignoreInvalidVersions = ignoreInvalidVersions;
    }

    public Map<String, String> parseRequiredBrowserAndVersion() throws MojoFailureException {
        if (this.getLatest == true) {
            return getLatestVersionsFromRepositoryMap(createRepositoryListDocument());
        } else {
            return verifySpecifiedVersionsAreInTheRepositoryMap(createRepositoryListDocument());
        }
    }

    private Map<String, String> verifySpecifiedVersionsAreInTheRepositoryMap(Document repositoryList) throws MojoFailureException {
        Map<String, String> versionsFound = new HashMap<String, String>();
        Iterator it = this.versionMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            Nodes versionCount = repositoryList.query("/root/" + pairs.getKey() + "/version[@id='" + pairs.getValue() + "']");
            if (versionCount.size() == 0) {
                if (!this.ignoreInvalidVersions) {
                    this.logger.error("Unable to find '" + pairs.getKey() + "' versionCount '" + pairs.getKey() + "'!");
                    throw new MojoFailureException("Invalid version!");
                } else {
                    this.logger.warn("Unable to find '" + pairs.getKey() + "' versionCount '" + pairs.getKey() + "'!");
                }
            } else {
                versionsFound.put(pairs.getKey().toString(), pairs.getValue().toString());
            }
        }
        return versionsFound;
    }

    private Map<String, String> getLatestVersionsFromRepositoryMap(Document repositoryList) {
        Map<String, String> versionsFound = new HashMap<String, String>();
        Nodes seleniumStandaloneExecutables = repositoryList.query("/root/*");
        for (int i = 0; i < seleniumStandaloneExecutables.size(); i++) {
            Element driver = (Element) seleniumStandaloneExecutables.get(i);
            VersionHandler seleniumVersions = new VersionHandler();
            Elements versions = driver.getChildElements("version");
            for (int n = 0; n < versions.size(); n++) {
                seleniumVersions.addVersion(versions.get(n).getAttribute("id").getValue());
            }
            versionsFound.put(driver.getLocalName(), seleniumVersions.calculateHighestVersion());
        }
        return versionsFound;
    }

    private Document createRepositoryListDocument() throws MojoFailureException {
        try {
            return new Builder(true).build(this.xmlFileMap);
        } catch (ParsingException ex) {
            this.logger.error("Unable to parse the repository map!");
            throw new MojoFailureException("Unable to parse repository map");
        } catch (IOException ex) {
            this.logger.error("Unable to access " + this.xmlFileMap.toString() + "!");
            throw new MojoFailureException("Unable to find repository map");
        }
    }
}

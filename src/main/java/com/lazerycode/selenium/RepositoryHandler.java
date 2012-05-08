package com.lazerycode.selenium;

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

    public Map<String, String> parseRequiredFiles() throws MojoFailureException {
        Map<String, String> versionsFound = new HashMap<String, String>();
        Document repositoryList = createRepositoryListDocument();
        if (this.getLatest == true) {
            Nodes driverStandalones = repositoryList.query("/root/*");
            for (int i = 0; i < driverStandalones.size(); i++) {
                Element driver = (Element) driverStandalones.get(i);
                VersionHandler driverVersions = new VersionHandler();
                Elements versions = driver.getChildElements("version");
                for (int n = 0; n < versions.size(); n++) {
                    driverVersions.addVersion(versions.get(n).getAttribute("id").getValue());
                }
                versionsFound.put(driver.getLocalName(), driverVersions.calculateHighestVersion());
            }
        } else {
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
        }
        return versionsFound;
    }

    private Document createRepositoryListDocument() throws MojoFailureException {
        Builder xmlParser = new Builder(true);
        Document repositoryList;
        try {
            repositoryList = xmlParser.build(this.xmlFileMap);
            return repositoryList;
        } catch (ParsingException ex) {
            this.logger.error("Unable to parse the repository map!");
            throw new MojoFailureException("Unable to parse repository map");
        } catch (IOException ex) {
            this.logger.error("Unable to access " + this.xmlFileMap.toString() + "!");
            throw new MojoFailureException("Unable to find repository map");
        }
    }
}

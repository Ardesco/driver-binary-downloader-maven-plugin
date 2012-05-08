package com.lazerycode.selenium;

import nu.xom.*;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RepositoryHandler {

    private Map<String, String> versionMap;
    private boolean getLatest;
    private File xmlFileMap;

    public RepositoryHandler(Map<String, String> versionMap, boolean getLatest, File xmlFileMap){
        this.versionMap = versionMap;
        this.getLatest = getLatest;
        this.xmlFileMap = xmlFileMap;
    }

    public Map<String, String> parseRequiredFiles() throws MojoFailureException {
        Document repositoryList = createRepositoryListDocument();
        if (this.getLatest == true) {
            Nodes driverStandalones = repositoryList.query("/root/*");
            for(int i = 0; i < driverStandalones.size(); i++){
                Element driver = (Element) driverStandalones.get(i);
                VersionHandler driverVersions= new VersionHandler();
                Elements versions = driver.getChildElements("version");
                for(int n = 0; n < versions.size(); n++){
                    driverVersions.addVersion(versions.get(n).getAttribute("id").getValue());
                }
                this.versionMap.put(driver.getLocalName(), driverVersions.calculateHighestVersion());
            }
        } else {
            //TODO Validate the getVersions map and advise the user if we can't match any of them.
            //TODO throw exception if driver/version not found (enable a way to suppress this)
        }
        return this.versionMap;
    }

    private Document createRepositoryListDocument() throws MojoFailureException {
        Builder xmlParser = new Builder(true);
        Document repositoryList;
        try {
            repositoryList = xmlParser.build(this.xmlFileMap);
            return repositoryList;
        } catch (ParsingException ex) {
//            getLog().error("Unable to parse the repository map!");
            throw new MojoFailureException("Unable to parse repository map");
        } catch (IOException ex) {
//            getLog().error("Unable to access " + this.xmlFileMap.toString() + "!");
            throw new MojoFailureException("Unable to find repository map");
        }
    }
}

package com.lazerycode.selenium.repository;

import com.lazerycode.selenium.OS;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RepositoryParser {

    private Document repositoryMap;
    private StandAloneServer mappedRepository;
    private boolean onlyGetLatestVersions = true;

    private Map<String, String> getSpecificExecutableVersions;
    private ArrayList<OS> operatingSystemList;
    private boolean getSixtyFourBitBinaries;
    private boolean getThirtyTwoBitBinaries;

    public RepositoryParser(File repositoryMapLocation, ArrayList<OS> operatingSystems, boolean thirtyTwoBit, boolean sixtyFourBit, boolean onlyGetLatestVersions) throws MojoFailureException {
        Builder parser = new Builder();
        try {
            this.repositoryMap = parser.build(repositoryMapLocation);
        } catch (ParsingException pe) {
            throw new MojoFailureException(pe.getLocalizedMessage());
        } catch (IOException ioe) {
            throw new MojoFailureException(ioe.getLocalizedMessage());
        }
        this.operatingSystemList = operatingSystems;
        this.getThirtyTwoBitBinaries = thirtyTwoBit;
        this.getSixtyFourBitBinaries = sixtyFourBit;
        this.onlyGetLatestVersions = onlyGetLatestVersions;
    }

    public void specifySpecificExecutableVersions(Map<String, String> executableVersions) {
        this.onlyGetLatestVersions = false;
        this.getSpecificExecutableVersions = executableVersions;
    }

    private Nodes getAllChildren(String xpath) {
        return repositoryMap.query(xpath + "/*");
    }

    private StandAloneServer parseRepositoryMap() {

        //if getSpecificExecutableVersions is set a simple mapping to known path should work
        // e.g. /root/driver id="${getSpecificExecutableVersions}.getKey()"/version id="${getSpecificExecutableVersions}.getValue()"/OS LIST/BIT boolean/rest known



        Nodes drivers = getAllChildren("/root");
        for (int i = 0; i < drivers.size(); i++) {
            this.mappedRepository.addStandaloneExecutableForDriverType(drivers.get(i).getValue());

        }

        return this.mappedRepository;
    }

    public HashMap<String, FileDetails> getFilesToDownload(){
        return null;
    }
}

package com.lazerycode.selenium.repository;

import com.lazerycode.selenium.OS;
import nu.xom.*;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RepositoryParser {

    private Document repositoryMap;
    private StandAloneServer mappedRepository;
    private boolean onlyGetLatestVersions = true;
    private boolean selectivelyParseDriverExecutableList = false;

    private Map<String, ArrayList<String>> getSpecificExecutableVersions;
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
        this.selectivelyParseDriverExecutableList = true;
        this.onlyGetLatestVersions = false;

        for (Iterator iterator = executableVersions.keySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, String> values = (Map.Entry<String, String>) iterator.next();
            if (!this.getSpecificExecutableVersions.containsKey(values.getKey())) this.getSpecificExecutableVersions.put(values.getKey(), new ArrayList<String>());
            ((ArrayList) this.getSpecificExecutableVersions.get(values.getKey())).add(values.getValue());
        }
    }

    private Nodes getAllChildren(String xpath) {
        return repositoryMap.query(xpath + "/*");
    }

    private StandAloneServer parseRepositoryMap() {

        //if getSpecificExecutableVersions is set a simple mapping to known path should work
        // e.g. /root/driver id="${getSpecificExecutableVersions}.getKey()"/version id="${getSpecificExecutableVersions}.getValue()"/OS LIST/BIT boolean/rest known


        Nodes availableDrivers = getAllChildren("/root/driver");
        Nodes usedDrivers = new Nodes();


        //TODO break out below blocks into functions, use boolean information to work out which functions to call

        for (int i = 0; i < availableDrivers.size(); i++) {
            String driverType = ((Element) availableDrivers.get(i)).getAttribute("id").getValue();
            for (Iterator iterator = this.getSpecificExecutableVersions.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, String> driverDetail = (Map.Entry<String, String>) iterator.next();
                if (driverDetail.getKey().equalsIgnoreCase(driverType)) {
                    usedDrivers.append((Node) availableDrivers.get(i));
                    break;
                }
            }
        }


        for (int i = 0; i < usedDrivers.size(); i++) {

            for (Iterator iterator = this.getSpecificExecutableVersions.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, String> versionInfo = (Map.Entry<String, String>) iterator.next();
                Nodes availableVersions = getAllChildren("/root/driver[@id='" + versionInfo.getKey() + "'/version");
                //iterate through versions to check them and add to another list
            }
        }

        //go through os list matching have 2 options 32 and 64bit

        //generate a final node ArrayList and use it to populate a FileDetails object

        return this.mappedRepository;
    }

    public HashMap<String, FileDetails> getFilesToDownload() {
        return null;
    }
}

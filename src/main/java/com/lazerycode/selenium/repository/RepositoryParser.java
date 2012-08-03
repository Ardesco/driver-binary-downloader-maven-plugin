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
    private FileDetails mappedRepository;
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
            if (!this.getSpecificExecutableVersions.containsKey(values.getKey()))
                this.getSpecificExecutableVersions.put(values.getKey(), new ArrayList<String>());
            ((ArrayList) this.getSpecificExecutableVersions.get(values.getKey())).add(values.getValue());
        }
    }

    private Nodes getAllChildren(String xpath) {
        return repositoryMap.query(xpath + "/*");
    }

    private Nodes getAllRelevantDriverNodes() {
        Nodes availableDrivers = getAllChildren("/root/driver");
        Nodes usedDrivers = new Nodes();

        for (int i = 0; i < availableDrivers.size(); i++) {
            String driverType = ((Element) availableDrivers.get(i)).getAttribute("id").getValue();
            if (this.selectivelyParseDriverExecutableList) {
                for (Iterator iterator = this.getSpecificExecutableVersions.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry<String, String> driverDetail = (Map.Entry<String, String>) iterator.next();
                    if (driverDetail.getKey().equalsIgnoreCase(driverType)) {
                        usedDrivers.append((Node) availableDrivers.get(i));
                        break;
                    }
                }
            } else {
                usedDrivers.append((Node) availableDrivers.get(i));
            }
        }

        return usedDrivers;
    }

    private Nodes getFilteredListOfVersionNodes(Nodes usedDrivers) {
        Nodes filteredVersions = new Nodes();

        for (int i = 0; i < usedDrivers.size(); i++) {
            String driverID = ((Element) usedDrivers.get(i)).getAttribute("id").getValue();
            Nodes availableVersions = getAllChildren("/root/driver[@id='" + driverID + "'/version");
            if (this.selectivelyParseDriverExecutableList) {
                //Map versions based upon this.getSpecificExecutableVersions
                //TODO break out
                for (Iterator foob = this.getSpecificExecutableVersions.entrySet().iterator(); foob.hasNext(); ) {
                    Map.Entry<String, String> driverDetail = (Map.Entry<String, String>) foob.next();
                    if (driverDetail.getKey().equalsIgnoreCase(driverID)) {
                        for (int j = 0; j < availableVersions.size(); j++) {
                            String currentVersion = ((Element) availableVersions.get(j)).getAttribute("id").getValue();
                            if (driverDetail.getValue().equalsIgnoreCase(currentVersion)) {
                                filteredVersions.append(availableVersions.get(j));
                                break;
                            }
                        }
                    }
                }
            } else if (this.onlyGetLatestVersions) {
                //Add latest version only
                //TODO break out
                Node nodeToAdd = null;
                String highestVersion = null;
                for (int j = 0; j < availableVersions.size(); j++) {
                    String currentVersion = ((Element) availableVersions.get(j)).getAttribute("id").getValue();
                    if (highestVersion == null || currentVersion.compareTo(highestVersion) > 0) {
                        highestVersion = currentVersion;
                        nodeToAdd = availableVersions.get(j);
                    }
                }
                if (nodeToAdd != null) filteredVersions.append(nodeToAdd);
            } else {
                //Add all versions
                //TODO break out
                for (int j = 0; j < availableVersions.size(); j++) {
                    filteredVersions.append(availableVersions.get(j));
                }
            }
        }

        return filteredVersions;
    }

    private FileDetails parseRepositoryMap() {

        //if getSpecificExecutableVersions is set a simple mapping to known path should work
        // e.g. /root/driver id="${getSpecificExecutableVersions}.getKey()"/version id="${getSpecificExecutableVersions}.getValue()"/OS LIST/BIT boolean/rest known


        Nodes usedDrivers = getAllRelevantDriverNodes();
        Nodes usedVersions = getFilteredListOfVersionNodes(usedDrivers);

        //Go through os list matching have 2 options 32 and 64bit
        Nodes finalList = new Nodes();

        for (int i = 0; i < usedVersions.size(); i++) {
            Node node = usedVersions.get(i);
            for (int j = 0; j < operatingSystemList.size(); j++) {
                OS os =  operatingSystemList.get(j);
                String baseXPath = "./" + os.toString();
                if (this.getThirtyTwoBitBinaries){
                    Node foo = node.query(baseXPath + "[@thirtytwobit='true']").get(1);
                    if (foo != null) finalList.append(foo);
                }
                if (this.getSixtyFourBitBinaries){
                    Node foo = node.query(baseXPath + "[@sixtyfourbit='true']").get(1);
                    if (foo != null) finalList.append(foo);
                }
            }
        }


        //generate a final node ArrayList and use it to populate a FileDetails object

        for (int i = 0; i < finalList.size(); i++) {
            Node node =  finalList.get(i);
//            (Element) node.query("./filelocation").get(1);
//            (Element) node.query("./hash").get(1);
//            (Element) node.query("./hashtype").get(1);

        }

        return this.mappedRepository;
    }

    public HashMap<String, FileDetails> getFilesToDownload() {
        return null;
    }
}

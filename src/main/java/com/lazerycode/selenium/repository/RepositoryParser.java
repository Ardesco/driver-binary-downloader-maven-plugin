package com.lazerycode.selenium.repository;

import com.lazerycode.selenium.OS;
import nu.xom.*;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RepositoryParser {

    private Document repositoryMap;
    private ArrayList<OS> operatingSystemList;
    private Map<String, String> bitRates = new HashMap<String, String>();
    private Map<String, ArrayList<String>> getSpecificExecutableVersions = new HashMap<String, ArrayList<String>>();
    private boolean onlyGetLatestVersions = true;
    private boolean selectivelyParseDriverExecutableList = false;

    private HashMap<String, FileDetails> downloadableFileList = new HashMap<String, FileDetails>();

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
        if (thirtyTwoBit) this.bitRates.put("32bit", "[@thirtytwobit='true']");
        if (sixtyFourBit) this.bitRates.put("64bit", "[@sixtyfourbit='true']");
        this.onlyGetLatestVersions = onlyGetLatestVersions;
    }

    public void specifySpecificExecutableVersions(Map<String, String> executableVersions) {
        this.selectivelyParseDriverExecutableList = true;
        this.onlyGetLatestVersions = false;

        for (Iterator iterator = executableVersions.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, String> values = (Map.Entry<String, String>) iterator.next();
            if (!this.getSpecificExecutableVersions.containsKey(values.getKey())) {
                this.getSpecificExecutableVersions.put(values.getKey(), new ArrayList<String>());
            }
            ((ArrayList) this.getSpecificExecutableVersions.get(values.getKey())).add(values.getValue());
        }
    }

    /**
     * Take an existing node, find all the child nodes and return them as a list.
     *
     * @param xpath
     * @return
     */
    private Nodes getAllChildren(String xpath) {
        return repositoryMap.query(xpath + "/*");
    }

    private Nodes getAllRelevantDriverNodes() {
        Nodes availableDrivers = getAllChildren("/root");
        Nodes usedDrivers = new Nodes();

        for (int currentDriver = 0; currentDriver < availableDrivers.size(); currentDriver++) {
            String driverType = ((Element) availableDrivers.get(currentDriver)).getAttribute("id").getValue();
            if (this.selectivelyParseDriverExecutableList) {
                for (Iterator iterator = this.getSpecificExecutableVersions.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry<String, String> driverDetail = (Map.Entry<String, String>) iterator.next();
                    if (driverDetail.getKey().equalsIgnoreCase(driverType)) {
                        usedDrivers.append((Node) availableDrivers.get(currentDriver));
                        break;
                    }
                }
            } else {
                usedDrivers.append((Node) availableDrivers.get(currentDriver));
            }
        }

        return usedDrivers;
    }

    private Nodes getFilteredListOfVersionNodes(Nodes usedDrivers) {
        Nodes filteredVersions = new Nodes();

        for (int i = 0; i < usedDrivers.size(); i++) {
            String driverID = ((Element) usedDrivers.get(i)).getAttribute("id").getValue();
            Nodes availableVersions = getAllChildren("/root/driver[@id='" + driverID + "']");
            if (this.selectivelyParseDriverExecutableList) {
                //Map versions based upon this.getSpecificExecutableVersions
                //TODO break out
                for (Iterator foob = this.getSpecificExecutableVersions.entrySet().iterator(); foob.hasNext(); ) {
                    Map.Entry<String, ArrayList<String>> driverDetail = (Map.Entry<String, ArrayList<String>>) foob.next();
                    if (driverDetail.getKey().equalsIgnoreCase(driverID)) {
                        for (int j = 0; j < availableVersions.size(); j++) {
                            String currentVersion = ((Element) availableVersions.get(j)).getAttribute("id").getValue();
                            ArrayList<String> wantedVersions = driverDetail.getValue();
                            for (int current = 0; current < wantedVersions.size(); current++) {
                                if (wantedVersions.get(current).equalsIgnoreCase(currentVersion)) {
                                    filteredVersions.append(availableVersions.get(j));
                                    break;
                                }
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

    /**
     * Extract all the file information needed to download a specific file from the children of a specific node.
     *
     * @param downloadableZipInformation
     * @return
     * @throws MalformedURLException
     */
    private FileDetails extractFileInformation(Node downloadableZipInformation) throws MalformedURLException {
        FileDetails fileDownloadInformation = new FileDetails();

        fileDownloadInformation.setFileLocation((downloadableZipInformation.query("./filelocation").get(0)).getValue());
        fileDownloadInformation.setHash((downloadableZipInformation.query("./hash").get(0)).getValue());
        fileDownloadInformation.setHashType((downloadableZipInformation.query("./hashtype").get(0)).getValue());

        return fileDownloadInformation;
    }

    private void addDownloadableFilesToList(Node node, String osString) throws MalformedURLException {
        osString = osString.toLowerCase();

        for (Iterator iterator = this.bitRates.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, String> bitRate = (Map.Entry<String, String>) iterator.next();
            Nodes fileDetails = node.query("./" + osString + bitRate.getValue());
            String driverType = ((Element) node.getParent()).getAttribute("id").getValue();
            String driverVersion = ((Element) node).getAttribute("id").getValue();
            String extractionPath = driverType + File.separator + osString.toLowerCase() + File.separator + bitRate.getKey() + File.separator + driverVersion;
            if (fileDetails.size() > 0) this.downloadableFileList.put(extractionPath, extractFileInformation(fileDetails.get(0)));
        }
    }

    /**
     * Parse the RepositoryMap.xml and return a HashMap of downloadable files.
     *
     * @return
     * @throws MalformedURLException
     */
    public HashMap<String, FileDetails> getFilesToDownload() throws MalformedURLException {
        Nodes usedVersions = getFilteredListOfVersionNodes(getAllRelevantDriverNodes());

        for (int selectedVersion = 0; selectedVersion < usedVersions.size(); selectedVersion++) {
            for (int selectedOperatingSystem = 0; selectedOperatingSystem < operatingSystemList.size(); selectedOperatingSystem++) {
                addDownloadableFilesToList(usedVersions.get(selectedVersion), operatingSystemList.get(selectedOperatingSystem).toString());
            }
        }

        return this.downloadableFileList;
    }
}

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
    private HashMap<String, FileDetails> downloadableFileList = new HashMap<String, FileDetails>();
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
        Nodes availableDrivers = getAllChildren("/root");
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
            Nodes availableVersions = getAllChildren("/root/driver[@id='" + driverID + "']");
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

    private FileDetails extractFileInformation(Node downloadableZipInformation) throws MalformedURLException {
        FileDetails fileDownloadInformation = new FileDetails();

        fileDownloadInformation.setFileLocation((downloadableZipInformation.query("./filelocation").get(0)).getValue());
        fileDownloadInformation.setHash((downloadableZipInformation.query("./hash").get(0)).getValue());
        fileDownloadInformation.setHashType((downloadableZipInformation.query("./hashtype").get(0)).getValue());

        return fileDownloadInformation;
    }

    private HashMap<String, FileDetails> parseRepositoryMap() throws MalformedURLException {

        Nodes usedDrivers = getAllRelevantDriverNodes();
        Nodes usedVersions = getFilteredListOfVersionNodes(usedDrivers);

        //Go through os list matching have 2 options 32 and 64bit

        for (int i = 0; i < usedVersions.size(); i++) {
            Node node = usedVersions.get(i);
            for (int j = 0; j < operatingSystemList.size(); j++) {
                OS os = operatingSystemList.get(j);
                String baseXPath = "./" + os.toString().toLowerCase();
                if (this.getThirtyTwoBitBinaries) {
                    Nodes bar = node.query(baseXPath + "[@thirtytwobit='true']");
                    String currentDriver = ((Element) usedVersions.get(i).getParent()).getAttribute("id").getValue();
                    String currentVersion = ((Element) usedVersions.get(i)).getAttribute("id").getValue();
                    String extractionPath = currentDriver + File.separator + os.toString().toLowerCase() + File.separator + "32bit" + File.separator + currentVersion;
                    if (bar.size() > 0) {
                        this.downloadableFileList.put(extractionPath, extractFileInformation(bar.get(0)));
                    }
                }
                if (this.getSixtyFourBitBinaries) {
                    Nodes bar = node.query(baseXPath + "[@sixtyfourbit='true']");
                    String currentDriver = ((Element) usedVersions.get(i).getParent()).getAttribute("id").getValue();
                    String currentVersion = ((Element) usedVersions.get(i)).getAttribute("id").getValue();
                    String extractionPath = currentDriver + File.separator + os.toString().toLowerCase() + File.separator + "64bit" + File.separator + currentVersion;
                    if (bar.size() > 0) {
                        this.downloadableFileList.put(extractionPath, extractFileInformation(bar.get(0)));
                    }
                }
            }
        }

        return this.downloadableFileList;
    }

    public HashMap<String, FileDetails> getFilesToDownload() throws Exception {
        return parseRepositoryMap();
    }
}

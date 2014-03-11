package com.lazerycode.selenium.repository;

import com.lazerycode.selenium.OS;
import nu.xom.*;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RepositoryParser {

    private static final Logger LOG = Logger.getLogger(RepositoryParser.class);
    private final Map<String, ArrayList<String>> getSpecificExecutableVersions = new HashMap<String, ArrayList<String>>();
    private final Map<String, String> bitRates = new HashMap<String, String>();
    private final HashMap<String, FileDetails> downloadableFileList = new HashMap<String, FileDetails>();
    private Document repositoryMap;
    private ArrayList<OS> operatingSystemList;
    private boolean onlyGetLatestVersions = true;
    private boolean selectivelyParseDriverExecutableList = false;
    private boolean throwExceptionIfSpecifiedVersionIsNotFound = false;

    public RepositoryParser(InputStream repositoryMapLocation, ArrayList<OS> operatingSystems, boolean thirtyTwoBit, boolean sixtyFourBit, boolean onlyGetLatestVersions, boolean throwExceptionIfSpecifiedVersionIsNotFound) throws MojoFailureException {
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
        LOG.info("Download 32bit binaries: " + thirtyTwoBit);
        if (sixtyFourBit) this.bitRates.put("64bit", "[@sixtyfourbit='true']");
        LOG.info("Download 64bit binaries: " + sixtyFourBit);
        this.onlyGetLatestVersions = onlyGetLatestVersions;
        LOG.info("Download Latest Versions Only: " + onlyGetLatestVersions);
        this.throwExceptionIfSpecifiedVersionIsNotFound = throwExceptionIfSpecifiedVersionIsNotFound;
        LOG.info("Throw Exception If Specified Version Is Not Found: " + throwExceptionIfSpecifiedVersionIsNotFound);
        LOG.info(" ");
    }

    /**
     * Supply a specific map of drivers and versions to download.
     * This implicitly disables the ability to only get the latest versions.
     *
     * @param executableVersions a map of executable versions.
     */
    public void specifySpecificExecutableVersions(Map<String, String> executableVersions) {
        this.selectivelyParseDriverExecutableList = true;
        this.onlyGetLatestVersions = false;

        for (Map.Entry<String, String> values : executableVersions.entrySet()) {
            if (!this.getSpecificExecutableVersions.containsKey(values.getKey())) {
                this.getSpecificExecutableVersions.put(values.getKey(), new ArrayList<String>());
            }
            this.getSpecificExecutableVersions.get(values.getKey()).add(values.getValue());
        }
    }

    /**
     * Take an existing node, find all the child nodes and return them as a list.
     *
     * @param xpath base xpath to find children of
     * @return A list of child nodes
     */
    private Nodes getAllChildren(String xpath) {
        return repositoryMap.query(xpath + "/*");
    }

    /**
     * Read the RepositoryMap.xml and extract a filtered list of drivers that there are download locations for
     *
     * @return A list of driver nodes
     */
    private Nodes getAllRelevantDriverNodes(String operatingSystem) {
        Nodes availableDrivers = getAllChildren("/root/" + operatingSystem);
        Nodes usedDrivers = new Nodes();

        for (int currentDriver = 0; currentDriver < availableDrivers.size(); currentDriver++) {
            String driverType = ((Element) availableDrivers.get(currentDriver)).getAttribute("id").getValue();
            //If a specific map of driver executable/version has been passed in use it to filter output
            if (this.selectivelyParseDriverExecutableList) {
                for (Map.Entry<String, ArrayList<String>> stringArrayListEntry : this.getSpecificExecutableVersions.entrySet()) {
                    if (stringArrayListEntry.getKey().equalsIgnoreCase(driverType)) {
                        usedDrivers.append(availableDrivers.get(currentDriver));
                        break;
                    }
                }
            } else {
                usedDrivers.append(availableDrivers.get(currentDriver));
            }
        }

        return usedDrivers;
    }

    /**
     * Take a list of nodes and work out which has the highest version number by comparing the id attribute of all of them
     *
     * @param listOfVersions a list of nodes
     * @return a list of nodes only containing the highest version of each binary
     */
    private Nodes getHighestVersion(Nodes listOfVersions) {
        Nodes highestVersionsList = new Nodes();
        Node nodeToAdd = null;
        String highestVersion = null;
        for (int i = 0; i < listOfVersions.size(); i++) {
            String currentVersion = ((Element) listOfVersions.get(i)).getAttribute("id").getValue();
            if (highestVersion == null || currentVersion.compareTo(highestVersion) > 0) {
                highestVersion = currentVersion;
                nodeToAdd = listOfVersions.get(i);
            }
            if (nodeToAdd != null) highestVersionsList.append(nodeToAdd);
        }

        return highestVersionsList;
    }

    /**
     * Scan through a list of nodes and match any that have been specifically requested.
     * Return a list of nodes to use
     *
     * @param listOfVersions a list of available versions
     * @param driverID       the binary we are interested in
     * @return a list of binaries that match the search criteria
     */
    private Nodes getSpecificVersions(Nodes listOfVersions, String driverID) throws MojoFailureException {
        Nodes filteredVersions = new Nodes();
        for (Map.Entry<String, ArrayList<String>> driverDetail : this.getSpecificExecutableVersions.entrySet()) {
            if (driverDetail.getKey().equalsIgnoreCase(driverID)) {
                ArrayList<String> wantedVersions = driverDetail.getValue();
                for (String wantedVersion : wantedVersions) {
                    boolean versionFound = false;
                    for (int j = 0; j < listOfVersions.size(); j++) {
                        String currentVersion = ((Element) listOfVersions.get(j)).getAttribute("id").getValue();
                        if (wantedVersion.equalsIgnoreCase(currentVersion)) {
                            filteredVersions.append(listOfVersions.get(j));
                            LOG.info("Found " + driverID + " version " + wantedVersion + " in the repository map.");
                            versionFound = true;
                            break;
                        }
                    }
                    if (this.throwExceptionIfSpecifiedVersionIsNotFound && !versionFound) {
                        throw new MojoFailureException("Unable to find" + driverID + " version " + wantedVersion + " in the repository map.");
                    }
                }
            }
        }

        return filteredVersions;
    }

    private Nodes getFilteredListOfVersionNodes(Nodes usedDrivers, String operatingSystem) throws MojoFailureException {
        Nodes filteredVersions = new Nodes();
        if (this.selectivelyParseDriverExecutableList) {
            LOG.info("Parsing Specific Executable Versions Supplied...");
            LOG.info(" ");
        }
        for (int i = 0; i < usedDrivers.size(); i++) {
            String driverID = ((Element) usedDrivers.get(i)).getAttribute("id").getValue();
            Nodes availableVersions = getAllChildren("/root/" + operatingSystem + "/driver[@id='" + driverID + "']");
            if (this.selectivelyParseDriverExecutableList) {
                Nodes specificVersions = getSpecificVersions(availableVersions, driverID);
                for (int specificVersion = 0; specificVersion < specificVersions.size(); specificVersion++) {
                    Node node = specificVersions.get(specificVersion);
                    filteredVersions.append(node);
                }
            } else if (this.onlyGetLatestVersions) {
                if (availableVersions.size() > 0) {
                    Nodes highestVersions = getHighestVersion(availableVersions);
                    for (int highestVersion = 0; highestVersion < highestVersions.size(); highestVersion++) {
                        Node node = highestVersions.get(highestVersion);
                        filteredVersions.append(node);
                    }
                }
            } else {
                for (int currentVersion = 0; currentVersion < availableVersions.size(); currentVersion++) {
                    filteredVersions.append(availableVersions.get(currentVersion));
                }
            }
        }
        if (this.selectivelyParseDriverExecutableList) LOG.info(" ");

        return filteredVersions;
    }

    /**
     * Extract all the file information needed to download a specific file from the children of a specific node.
     *
     * @param downloadableZipInformation A node containing information about the binary we are going to download
     * @return A FileDetails object containing the location of the file, a hash to confirm it is valid when downloaded and a hash type.
     * @throws MalformedURLException
     */
    private FileDetails extractFileInformation(Node downloadableZipInformation) throws MalformedURLException, IllegalArgumentException {
        String fileLocation = downloadableZipInformation.query("./filelocation").get(0).getValue();
        String hash = null;
        String hashType = null;
        if (downloadableZipInformation.query("./hash").size() != 0 && downloadableZipInformation.query("./hashtype").size() != 0) {
            hash = downloadableZipInformation.query("./hash").get(0).getValue();
            hashType = downloadableZipInformation.query("./hashtype").get(0).getValue();
        }

        return new FileDetails(fileLocation, hashType, hash);
    }

    /**
     * Takes a node from the RepositoryMap.xml and extracts file information and builds a zip extraction path.
     * This information is then added to the downloadable file list.
     *
     * @param node the node of a binary we want to download
     * @throws MalformedURLException
     */
    private void addDownloadableFilesToList(Node node, String operatingSystem) throws MalformedURLException {
        for (Map.Entry<String, String> bitRate : this.bitRates.entrySet()) {
            Nodes fileDetails = node.query("./bitrate" + bitRate.getValue());
            String driverType = ((Element) node.getParent()).getAttribute("id").getValue();
            String driverVersion = ((Element) node).getAttribute("id").getValue();
            String extractionPath = operatingSystem + File.separator + driverType + File.separator + bitRate.getKey() + File.separator + driverVersion;
            if (fileDetails.size() > 0)
                this.downloadableFileList.put(extractionPath, extractFileInformation(fileDetails.get(0)));
        }
    }

    /**
     * Parse the RepositoryMap.xml and return a HashMap of downloadable files.
     *
     * @return A map of file versions/file URL's that will be downloaded
     * @throws MalformedURLException
     */
    public HashMap<String, FileDetails> getFilesToDownload() throws MalformedURLException, MojoFailureException {
        for (OS anOperatingSystemList : operatingSystemList) {
            Nodes usedVersions = getFilteredListOfVersionNodes(getAllRelevantDriverNodes(anOperatingSystemList.toString().toLowerCase()), anOperatingSystemList.toString().toLowerCase());
            for (int selectedVersion = 0; selectedVersion < usedVersions.size(); selectedVersion++) {
                addDownloadableFilesToList(usedVersions.get(selectedVersion), anOperatingSystemList.toString().toLowerCase());
            }
        }

        return this.downloadableFileList;
    }
}

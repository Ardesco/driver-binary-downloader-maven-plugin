package com.lazerycode.selenium;

import nu.xom.*;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SeleniumServerMojo extends AbstractMojo {
    /**
     * Root directory where the standalone server file structure will be created and files will be saved
     *
     * @parameter
     */
    protected File rootStandaloneServerDirectory;

    /**
     * Absolute path to the XML RepositoryMap
     *
     * @parameter default-value="${project.basedir}/src/main/resources/RepositoryMap.xml"
     */
    protected File xmlFileMap;

    /**
     * Disable running an SHA1 check on downloaded standalone server binaries
     *
     * @parameter default-value="false"
     */
    protected boolean disableSHA1HashCheck;

    /**
     * Always downloadZipAndExtractFiles the server binaries, even if you already have them.
     * (If MD5 Checks are disabled it will only use the filename to determine if the standalone server binary has already been downloaded)
     *
     * @parameter default-value="false"
     */
    protected boolean alwaysUpdate;

    /**
     * Get 64 bit versions of the standalone server
     *
     * @parameter default-value="false"
     */
    protected boolean getSixtyFourBit;

    /**
     * Get 32 bit versions of the standalone server
     *
     * @parameter default-value="true"
     */
    protected boolean getThirtyTwoBit;

    /**
     * Get the highest version of each driver in RepositoryMap.xml
     *
     * @parameter default-value="false"
     */
    protected boolean getLatestVersions;

    /**
     * A map of driver standalone versions to downloadZipAndExtractFiles eg:
     * <p/>
     * <googlechrome>19</googlechrome>
     * <internetexplorer>2.21.0</internetexplorer>
     *
     * @parameter
     */
    protected Map<String, String> getVersions;

    private RepositoryParser searchMap;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info(" ");
        getLog().info("-------------------------------------------------------");
        getLog().info(" DOWNLOADING SELENIUM STAND-ALONE EXECUTABLES...");
        getLog().info("-------------------------------------------------------");
        getLog().info(" ");
        if (this.getVersions.size() == 0) this.getLatestVersions = true;
        parseRequiredFiles();
        //TODO go and get the files
        getLog().info(" ");
        getLog().info("-------------------------------------------------------");
        getLog().info(" SELENIUM STAND-ALONE EXECUTABLES DOWNLOAD COMPLETE");
        getLog().info("-------------------------------------------------------");
        getLog().info(" ");
    }

    private void parseRequiredFiles() throws MojoFailureException {
        Document repositoryList = createRepositoryListDocument();
        if (this.getLatestVersions == true) {
            Nodes driverStandalones = repositoryList.query("/root/*");
            for(int i = 1; i < driverStandalones.size(); i++){
                Element driver = driverStandalones.get(i).getDocument().getRootElement();
                VersionHandler driverVersions= new VersionHandler();
                Elements versions = driver.getChildElements("version");
                for(int n = 1; n < versions.size(); n++){
                    driverVersions.addVersion(versions.get(n).getAttribute("id").getValue());
                }
                this.getVersions.put(driver.getLocalName(), driverVersions.calculateHighestVersion());
            }
        } else {
            //TODO Validate the getVersions map and advise the user if we can't match any of them.
            //TODO throw exception if driver/version not found (enable a way to suppress this)
        }
    }

    private Document createRepositoryListDocument() throws MojoFailureException {
        Builder xmlParser = new Builder(true);
        Document repositoryList;
        try {
            repositoryList = xmlParser.build(this.xmlFileMap);
            return repositoryList;
        } catch (ParsingException ex) {
            getLog().error("Unable to parse the repository map!");
            throw new MojoFailureException("Unable to parse repository map");
        } catch (IOException ex) {
            getLog().error("Unable to access " + this.xmlFileMap.toString() + "!");
            throw new MojoFailureException("Unable to find repository map");
        }
    }
}
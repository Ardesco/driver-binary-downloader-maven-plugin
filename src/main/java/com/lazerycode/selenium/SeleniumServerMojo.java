package com.lazerycode.selenium;

import com.lazerycode.selenium.download.DownloadHandler;
import com.lazerycode.selenium.repository.RepositoryHandler;
import com.lazerycode.selenium.repository.RepositoryParser;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.util.Map;

/**
 * Selenium Standalone Server Maven Plugin
 *
 * @author Mark Collin
 * @goal selenium
 * @requiresProject true
 */

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
     * Disable running a SHA1 check on downloaded standalone server binaries
     *
     * @parameter default-value="false"
     */
    protected boolean disableSHA1HashCheck;

    /**
     * If you have disabled SHA1 hash checking, force a download of the standalone binaries.
     *
     * @parameter default-value="false"
     */
    protected boolean forceUncheckedFileUpdate;

    /**
     * Get 64 bit versions of the standalone server
     *
     * @parameter default-value="true"
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
     * A map of driver standalone versions to download eg:
     *
     * <googlechrome>19</googlechrome>
     * <internetexplorer>2.21.0</internetexplorer>
     *
     * Unrecognised browser names/versions will cause an exception to be thrown
     *
     * @parameter
     */
    protected Map<String, String> getVersions;

    /**
     * If there are invalid browser names/versions specified in the POM ignore them and just download the valid ones.
     *
     * @parameter default-value="false"
     */
    protected boolean ignoreInvalidVersionsMapEntries;

    private RepositoryParser searchMap;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info(" ");
        getLog().info("-------------------------------------------------------");
        getLog().info(" DOWNLOADING SELENIUM STAND-ALONE EXECUTABLES...");
        getLog().info("-------------------------------------------------------");
        getLog().info(" ");
        if (this.getVersions.size() == 0) this.getLatestVersions = true;
        RepositoryHandler filesToDownload = new RepositoryHandler(this.getVersions, this.getLatestVersions, this.xmlFileMap, this.ignoreInvalidVersionsMapEntries);
        try {
            DownloadHandler standaloneExecutableDownloader = new DownloadHandler(filesToDownload.parseRequiredFiles(), this.xmlFileMap, this.rootStandaloneServerDirectory);
            standaloneExecutableDownloader.getStandaloneExecutables();
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to download all of the standalone executibles: " + e.getLocalizedMessage());
        }
        getLog().info(" ");
        getLog().info("-------------------------------------------------------");
        getLog().info(" SELENIUM STAND-ALONE EXECUTABLES DOWNLOAD COMPLETE");
        getLog().info("-------------------------------------------------------");
        getLog().info(" ");
    }


}
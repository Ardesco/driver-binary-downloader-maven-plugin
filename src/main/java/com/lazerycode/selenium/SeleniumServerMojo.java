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

    /**
     * If there are invalid versions specified in the POM ignore them and just download the valid ones.
     *
     * @parameter default-value="false"
     */
    protected boolean ignoreInvalidVersionsRequested;

    private RepositoryParser searchMap;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info(" ");
        getLog().info("-------------------------------------------------------");
        getLog().info(" DOWNLOADING SELENIUM STAND-ALONE EXECUTABLES...");
        getLog().info("-------------------------------------------------------");
        getLog().info(" ");
        if (this.getVersions.size() == 0) this.getLatestVersions = true;
        RepositoryHandler filesToDownload = new RepositoryHandler(this.getVersions, this.getLatestVersions, this.xmlFileMap, this.ignoreInvalidVersionsRequested);
        filesToDownload.parseRequiredFiles();
        //TODO go and get the files
        getLog().info(" ");
        getLog().info("-------------------------------------------------------");
        getLog().info(" SELENIUM STAND-ALONE EXECUTABLES DOWNLOAD COMPLETE");
        getLog().info("-------------------------------------------------------");
        getLog().info(" ");
    }


}
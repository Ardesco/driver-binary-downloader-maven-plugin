package com.lazerycode.selenium;

import com.lazerycode.selenium.configuration.BitRate;
import com.lazerycode.selenium.download.DownloadHandler;
import com.lazerycode.selenium.repository.RepositoryHandler;
import com.lazerycode.selenium.repository.RepositoryParser;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
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
     * Directory where downloaded zip files will be saved
     *
     * @parameter
     */
    protected File downloadedZipFileDirectory;

    /**
     * Absolute path to the XML RepositoryMap
     *
     * @parameter default-value="${project.basedir}/src/main/resources/RepositoryMap.xml"
     */
    protected File xmlFileMap;

    /**
     * The Operating systems you would like to download standalone executables for.
     *
     * @parameter default-value="${operatingSystems}"
     */
    protected OperatingSystem operatingSystems;

    /**
     * The bit rate of the standalone executables you would like to download.
     *
     * @parameter default-value="${bitRate}"
     */
    protected BitRate bitRate;

    /**
     * Get the highest version of each driver in RepositoryMap.xml
     *
     * @parameter default-value="false"
     */
    protected boolean getLatestVersions;

    /**
     * A map of driver standalone versions to download eg:
     * <p/>
     * <googlechrome>19</googlechrome>
     * <internetexplorer>2.21.0</internetexplorer>
     * <p/>
     * Unrecognised browser names/versions will cause an exception to be thrown
     *
     * @parameter
     */
    protected Map<String, String> getVersions;

    /**
     * Number of times to retry the file download of each executable
     *
     * @parameter default-value="1"
     */
    protected int fileDownloadRetryAttempts;

    /**
     * the number of milliseconds until this method will timeout if no connection could be established to remote file location.
     * Defaults to 15000 (15 Seconds)
     *
     * @parameter default-value="15000"
     */
    protected int fileDownloadConnectTimeout;

    /**
     * the number of milliseconds until this method will timeout if if no data could be read from remote file location.
     * Defaults to 15000 (15 Seconds)
     *
     * @parameter default-value="15000"
     */
    protected int fileDownloadReadTimeout;

    /**
     * If there are invalid browser names/versions specified in the POM ignore them and just download the valid ones.
     *
     * @parameter default-value="false"
     */
    protected boolean ignoreInvalidVersionsMapEntries;

    private RepositoryParser searchMap;
    private static final Logger LOG = Logger.getLogger(SeleniumServerMojo.class);

    public void execute() throws MojoExecutionException, MojoFailureException {
        BasicConfigurator.configure(new MavenLoggerLog4jBridge(getLog()));
        LOG.info(" ");
        LOG.info("-------------------------------------------------------");
        LOG.info(" DOWNLOADING SELENIUM STAND-ALONE EXECUTABLES...");
        LOG.info("-------------------------------------------------------");
        LOG.info(" ");
        if (this.getVersions.size() == 0) this.getLatestVersions = true;
        RepositoryHandler filesToDownload = new RepositoryHandler(this.getVersions, this.getLatestVersions, this.xmlFileMap, this.ignoreInvalidVersionsMapEntries);
        try {
            DownloadHandler standaloneExecutableDownloader = new DownloadHandler(filesToDownload.parseRequiredBrowserAndVersion(), this.xmlFileMap, this.rootStandaloneServerDirectory, this.downloadedZipFileDirectory, this.fileDownloadRetryAttempts, this.bitRate, this.operatingSystems, this.fileDownloadConnectTimeout, this.fileDownloadReadTimeout);
            standaloneExecutableDownloader.getStandaloneExecutables();
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to download all of the standalone executibles: " + e.getLocalizedMessage());
        }
        LOG.info(" ");
        LOG.info("-------------------------------------------------------");
        LOG.info(" SELENIUM STAND-ALONE EXECUTABLES DOWNLOAD COMPLETE");
        LOG.info("-------------------------------------------------------");
        LOG.info(" ");
    }


}
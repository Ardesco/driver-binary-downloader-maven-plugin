package com.lazerycode.selenium;

import com.lazerycode.selenium.download.DownloadHandler;
import com.lazerycode.selenium.repository.RepositoryParser;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
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
     * <h3>Root directory where the standalone server file structure will be created and files will be saved</h3>
     * <p>&lt;rootStandaloneServerDirectory&gt;${project.basedir}/src/main/resources/standalone_executable_root&lt;/rootStandaloneServerDirectory&gt;</p>
     *
     * @parameter default-value="${project.basedir}/selenium_standalone"
     */
    protected File rootStandaloneServerDirectory;

    /**
     * <h3>Directory where downloaded standalone executable zip files will be saved</h3>
     * <p>&lt;downloadedZipFileDirectory&gt;${project.basedir}/src/main/resources/downloaded_zip_files&lt;/downloadedZipFileDirectory&gt;</p>
     *
     * @parameter default-value="${project.basedir}/selenium_standalone_zips"
     */
    protected File downloadedZipFileDirectory;

    /**
     * <h3>Absolute path to the XML RepositoryMap</h3>
     * <p>&lt;xmlRepositoryMap&gt;${project.basedir}/src/main/resources/RepositoryMap.xml&lt;/xmlRepositoryMap&gt;</p>
     *
     * @parameter default-value=null
     */
    protected File customRepositoryMap;

    /**
     * <h3>The Operating systems you would like to download a standalone executable for</h3>
     * <p/>
     * &lt;operatingSystems&gt;
     * &lt;windows&gt;true&lt;/windows&gt;
     * &lt;linux&gt;true&lt;/linux&gt;
     * &lt;osx&gt;true&lt;/osx&gt;
     * &lt;/operatingSystems&gt;
     * <p/>
     * <p>Unknown operating systems will cause an error to be thrown, only use the options shown above.</p>
     * <p><strong>Default:</strong>All operating systems.</p>
     *
     * @parameter default-value=null
     */
    protected Map<String, Boolean> operatingSystems;

    /**
     * <h3>Download 32 bit standalone executable</h3>.
     * <p>&lt;thirtyTwoBitBinaries&gt;true&lt;/thirtyTwoBitBinaries&gt;</p>
     *
     * @parameter default-value="true"
     */
    protected boolean thirtyTwoBitBinaries;

    /**
     * <h3>Download 64 bit standalone executable</h3>
     * <p>&lt;sixtyFourBitBinaries&gt;true&lt;/sixtyFourBitBinaries&gt;</p>
     *
     * @parameter default-value="true"
     */
    protected boolean sixtyFourBitBinaries;

    /**
     * <h3>Only get the latest version of each standalone executable in RepositoryMap.xml</h3>
     * <p>&lt;onlyGetLatestVersions&gt;true&lt;/onlyGetLatestVersions&gt;</p>
     * <p>If set to false this will download all versions specified in the RepositoryMap.xml</p>
     * <p><strong>This will be ignored if specific executable versions have been specified</strong></p>
     *
     * @parameter default-value="true"
     */
    protected boolean onlyGetLatestVersions;

    /**
     * <h3>A map of driver standalone versions to download</h3>
     * <p/>
     * &lt;getSpecificExecutableVersions&gt;
     * &lt;googlechrome&gt;19&lt;/googlechrome&gt;
     * &lt;internetexplorer&gt;2.21.0&lt;/internetexplorer&gt;
     * &lt;/getSpecificExecutableVersions&gt;
     * <p/>
     * <p>Unrecognised browser names/versions will cause an exception to be thrown</p>
     * <p>If this is not specified </p>
     *
     * @parameter
     */
    protected Map<String, String> getSpecificExecutableVersions;

    /**
     * <h3>Number of times to retry the file download of each executable</h3>
     * <p>&lt;fileDownloadRetryAttempts&gt;1&lt;/fileDownloadRetryAttempts&gt;</p>
     *
     * @parameter default-value="1"
     */
    protected int fileDownloadRetryAttempts;

    /**
     * <h3>the number of milliseconds until this method will timeout if no connection could be established to remote file location</h3>
     * <p>&lt;fileDownloadConnectTimeout&gt;15000&lt;/fileDownloadConnectTimeout&gt;</p>
     *
     * @parameter default-value="15000"
     */
    protected int fileDownloadConnectTimeout;

    /**
     * <h3>the number of milliseconds until this method will timeout if if no data could be read from remote file location</h3>
     * <p>&lt;fileDownloadReadTimeout&gt;15000&lt;/fileDownloadReadTimeout&gt;</p>
     *
     * @parameter default-value="15000"
     */
    protected int fileDownloadReadTimeout;

    private InputStream xmlRepositoryMap = null;
    private static final Logger LOG = Logger.getLogger(SeleniumServerMojo.class);

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        BasicConfigurator.configure(new MavenLoggerLog4jBridge(getLog()));
        LOG.info(" ");
        LOG.info("--------------------------------------------------------");
        LOG.info(" DOWNLOADING SELENIUM STAND-ALONE EXECUTABLE BINARIES...");
        LOG.info("--------------------------------------------------------");
        LOG.info(" ");

        setRepositoryMapFile();

        //TODO check the RepositoryMap.xml against an xsd to ensure it is valid
        RepositoryParser executableBinaryMapping = new RepositoryParser(
                this.xmlRepositoryMap,
                buildOSArrayList(this.operatingSystems),
                this.thirtyTwoBitBinaries,
                this.sixtyFourBitBinaries,
                this.onlyGetLatestVersions);
        if (this.getSpecificExecutableVersions != null && this.getSpecificExecutableVersions.size() > 0) {
            executableBinaryMapping.specifySpecificExecutableVersions(this.getSpecificExecutableVersions);
        }

        try {
            DownloadHandler standaloneExecutableDownloader = new DownloadHandler(
                    this.rootStandaloneServerDirectory,
                    this.downloadedZipFileDirectory,
                    this.fileDownloadRetryAttempts,
                    this.fileDownloadConnectTimeout,
                    this.fileDownloadReadTimeout,
                    executableBinaryMapping.getFilesToDownload());
            standaloneExecutableDownloader.getStandaloneExecutables();
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to download all of the standalone executables: " + e.getLocalizedMessage());
        }

        LOG.info(" ");
        LOG.info("--------------------------------------------------------");
        LOG.info(" SELENIUM STAND-ALONE EXECUTABLE DOWNLOADS COMPLETE");
        LOG.info("--------------------------------------------------------");
        LOG.info(" ");
    }

    /**
     * Build a valid list of operating systems based upon the values parsed from the POM
     *
     * @param operatingSystems
     * @return
     */
    private ArrayList<OS> buildOSArrayList(Map<String, Boolean> operatingSystems) throws MojoExecutionException {
        ArrayList<OS> operatingSystemsSelected = new ArrayList<OS>();
        if (operatingSystems == null || operatingSystems.size() < 1) {
            //Default to all Operating Systems
            for (OS selectedOS : OS.values()) {
                operatingSystemsSelected.add(selectedOS);
            }
        } else {
            for (Iterator iterator = operatingSystems.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, Boolean> os = (Map.Entry<String, Boolean>) iterator.next();
                if (os.getValue()) {
                    try {
                        operatingSystemsSelected.add(OS.valueOf(os.getKey()));
                    } catch (IllegalArgumentException iae) {
                        throw new MojoExecutionException("'" + os.getKey() + "' is not a known operating system.");
                    }
                }
            }
        }

        return operatingSystemsSelected;
    }

    /**
     * Set the RepositoryMap used to get file information.
     * If the supplied map is invalid it will default to the pre-packaged one here.
     *
     * @throws MojoFailureException
     */
    private void setRepositoryMapFile() throws MojoFailureException {
        if (this.customRepositoryMap == null || !this.customRepositoryMap.exists()) {
            LOG.info("Unable to find a custom repository map, defaulting to bundled version...");
            this.xmlRepositoryMap = this.getClass().getResourceAsStream("/RepositoryMap.xml");
        } else {
            try {
                this.xmlRepositoryMap = this.customRepositoryMap.toURI().toURL().openStream();
            } catch (IOException ioe) {
                throw new MojoFailureException(ioe.getLocalizedMessage());
            }
        }
    }

}
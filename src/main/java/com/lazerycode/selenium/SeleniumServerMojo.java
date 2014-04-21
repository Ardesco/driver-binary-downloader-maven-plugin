package com.lazerycode.selenium;

import com.lazerycode.selenium.download.DownloadHandler;
import com.lazerycode.selenium.repository.RepositoryParser;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Selenium Standalone Server Maven Plugin
 *
 * @author Mark Collin
 */

@Mojo(name = "selenium", defaultPhase = LifecyclePhase.TEST_COMPILE)
@Execute(phase = LifecyclePhase.TEST_COMPILE)
@SuppressWarnings({"UnusedDeclaration"})
public class SeleniumServerMojo extends AbstractMojo {
    /**
     * <h3>Root directory where the standalone server file structure will be created and files will be saved</h3>
     * <p>&lt;rootStandaloneServerDirectory&gt;${project.basedir}/src/main/resources/standalone_executable_root&lt;/rootStandaloneServerDirectory&gt;</p>
     */
    @Parameter(defaultValue = "${project.basedir}/selenium_standalone")
    protected File rootStandaloneServerDirectory;

    /**
     * <h3>Directory where downloaded standalone executable zip files will be saved</h3>
     * <p>&lt;downloadedZipFileDirectory&gt;${project.basedir}/src/main/resources/downloaded_zip_files&lt;/downloadedZipFileDirectory&gt;</p>
     */
    @Parameter(defaultValue = "${project.basedir}/selenium_standalone_zips")
    protected File downloadedZipFileDirectory;

    /**
     * <h3>Absolute path to the XML RepositoryMap</h3>
     * <p>&lt;xmlRepositoryMap&gt;${project.basedir}/src/main/resources/RepositoryMap.xml&lt;/xmlRepositoryMap&gt;</p>
     */
    @Parameter
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
     * <p><strong>WARNING</strong>if <em>onlyGetDriversForHostOperatingSystem</em> is true, this <strong>will</strong> be ignored!</p>
     */
    @Parameter
    protected Map<String, String> operatingSystems;

    /**
     * <h3>Download 32 bit standalone executable</h3>.
     * <p>&lt;thirtyTwoBitBinaries&gt;true&lt;/thirtyTwoBitBinaries&gt;</p>
     */
    @Parameter(defaultValue = "true")
    protected boolean thirtyTwoBitBinaries;

    /**
     * <h3>Download 64 bit standalone executable</h3>
     * <p>&lt;sixtyFourBitBinaries&gt;true&lt;/sixtyFourBitBinaries&gt;</p>
     */
    @Parameter(defaultValue = "true")
    protected boolean sixtyFourBitBinaries;

    /**
     * <h3>Only get the latest version of each standalone executable in RepositoryMap.xml</h3>
     * <p>&lt;onlyGetLatestVersions&gt;true&lt;/onlyGetLatestVersions&gt;</p>
     * <p>If set to false this will download all versions specified in the RepositoryMap.xml</p>
     * <p><strong>This will be ignored if specific executable versions have been specified</strong></p>
     */
    @Parameter(defaultValue = "true")
    protected boolean onlyGetLatestVersions;

    /**
     * <h3>Only get drivers that are compatible with the operating system running the plugin</h3>
     * <p>&lt;onlyGetDriversForHostOperatingSystem&gt;true&lt;/onlyGetDriversForHostOperatingSystem&gt;</p>
     * <p>If set to false this will download binary executables for all operating systems</p>
     */
    @Parameter(defaultValue = "true")
    protected boolean onlyGetDriversForHostOperatingSystem;

    /**
     * <h3>A map of driver standalone versions to download</h3>
     * <p>
     * &lt;getSpecificExecutableVersions&gt;
     * &lt;googlechrome&gt;19&lt;/googlechrome&gt;
     * &lt;internetexplorer&gt;2.21.0&lt;/internetexplorer&gt;
     * &lt;/getSpecificExecutableVersions&gt;
     * <p/>
     * <p>Unrecognised browser names/versions will be ignored by default</p>
     */
    @Parameter
    protected Map<String, String> getSpecificExecutableVersions;

    /**
     * <h3>Throw an exception if any of the specified standalone versions do not exist in the repository map</h3>
     * <p>
     * &lt;throwExceptionIfSpecifiedVersionIsNotFound&gt;false&lt;/throwExceptionIfSpecifiedVersionIsNotFound&gt;
     * </p>
     * <p>This will cause a MojoFailureException to be thrown if any specific executable versions that have been specified do not exist in the RepositoryMap.xml</p>
     */
    @Parameter(defaultValue = "false")
    protected boolean throwExceptionIfSpecifiedVersionIsNotFound;

    /**
     * <h3>Number of times to retry the file download of each executable</h3>
     * <p>&lt;fileDownloadRetryAttempts&gt;1&lt;/fileDownloadRetryAttempts&gt;</p>
     */
    @Parameter(defaultValue = "1")
    protected int fileDownloadRetryAttempts;

    /**
     * <h3>the number of milliseconds until this method will timeout if no connection could be established to remote file location</h3>
     * <p>&lt;fileDownloadConnectTimeout&gt;15000&lt;/fileDownloadConnectTimeout&gt;</p>
     */
    @Parameter(defaultValue = "15000")
    protected int fileDownloadConnectTimeout;

    /**
     * <h3>the number of milliseconds until this method will timeout if if no data could be read from remote file location</h3>
     * <p>&lt;fileDownloadReadTimeout&gt;15000&lt;/fileDownloadReadTimeout&gt;</p>
     */
    @Parameter(defaultValue = "15000")
    protected int fileDownloadReadTimeout;

    /**
     * <h3>Force the plugin to overwrite any files that have already been extracted from a valid zip file</h3>
     * <p>&lt;overwriteFilesThatExist&gt;false&lt;/overwriteFilesThatExist&gt;</p>
     * <p>crc checks are not performed on files that have been extracted from valid zips, they are assumed to be valid.
     * This will force the plugin to extract everything from valid zips again and overwrite any existing files.</p>
     * <p>This does not clean out old files, it only writes new files over the top of existing ones.</p>
     */
    @Parameter(defaultValue = "false")
    protected boolean overwriteFilesThatExist;

    /**
     * <h3>Enable the file hash check for downloaded files</h3>
     * <p>&lt;checkFileHashes&gt;false&lt;/checkFileHashes&gt;</p>
     * <p>Setting this to false will skip all hash checks on downloaded files, <strong>this is not recommended</strong>.</p>
     * <p>If you do not check the file hash there is no guarantee that the downloaded file is the correct file</p>
     */
    @Parameter(defaultValue = "true")
    protected boolean checkFileHashes;

    protected InputStream xmlRepositoryMap = null;
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

        if (null == this.operatingSystems || this.operatingSystems.size() < 1) {
            this.onlyGetDriversForHostOperatingSystem = true;
        }

        Map<String, String> selectedOperatingSystems = new HashMap<String, String>();
        LOG.info(" Only get drivers for current Operating System: " + this.onlyGetDriversForHostOperatingSystem);
        if (this.onlyGetDriversForHostOperatingSystem) {
            String currentOS = System.getProperty("os.name").toLowerCase();
            if (currentOS.startsWith("win")) {
                selectedOperatingSystems.put("windows", "true");
            } else if (currentOS.startsWith("mac")) {
                selectedOperatingSystems.put("osx", "true");
            } else {
                selectedOperatingSystems.put("linux", "true");
            }
        } else {
            selectedOperatingSystems = this.operatingSystems;
        }

        ArrayList<OS> osList = buildOSArrayList(selectedOperatingSystems);

        if (null == osList || osList.size() == 0) {
            LOG.info(" ");
            LOG.info(" All operating systems have been excluded, check your <operatingSystems> configuration in your POM!");
            LOG.info(" ");
            LOG.info("--------------------------------------------------------");
            LOG.info(" SELENIUM STAND-ALONE EXECUTABLE DOWNLOAD ABORTED");
            LOG.info("--------------------------------------------------------");
            LOG.info(" ");
            return;
        }

        RepositoryParser executableBinaryMapping = new RepositoryParser(
                this.xmlRepositoryMap,
                osList,
                this.thirtyTwoBitBinaries,
                this.sixtyFourBitBinaries,
                this.onlyGetLatestVersions,
                this.throwExceptionIfSpecifiedVersionIsNotFound);
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
                    executableBinaryMapping.getFilesToDownload(),
                    this.overwriteFilesThatExist,
                    this.checkFileHashes);
            standaloneExecutableDownloader.getStandaloneExecutableFiles();
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
     * @param operatingSystems Operating systems to download Driver Binaries for
     * @return ArrayList<OS>
     */
    private ArrayList<OS> buildOSArrayList(Map<String, String> operatingSystems) throws MojoExecutionException {
        ArrayList<OS> operatingSystemsSelected = new ArrayList<OS>();
        if (operatingSystems == null || operatingSystems.size() < 1) {
            //Default to all Operating Systems
            Collections.addAll(operatingSystemsSelected, OS.values());
        } else {
            for (Map.Entry<String, String> os : operatingSystems.entrySet()) {
                if (os.getValue().toLowerCase().equals("true")) {
                    try {
                        operatingSystemsSelected.add(OS.valueOf(os.getKey().toUpperCase()));
                    } catch (IllegalArgumentException iae) {
                        throw new MojoExecutionException("'" + os.getKey().toUpperCase() + "' is not a known operating system.");
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
     * @throws MojoExecutionException
     */
    private void setRepositoryMapFile() throws MojoExecutionException {
        if (this.customRepositoryMap == null || !this.customRepositoryMap.exists()) {
            if (this.customRepositoryMap != null) {
                LOG.info("Unable to access the specified custom repository map, defaulting to bundled version...");
                LOG.info(" ");
            }
            this.xmlRepositoryMap = this.getClass().getResourceAsStream("/RepositoryMap.xml");
        } else {
            checkRepositoryMapIsValid();
            try {
                this.xmlRepositoryMap = this.customRepositoryMap.toURI().toURL().openStream();
            } catch (IOException ioe) {
                throw new MojoExecutionException(ioe.getLocalizedMessage());
            }
        }
    }

    /**
     * Validate any custom repository maps that are supplied against the xsd.
     * Throw an error and stop if it is not valid.
     * Assume it doesn't exist if we get an IOError and fall back to default file.
     *
     * @throws MojoExecutionException
     */
    protected void checkRepositoryMapIsValid() throws MojoExecutionException {
        URL schemaFile = this.getClass().getResource("/RepositoryMap.xsd");
        Source xmlFile = new StreamSource(this.customRepositoryMap);
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            Schema schema = schemaFactory.newSchema(schemaFile);
            Validator validator = schema.newValidator();
            validator.validate(xmlFile);
            LOG.info(" " + xmlFile.getSystemId() + " is valid");
            LOG.info(" ");
        } catch (SAXException saxe) {
            throw new MojoExecutionException(this.customRepositoryMap.getName() + " is not valid: " + saxe.getLocalizedMessage());
        } catch (IOException ioe) {
            //Assume it doesn't exist, set to null so that we default to packaged version
            this.customRepositoryMap = null;
        }
    }

}
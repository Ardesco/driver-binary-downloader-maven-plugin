package com.lazerycode.selenium;

import com.lazerycode.selenium.download.DownloadHandler;
import com.lazerycode.selenium.repository.RepositoryParser;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
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
import java.util.Iterator;
import java.util.Map;

/**
 * Selenium Standalone Server Maven Plugin
 *
 * @author Mark Collin
 * @goal selenium
 * @phase test-compile
 * @execute phase="test-compile"
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

    /**
     * <h3>Force the plugin to overwrite any files that have already been extracted from a valid zip file</h3>
     * <p>&lt;overwriteFilesThatExist&gt;false&lt;/overwriteFilesThatExist&gt;</p>
     * <p>crc checks are not performed on files that have been extracted from valid zips, they are assumed to be valid.
     * This will force the plugin to extract everything from valid zips again and overwrite any existing files.</p>
     * <p>This does not clean out old files, it only writes new files over the top of existing ones.</p>
     *
     * @parameter default-value="false"
     */
    protected boolean overwriteFilesThatExist;

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
        CheckRepositoryMapIsValid();
        SetRepositoryMapFile();

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
                    executableBinaryMapping.getFilesToDownload(),
                    this.overwriteFilesThatExist);
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
     * @throws MojoExecutionException
     */
    private void SetRepositoryMapFile() throws MojoExecutionException {
        if (this.customRepositoryMap == null || !this.customRepositoryMap.exists()) {
            LOG.info("Unable to access the specified custom repository map, defaulting to bundled version...");
            this.xmlRepositoryMap = this.getClass().getResourceAsStream("/RepositoryMap.xml");
        } else {
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
    private void CheckRepositoryMapIsValid() throws MojoExecutionException {
        URL schemaFile = this.getClass().getResource("/RepositoryMap.xsd");
        Source xmlFile = new StreamSource(this.customRepositoryMap);
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            Schema schema = schemaFactory.newSchema(schemaFile);
            Validator validator = schema.newValidator();
            validator.validate(xmlFile);
            LOG.info(xmlFile.getSystemId() + " is valid");
        } catch (SAXException saxe) {
            throw new MojoExecutionException(this.customRepositoryMap.getName() + " is not valid: " + saxe.getLocalizedMessage());
        } catch (IOException ioe) {
            //Assume it doesn't exist, set to null so that we default to packaged version
            this.customRepositoryMap = null;
        }
    }

}
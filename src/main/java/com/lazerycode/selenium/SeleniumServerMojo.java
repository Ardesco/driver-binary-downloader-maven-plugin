package com.lazerycode.selenium;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;

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
     * Disable running an MD5 check on downloaded standalone server binaries
     *
     * @parameter default-value="false"
     */
    protected boolean disableMD5HashCheck;

    /**
     * Always download the server binaries, even if you already have them.
     * (If MD5 Checks are disabled it will only use the filename to determine if the stadalone server binary has already been downloaded)
     *
     * @parameter default-value="false"
     */
    protected boolean alwaysUpdate;
    /**
     * The version of Selenium being currently used
     * (It's assumed that the Selenium version is related to standalone server binary version)
     */
    protected String seleniumVersion;

    private RepositoryParser searchMap;

    public void execute() throws MojoExecutionException, MojoFailureException {

    }
}

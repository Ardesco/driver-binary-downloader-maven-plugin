package com.lazerycode.selenium;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;

public class SeleniumServerMojo extends AbstractMojo {
    /**
     * Root directory to create the standalone server file structure
     *
     * @parameter
     */
    protected File rootStandaloneServerDirectory;

    public void execute() throws MojoExecutionException, MojoFailureException {

    }
}

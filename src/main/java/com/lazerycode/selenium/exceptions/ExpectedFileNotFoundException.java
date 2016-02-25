package com.lazerycode.selenium.exceptions;

import org.apache.maven.plugin.MojoFailureException;

public class ExpectedFileNotFoundException extends MojoFailureException {
    public ExpectedFileNotFoundException(String message) {
        super(message);
    }
}

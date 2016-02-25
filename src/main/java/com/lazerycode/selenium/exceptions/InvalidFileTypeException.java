package com.lazerycode.selenium.exceptions;

import org.apache.maven.plugin.MojoFailureException;

public class InvalidFileTypeException extends MojoFailureException {
    public InvalidFileTypeException(String message) {
        super(message);
    }
}

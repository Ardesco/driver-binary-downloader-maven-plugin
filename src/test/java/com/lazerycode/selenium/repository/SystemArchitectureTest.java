package com.lazerycode.selenium.repository;

import org.junit.Test;

import static com.lazerycode.selenium.repository.SystemArchitecture.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

public class SystemArchitectureTest {

    @Test
    public void willReturn64BitArchitectureForAMD64() {

        SystemArchitecture systemArchitecture = getSystemArchitecture("amd64");

        assertThat(systemArchitecture,
                is(equalTo(ARCHITECTURE_64_BIT)));
    }

    @Test
    public void willReturn64BitArchitectureForX8664() {

        SystemArchitecture systemArchitecture = getSystemArchitecture("x86_64");

        assertThat(systemArchitecture,
                is(equalTo(ARCHITECTURE_64_BIT)));
    }

    @Test
    public void willReturn32BitArchitectureForAnythingElse() {

        SystemArchitecture systemArchitecture = getSystemArchitecture("foo");

        assertThat(systemArchitecture,
                is(equalTo(ARCHITECTURE_32_BIT)));
    }

    @Test
    public void willReturnAValidArchitectureUsingGetCurrentSystemArchitecture() {
        assertThat(getCurrentSystemArcitecture(),
                instanceOf(SystemArchitecture.class));
    }
}
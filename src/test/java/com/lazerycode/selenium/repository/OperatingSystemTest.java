package com.lazerycode.selenium.repository;

import org.junit.Test;

import java.util.HashSet;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;


public class OperatingSystemTest {

    @Test(expected = IllegalArgumentException.class)
    public void willThrowAnIllegalArgumentExceptionIfOperatingSystemIsNotValid() {
        OperatingSystem.getOperatingSystem("foo");
    }

    @Test
    public void willReturnTheCorrectOperatingSystemEnumType() {
        OperatingSystem operatingSystem = OperatingSystem.getOperatingSystem("windows");

        assertThat(operatingSystem,
                is(equalTo(OperatingSystem.WINDOWS)));
    }

    @Test
    public void willReturnAValidArrayListBasedOnCurrentOperatingSystemType() {
        HashSet<OperatingSystem> currentOperatingSystem = OperatingSystem.getCurrentOperatingSystemAsAHashSet();

        assertThat(currentOperatingSystem.size(),
                is(equalTo(1)));
    }

    @Test
    public void willReturnAValidOperatingSystemBasedOnCurrentOperatingSystemType() {
        assertThat(OperatingSystem.getCurrentOperatingSystem(),
                instanceOf(OperatingSystem.class));
    }
}

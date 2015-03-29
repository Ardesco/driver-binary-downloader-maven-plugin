package com.lazerycode.selenium.repository;

import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
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
        ArrayList<OperatingSystem> currentOperatingSystem = OperatingSystem.getCurrentOperatingSystemAsAnArrayList();

        assertThat(currentOperatingSystem.size(),
                is(equalTo(1)));
    }
}

package com.lazerycode.selenium.repository;

import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import javax.xml.xpath.XPathExpressionException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;

import static com.lazerycode.selenium.repository.FileRepository.buildDownloadableFileRepository;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class FileRepositoryTest {

    @Test
    public void listOfStuffReturnedWhenPassingInARepositoryMapWithout32Or64OrArm() throws XPathExpressionException, MojoFailureException, JAXBException, MalformedURLException {
        InputStream xmlRepositoryMap = this.getClass().getResourceAsStream("/TestRepoMap.xml");
        XMLParser parser = new XMLParser(xmlRepositoryMap, new HashSet<OperatingSystem>(), new HashMap<String, String>(), false, false);
        DriverMap driverMap = buildDownloadableFileRepository(parser.getAllNodesInScope(), false, false, false);

        assertThat(driverMap.repository.size(),
                is(equalTo(0)));
    }

    @Test
    public void listOfStuffReturnedWhenPassingInARepositoryMapWith32And64ButNotArm() throws XPathExpressionException, MojoFailureException, JAXBException, MalformedURLException {
        InputStream xmlRepositoryMap = this.getClass().getResourceAsStream("/TestRepoMap.xml");
        XMLParser parser = new XMLParser(xmlRepositoryMap, new HashSet<OperatingSystem>(), new HashMap<String, String>(), false, false);
        DriverMap driverMap = buildDownloadableFileRepository(parser.getAllNodesInScope(), true, true, false);

        assertThat(driverMap.repository.size(),
                is(equalTo(7)));
    }

    @Test
    public void listOfStuffReturnedWhenPassingInARepositoryMapWith32ButNot64() throws XPathExpressionException, MojoFailureException, JAXBException, MalformedURLException {
        InputStream xmlRepositoryMap = this.getClass().getResourceAsStream("/TestRepoMap.xml");
        XMLParser parser = new XMLParser(xmlRepositoryMap, new HashSet<OperatingSystem>(), new HashMap<String, String>(), false, false);
        DriverMap driverMap = buildDownloadableFileRepository(parser.getAllNodesInScope(), true, false, false);

        assertThat(driverMap.repository.size(),
                is(equalTo(4)));
    }

    @Test
    public void listOfStuffReturnedWhenPassingInARepositoryMapWith64ButNot32() throws XPathExpressionException, MojoFailureException, JAXBException, MalformedURLException {
        InputStream xmlRepositoryMap = this.getClass().getResourceAsStream("/TestRepoMap.xml");
        XMLParser parser = new XMLParser(xmlRepositoryMap, new HashSet<OperatingSystem>(), new HashMap<String, String>(), false, false);
        DriverMap driverMap = buildDownloadableFileRepository(parser.getAllNodesInScope(), false, true, false);

        assertThat(driverMap.repository.size(),
                is(equalTo(3)));
    }

    @Test
    public void listOfStuffReturnedWhenPassingInARepositoryMapWith64AndArmButNot32() throws XPathExpressionException, MojoFailureException, JAXBException, MalformedURLException {
        InputStream xmlRepositoryMap = this.getClass().getResourceAsStream("/TestRepoMap.xml");
        XMLParser parser = new XMLParser(xmlRepositoryMap, new HashSet<OperatingSystem>(), new HashMap<String, String>(), false, false);
        DriverMap driverMap = buildDownloadableFileRepository(parser.getAllNodesInScope(), false, true, true);

        assertThat(driverMap.repository.size(),
                is(equalTo(4)));
    }
}

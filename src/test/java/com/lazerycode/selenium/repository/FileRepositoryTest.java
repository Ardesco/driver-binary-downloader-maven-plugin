package com.lazerycode.selenium.repository;

import com.lazerycode.selenium.OSType;
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
    public void listOfStuffReturnedWhenPassingInARepositoryMap() throws XPathExpressionException, MojoFailureException, JAXBException, MalformedURLException {
        InputStream xmlRepositoryMap = this.getClass().getResourceAsStream("/TestRepoMap.xml");
        XMLParser parser = new XMLParser(xmlRepositoryMap, new HashSet<OSType>(), new HashMap<String, String>(), false, false);
        DriverMap driverMap = buildDownloadableFileRepository(parser.getAllNodesInScope());

        assertThat(driverMap.repository.size(),
                is(equalTo(8)));
    }
}

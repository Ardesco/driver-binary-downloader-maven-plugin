package com.lazerycode.selenium.repository;

import org.junit.Test;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.lazerycode.selenium.repository.OperatingSystem.LINUX;
import static com.lazerycode.selenium.repository.OperatingSystem.WINDOWS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.core.Is.is;

public class XMLParserTest {

    @Test
    public void returnsNoOperatingSystemsByDefault() {
        Set<OperatingSystem> none = new HashSet<OperatingSystem>();
        XMLParser parser = new XMLParser(null, none, null, false, false);

        assertThat(parser.operatingSystemSelector(),
                is(equalTo("")));
    }

    @Test
    public void returnsASingleOperatingSystemWhenAListWithOnEntryIsSupplied() {
        Set<OperatingSystem> one = new HashSet<OperatingSystem>();
        one.add(WINDOWS);
        XMLParser parser = new XMLParser(null, one, null, false, false);

        assertThat(parser.operatingSystemSelector(),
                is(equalTo("[parent::windows]")));
    }

    @Test
    public void returnsACorrectlyFormattedStringWhenMultipleOperatingSystemsAreSupplied() {
        Set<OperatingSystem> two = new HashSet<OperatingSystem>();
        two.add(WINDOWS);
        two.add(LINUX);
        XMLParser parser = new XMLParser(null, two, null, false, false);

        assertThat(parser.operatingSystemSelector(),
                isOneOf("[parent::windows|parent::linux]", "[parent::linux|parent::windows]")
        );
    }

    @Test
    public void returnsNothingIfNoSpecificVersionsSpecified() {
        Map<String, String> versionsToFind = new HashMap<String, String>();
        XMLParser parser = new XMLParser(null, null, versionsToFind, false, false);

        assertThat(parser.driverVersionSelector(),
                is(equalTo("")));
    }

    @Test
    public void returnsASingleVersion() {
        Map<String, String> versionsToFind = new HashMap<String, String>();
        versionsToFind.put("googlechrome", "2.14");
        XMLParser parser = new XMLParser(null, null, versionsToFind, false, false);

        assertThat(parser.driverVersionSelector(),
                is(equalTo("[(parent::*[@id='googlechrome'] and @id='2.14')]")));
    }

    @Test
    public void returnsMultipleVersions() {
        Map<String, String> versionsToFind = new HashMap<String, String>();
        versionsToFind.put("googlechrome", "2.14");
        versionsToFind.put("internetexplorer", "2.45.0");
        XMLParser parser = new XMLParser(null, null, versionsToFind, false, false);

        assertThat(parser.driverVersionSelector(),
                isOneOf("[(parent::*[@id='googlechrome'] and @id='2.14') or (parent::*[@id='internetexplorer'] and @id='2.45.0')]",
                        "[(parent::*[@id='internetexplorer'] and @id='2.45.0') or (parent::*[@id='googlechrome'] and @id='2.14')]")
        );
    }

    @Test
    public void returnsEmptyStringWhenNoBitratesSet() {
        XMLParser parser = new XMLParser(null, null, null, false, false);

        assertThat(parser.calculateBitrate(),
                is(equalTo("")));
    }

    @Test
    public void returnsCorrectStringWhenThirtyTwoBitIsTrue() {
        XMLParser parser = new XMLParser(null, null, null, true, false);

        assertThat(parser.calculateBitrate(),
                is(equalTo("[@thirtytwobit='true']")));
    }

    @Test
    public void returnsCorrectStringWhenSixtyFourBitIsTrue() {
        XMLParser parser = new XMLParser(null, null, null, false, true);

        assertThat(parser.calculateBitrate(),
                is(equalTo("[@sixtyfourbit='true']")));
    }

    @Test
    public void returnsCorrectStringWhenBothThirtyTwoBitAndSixtyFourBitAreTrue() {
        XMLParser parser = new XMLParser(null, null, null, true, true);

        assertThat(parser.calculateBitrate(),
                is(equalTo("[@thirtytwobit='true' or @sixtyfourbit='true']")));
    }

    @Test
    public void listWithAllNodesReturnedWhenPassingInARepositoryMap() throws XPathExpressionException {
        InputStream xmlRepositoryMap = this.getClass().getResourceAsStream("/TestRepoMap.xml");
        XMLParser parser = new XMLParser(xmlRepositoryMap, new HashSet<OperatingSystem>(), new HashMap<String, String>(), false, false);
        NodeList nodes = parser.getAllNodesInScope();

        assertThat(nodes.getLength(),
                is(equalTo(12)));
    }

    @Test
    public void returnsSpecificNodeLocatorWhenOptionsAreSpecified() throws XPathExpressionException {
        Set<OperatingSystem> one = new HashSet<OperatingSystem>();
        one.add(WINDOWS);
        Map<String, String> versionsToFind = new HashMap<String, String>();
        versionsToFind.put("googlechrome", "22");
        InputStream xmlRepositoryMap = this.getClass().getResourceAsStream("/TestRepoMap.xml");
        XMLParser parser = new XMLParser(xmlRepositoryMap, one, versionsToFind, true, true);
        NodeList nodes = parser.getAllNodesInScope();

        assertThat(nodes.getLength(),
                is(equalTo(1)));
    }
}
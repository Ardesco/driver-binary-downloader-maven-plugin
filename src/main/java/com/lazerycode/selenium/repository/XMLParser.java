package com.lazerycode.selenium.repository;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.*;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class XMLParser {

    private static XPathFactory factory = XPathFactory.newInstance();
    private static XPath xpath = factory.newXPath();
    final InputSource repositoryMap;
    final Set<OperatingSystem> operatingSystems;
    final Map<String, String> driverVersions;
    final boolean thirtyTwoBit;
    final boolean sixtyFourBit;


    public XMLParser(InputStream repositoryMap, Set<OperatingSystem> operatingSystems, Map<String, String> driverVersions, boolean thirtyTwoBit, boolean sixtyFourBit) {
        this.repositoryMap = new InputSource(repositoryMap);
        this.operatingSystems = operatingSystems;
        this.driverVersions = driverVersions;
        this.thirtyTwoBit = thirtyTwoBit;
        this.sixtyFourBit = sixtyFourBit;
    }

    protected String operatingSystemSelector() {
        if (operatingSystems.size() == 0) {
            return "";
        }
        StringBuilder operatingSystemsSelector = new StringBuilder();
        operatingSystemsSelector.append("[");
        for (Iterator<OperatingSystem> iterator = operatingSystems.iterator(); iterator.hasNext(); ) {
            String operatingSystem = iterator.next().toString().toLowerCase();
            operatingSystemsSelector.append("parent::").append(operatingSystem);
            if (iterator.hasNext()) {
                operatingSystemsSelector.append("|");
            }
        }
        operatingSystemsSelector.append("]");

        return operatingSystemsSelector.toString();
    }

    protected String driverVersionSelector() {
        if (null == driverVersions || driverVersions.size() == 0) {
            return "";
        }
        StringBuilder versionSelector = new StringBuilder();
        versionSelector.append("[");
        Iterator it = driverVersions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry specificDriver = (Map.Entry) it.next();
            versionSelector.append("(parent::*[@id='");
            versionSelector.append(specificDriver.getKey());
            versionSelector.append("'] and @id='");
            versionSelector.append(specificDriver.getValue());
            versionSelector.append("')");
            if (it.hasNext()) {
                versionSelector.append(" or ");
            }
        }
        versionSelector.append("]");

        return versionSelector.toString();
    }

    protected String calculateBitrate() {
        if (thirtyTwoBit && sixtyFourBit) {
            return "[@thirtytwobit='true' or @sixtyfourbit='true']";
        } else if (thirtyTwoBit) {
            return "[@thirtytwobit='true']";
        } else if (sixtyFourBit) {
            return "[@sixtyfourbit='true']";
        }

        return "";
    }

    public NodeList getAllNodesInScope() throws XPathExpressionException {
        String nodeLocator = "//driver" + operatingSystemSelector() + "/version" + driverVersionSelector() + "/bitrate" + calculateBitrate();
        XPathExpression expression = xpath.compile(nodeLocator);

        return (NodeList) expression.evaluate(repositoryMap, XPathConstants.NODESET);
    }
}

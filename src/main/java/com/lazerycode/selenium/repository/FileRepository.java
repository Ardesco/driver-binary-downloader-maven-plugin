package com.lazerycode.selenium.repository;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import static com.lazerycode.selenium.repository.SystemArchitecture.*;

public class FileRepository {

    public static DriverMap buildDownloadableFileRepository(NodeList nodesFound, boolean useThirtyTwoBitBinaries, boolean useSixtyFourBitBinaries, boolean useArmBinaries) throws JAXBException {
        DriverMap driverMap = new DriverMap();
        Unmarshaller unmarshaller = JAXBContext.newInstance(DriverDetails.class).createUnmarshaller();
        unmarshaller.setEventHandler(new unmarshallingEventHandler());
        for (int nodeNumber = 0; nodeNumber < nodesFound.getLength(); nodeNumber++) {
            Node node = nodesFound.item(nodeNumber);
            String operatingSystem = node.getParentNode().getParentNode().getParentNode().getNodeName();
            String driver = node.getParentNode().getParentNode().getAttributes().getNamedItem("id").getNodeValue();
            String version = node.getParentNode().getAttributes().getNamedItem("id").getNodeValue();
            boolean thisIs64Bit = false;
            boolean thisIs32Bit = false;
            boolean thisIsArm = false;
            if (useThirtyTwoBitBinaries && node.getAttributes().getNamedItem("thirtytwobit") != null) {
                if (Boolean.valueOf(node.getAttributes().getNamedItem("thirtytwobit").getNodeValue())) {
                    thisIs32Bit = true;
                }
            }
            if (useSixtyFourBitBinaries && node.getAttributes().getNamedItem("sixtyfourbit") != null) {
                if (Boolean.valueOf(node.getAttributes().getNamedItem("sixtyfourbit").getNodeValue())) {
                    thisIs64Bit = true;
                }
            }
            if (useArmBinaries && node.getAttributes().getNamedItem("arm") != null) {
                if (Boolean.valueOf(node.getAttributes().getNamedItem("arm").getNodeValue())) {
                    thisIsArm = true;
                }
            }

            DriverDetails driverDetails = unmarshaller.unmarshal(node, DriverDetails.class).getValue();
            if (thisIs32Bit) {
                driverMap.getMapForDriverContext(DriverContext.binaryDataFor(operatingSystem, driver, ARCHITECTURE_32_BIT)).put(version, driverDetails);
            }
            if (thisIs64Bit) {
                driverMap.getMapForDriverContext(DriverContext.binaryDataFor(operatingSystem, driver, ARCHITECTURE_64_BIT)).put(version, driverDetails);
            }
            if (thisIsArm) {
                driverMap.getMapForDriverContext(DriverContext.binaryDataFor(operatingSystem, driver, ARCHITECTURE_ARM)).put(version, driverDetails);
            }
        }

        return driverMap;
    }
}

package com.lazerycode.selenium;

import nu.xom.*;

import java.io.File;
import java.io.IOException;

public class RepositoryParser {

    private Document repositoryMap;

    public RepositoryParser(File value) throws ParsingException, IOException {
        Builder parser = new Builder();
        this.repositoryMap = parser.build(value);
    }

    public SelectDriver forDriver(Driver driver, String version) {
        return new ForDriver(driver, version);
    }

    private class ForDriver implements SelectDriver {

        final Driver driver;
        final String version;

        public ForDriver(Driver driver, String version) {
            this.driver = driver;
            this.version = version;
        }

        public SelectOS andOS(Bit bit, OperatingSystem os) {
            return new AndOS(bit, os);
        }

        private class AndOS implements SelectOS {
            final OperatingSystem os;
            final Bit bit;

            public AndOS(Bit bit, OperatingSystem os) {
                this.os = os;
                this.bit = bit;
            }

            public String returnFilePath() {
                Node filePathNode = repositoryMap.query("/root/" + driver.getDriverName() + "/version[@id='" + version + "']/" + os.getOsName() + "[@" + bit.getBitValue() + "]/filelocation").get(1);
                return filePathNode.getValue();
            }

            public String fileSHA1Hash() {
                Node sha1HashNode = repositoryMap.query("/root/" + driver.getDriverName() + "/version[@id='" + version + "']/" + os.getOsName() + "[@" + bit.getBitValue() + "]/sha1hash").get(1);
                return sha1HashNode.getValue();
            }
        }

    }
}

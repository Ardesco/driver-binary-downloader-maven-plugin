selenium-standalone-server-plugin
=================================

A Maven plugin that will download the WebDriver stand alone server binaries for use in your mavenised Selenium project.

*Default Usage*

<plugins>
    <plugin>
        <groupId>com.lazerycode.selenium</groupId>
        <artifactId>driver-binary-downloader-maven-plugin</artifactId>
        <version>1.0.0</version>
        <configuration>
            <!-- root directory that downloaded driver binaries will be stored in -->
            <rootStandaloneServerDirectory>/my/location/binaries</rootStandaloneServerDirectory>
            <!-- Where you want to store downloaded zip files -->
            <downloadedZipFileDirectory>/my/location/zips</downloadedZipFileDirectory>
        </configuration>
        <executions>
            <execution>
                <goals>
                    <goal>selenium</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
</plugins>

By default the plugin will download the most recent binary specified in the RepositoryMap.xml for every driver/os/bitrate.
If you want to filter out the ones you don't need have a look at the advanced usage options below.

*Advanced Usage*

<build>
    <plugins>
        <plugin>
            <groupId>com.lazerycode.selenium</groupId>
            <artifactId>driver-binary-downloader-maven-plugin</artifactId>
            <version>1.0.0</version>
            <configuration>
                <!-- root directory that downloaded driver binaries will be stored in -->
                <rootStandaloneServerDirectory>/tmp/binaries</rootStandaloneServerDirectory>
                <!-- Where you want to store downloaded zip files -->
                <downloadedZipFileDirectory>/tmp/zips</downloadedZipFileDirectory>
                <!-- Location of a custom repository map -->
                <customRepositoryMap>/tmp/repo.xml</customRepositoryMap>
                <!-- Operating systems you want to download binaries for (Only valid options are: windows, linux, osx) -->
                <operatingSystems>
                    <windows>true</windows>
                    <linux>true</linux>
                    <osx>true</osx>
                </operatingSystems>
                <!-- Download 32bit binaries -->
                <thirtyTwoBitBinaries>true</thirtyTwoBitBinaries>
                <!-- Download 64bit binaries -->
                <sixtyFourBitBinaries>true</sixtyFourBitBinaries>
                <!-- If set to false will download every version available (Other filters will be taken into account -->
                <onlyGetLatestVersions>false</onlyGetLatestVersions>
                <!-- Provide a list of drivers and binary versions to download (this is a map so only one version can be specified per driver) -->
                <getSpecificExecutableVersions>
                    <googlechrome>18</googlechrome>
                </getSpecificExecutableVersions>
                <!-- Number of times to attempt to download each file -->
                <fileDownloadRetryAttempts>2</fileDownloadRetryAttempts>
                <!-- Number of ms to wait before timing out when trying to connect to remote server to download file -->
                <fileDownloadConnectTimeout>20000</fileDownloadConnectTimeout>
                <!-- Number of ms to wait before timing out when trying to read file from remote server -->
                <fileDownloadReadTimeout>10000</fileDownloadReadTimeout>
                <!-- Overwrite any existing binaries that have been downloaded and extracted -->
                <overwriteFilesThatExist>true</overwriteFilesThatExist>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>selenium</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>

*Custom RepositoryMap.xml*

You can supply a custom RepositoryMap.xml file to use instead of the default one, you can then use customised file locations/customised binaries.
If you decide to use a custom RepositoryMap.xml it must match the schema available at https://github.com/Ardesco/selenium-standalone-server-plugin/blob/master/src/main/resources/RepositoryMap.xsd.
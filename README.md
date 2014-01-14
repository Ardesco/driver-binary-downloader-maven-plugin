Selenium driver-binary-downloader-maven-plugin
=================================

A Maven plugin that will download the WebDriver stand alone server binaries for use in your mavenised Selenium project.

Default Usage
-----

    <plugins>
        <plugin>
            <groupId>com.lazerycode.selenium</groupId>
            <artifactId>driver-binary-downloader-maven-plugin</artifactId>
            <version>0.9.0</version>
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

Advanced Usage
-----

    <build>
        <plugins>
            <plugin>
                <groupId>com.lazerycode.selenium</groupId>
                <artifactId>driver-binary-downloader-maven-plugin</artifactId>
                <version>0.9.0</version>
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

Custom RepositoryMap.xml
-----

You __should__ supply your own RepositoryMap.xml file, if you don't supply one a default one (which will most likely be out of date) will be used instead.  Your RepositoryMap.xml must match the schema available at [Here](https://github.com/Ardesco/selenium-standalone-server-plugin/blob/master/src/main/resources/RepositoryMap.xsd).

___Below is an example RepositoryMap.xml that I will endeavour to keep up to date so that you can just copy/paste the contents into your own file.___

    <?xml version="1.0" encoding="utf-8" standalone="yes"?>
    <root>
        <windows>
            <driver id="internetexplorer">
                <version id="2.39.0">
                    <bitrate sixtyfourbit="true">
                        <filelocation>https://selenium.googlecode.com/files/IEDriverServer_x64_2.39.0.zip</filelocation>
                        <hash>c7ffa258de34d0934120b269a5af76e14a62d2d4</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                    <bitrate thirtytwobit="true">
                        <filelocation>https://selenium.googlecode.com/files/IEDriverServer_Win32_2.39.0.zip</filelocation>
                        <hash>71b8fad1dadc72a1b7e45ade3f5c3f72d4f02def</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="googlechrome">
                <version id="2.8">
                    <bitrate thirtytwobit="true" sixtyfourbit="true">
                        <filelocation>http://chromedriver.storage.googleapis.com/2.8/chromedriver_win32.zip</filelocation>
                        <hash>2e5ec89661e528bf69f717953896d85896ca64db</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="phantomjs">
                <version id="1.9.2">
                    <bitrate thirtytwobit="true" sixtyfourbit="true">
                        <filelocation>https://phantomjs.googlecode.com/files/phantomjs-1.9.2-windows.zip</filelocation>
                        <hash>5fcfb32d9df9e603a3980139026bc33d516dae01</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
        </windows>
        <linux>
            <driver id="googlechrome">
                <version id="2.8">
                    <bitrate sixtyfourbit="true">
                        <filelocation>http://chromedriver.storage.googleapis.com/2.8/chromedriver_linux64.zip</filelocation>
                        <hash>33112f4484145bd0bb8100bac7670d8c45793a4b</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                    <bitrate thirtytwobit="true">
                        <filelocation>http://chromedriver.storage.googleapis.com/2.8/chromedriver_linux32.zip</filelocation>
                        <hash>f3743b625e5466a82fb0e433611190588fa07357</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="phantomjs">
                <version id="1.9.2">
                    <bitrate sixtyfourbit="true">
                        <filelocation>https://phantomjs.googlecode.com/files/phantomjs-1.9.2-linux-x86_64.tar.bz2
                        </filelocation>
                        <hash>c78c4037d98fa893e66fc516214499c58228d2f9</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                    <bitrate thirtytwobit="true">
                        <filelocation>https://phantomjs.googlecode.com/files/phantomjs-1.9.2-linux-i686.tar.bz2
                        </filelocation>
                        <hash>9ead5dd275f79eaced61ce63dbeca58be4d7f090</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
        </linux>
        <osx>
            <driver id="googlechrome">
                <version id="2.8">
                    <bitrate thirtytwobit="true" sixtyfourbit="true">
                        <filelocation>http://chromedriver.storage.googleapis.com/2.8/chromedriver_mac32.zip</filelocation>
                        <hash>b44d4666d00531f9edc5f1e89534a789fb4ec162</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="phantomjs">
                <version id="1.9.2">
                    <bitrate thirtytwobit="true" sixtyfourbit="true">
                        <filelocation>https://phantomjs.googlecode.com/files/phantomjs-1.9.2-macosx.zip</filelocation>
                        <hash>36357dc95c0676fb4972420ad25455f49a8f3331</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
        </osx>
    </root>
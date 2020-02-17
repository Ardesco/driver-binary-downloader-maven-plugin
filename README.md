Selenium driver-binary-downloader-maven-plugin
=================================

[![Join the chat at https://gitter.im/Ardesco/selenium-standalone-server-plugin](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/Ardesco/selenium-standalone-server-plugin?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/Ardesco/driver-binary-downloader-maven-plugin.svg?branch=master)](https://travis-ci.org/Ardesco/driver-binary-downloader-maven-plugin)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.lazerycode.selenium/driver-binary-downloader-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.lazerycode.selenium/driver-binary-downloader-maven-plugin)
[![Javadoc](https://javadoc.io/badge2/com.lazerycode.selenium/driver-binary-downloader-maven-plugin/badge.svg)](http://www.javadoc.io/doc/com.lazerycode.selenium/driver-binary-downloader-maven-plugin)


A Maven plugin that will download the WebDriver stand alone server binaries for use in your mavenised Selenium project.

What's changed?  See the [Changelog](https://github.com/Ardesco/selenium-standalone-server-plugin/blob/master/CHANGELOG.md).

**This plugin now requires Java 8!**

Default Usage
-----

    <plugins>
        <plugin>
            <groupId>com.lazerycode.selenium</groupId>
            <artifactId>driver-binary-downloader-maven-plugin</artifactId>
            <version>1.0.18</version>
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
                <version>1.0.18</version>
                <configuration>
                    <!-- root directory that downloaded driver binaries will be stored in -->
                    <rootStandaloneServerDirectory>/tmp/binaries</rootStandaloneServerDirectory>
                    <!-- Where you want to store downloaded zip files -->
                    <downloadedZipFileDirectory>/tmp/zips</downloadedZipFileDirectory>
                    <!-- Location of a custom repository map -->
                    <customRepositoryMap>/tmp/repo.xml</customRepositoryMap>
                    <!-- This will ensure that the plugin only downloads binaries for the current OS, this will override anything specified in the <operatingSystems> configuration -->
                    <onlyGetDriversForHostOperatingSystem>false</onlyGetDriversForHostOperatingSystem>
                    <!-- Operating systems you want to download binaries for (Only valid options are: windows, linux, osx) -->
                    <operatingSystems>
                        <windows>true</windows>
                        <linux>true</linux>
                        <mac>true</mac>
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
                    <!-- Throw an exception if any specified binary versions that the plugin tries to download do not exist -->
                    <throwExceptionIfSpecifiedVersionIsNotFound>false</throwExceptionIfSpecifiedVersionIsNotFound>
                    <!-- Number of times to attempt to download each file -->
                    <fileDownloadRetryAttempts>2</fileDownloadRetryAttempts>
                    <!-- Number of ms to wait before timing out when trying to connect to remote server to download file -->
                    <fileDownloadConnectTimeout>20000</fileDownloadConnectTimeout>
                    <!-- Number of ms to wait before timing out when trying to read file from remote server -->
                    <fileDownloadReadTimeout>10000</fileDownloadReadTimeout>
                    <!-- Overwrite any existing binaries that have been downloaded and extracted -->
                    <overwriteFilesThatExist>true</overwriteFilesThatExist>
                    <!-- Check file hashes of downloaded files.  Default: true -->
                    <checkFileHashes>true</checkFileHashes>
                    <!-- auto detect system proxy to use when downloading files -->
                    <!-- To specify an explicit proxy set the environment variables http.proxyHost and http.proxyPort -->
                    <useSystemProxy>true</useSystemProxy>
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

How do I get the SHA1/MD5 hashes for the binaries I ant to download?

The people providing the binaries should be publishing MD5/SHA1 hashes as well so that you can check that the file you have downloaded is not corrupt.  It seems that recently this does not seem to be happening as a matter of course.  If you can't find a published SHA1/MD5 hash you can always download the file yourself, check that it is not corrupt and then generate the hash using the following commands:

On a *nix system it's pretty easy. perform the following command:

    openssl md5 <filename>
    openssl sha1 <filename>

On windows you can do the following (according to https://support.microsoft.com/en-us/kb/889768):

    FCIV -sha1 <filename>
    FCIV -md5 <filename>

___Below is an example RepositoryMap.xml that I will endeavour to keep up to date so that you can just copy/paste the contents into your own file.___

    <?xml version="1.0" encoding="utf-8" standalone="yes"?>
    <root>
        <windows>
            <driver id="internetexplorer">
                <version id="3.150.1">
                    <bitrate sixtyfourbit="true">
                        <filelocation>https://selenium-release.storage.googleapis.com/3.150/IEDriverServer_x64_3.150.1.zip</filelocation>
                        <hash>617f88598910dc2e77b25c5b4b653a0176025917</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                    <bitrate thirtytwobit="true">
                        <filelocation>https://selenium-release.storage.googleapis.com/3.150/IEDriverServer_Win32_3.150.1.zip</filelocation>
                        <hash>2247b50a1fd05b51fd441e2a42e037be16fd7d70</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="edge">
                <version id="82">
                    <bitrate sixtyfourbit="true">
                        <filelocation>https://msedgedriver.azureedge.net/82.0.425.0/edgedriver_win64.zip</filelocation>
                        <hash>f88a6ae9a83af88c5a75001422d1722341a2c142</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                    <bitrate thirtytwobit="true">
                        <filelocation>https://msedgedriver.azureedge.net/82.0.425.0/edgedriver_win32.zip</filelocation>
                        <hash>c3d1464b27173143d99247e9d0d3dfb78e552282</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="googlechrome">
                <version id="80">
                    <bitrate thirtytwobit="true" sixtyfourbit="true">
                        <filelocation>https://chromedriver.storage.googleapis.com/80.0.3987.106/chromedriver_win32.zip</filelocation>
                        <hash>40aeb7b0b3a3ea23a139a764b56e172f2fdb90a4</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="operachromium">
                <version id="79">
                    <bitrate sixtyfourbit="true">
                        <filelocation>https://github.com/operasoftware/operachromiumdriver/releases/download/v.79.0.3945.79/operadriver_win64.zip</filelocation>
                        <hash>0d22dfca1f201e4d69525835b755e522455eda63</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                    <bitrate thirtytwobit="true">
                        <filelocation>https://github.com/operasoftware/operachromiumdriver/releases/download/v.79.0.3945.79/operadriver_win32.zip</filelocation>
                        <hash>51da8a34f5ac99c0ba611668991a95fc3042116b</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="marionette">
                <version id="0.26.0">
                    <bitrate sixtyfourbit="true">
                        <filelocation>https://github.com/mozilla/geckodriver/releases/download/v0.26.0/geckodriver-v0.26.0-win64.zip</filelocation>
                        <hash>ca224a47e3a2f6370058b8d715fe577169398847</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                    <bitrate thirtytwobit="true">
                        <filelocation>https://github.com/mozilla/geckodriver/releases/download/v0.26.0/geckodriver-v0.26.0-win32.zip</filelocation>
                        <hash>ac7abe62c1d1adbb7a1b235010a4c33bd2af807c</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
        </windows>
        <linux>
            <driver id="edge">
                <version id="82">
                    <bitrate arm="true">
                        <filelocation>https://msedgedriver.azureedge.net/82.0.425.0/edgedriver_arm64.zip</filelocation>
                        <hash>b1b765e484068f8b1385cf66a9a0debb8b86a411</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="googlechrome">
                <version id="80">
                    <bitrate sixtyfourbit="true">
                        <filelocation>https://chromedriver.storage.googleapis.com/80.0.3987.106/chromedriver_linux64.zip</filelocation>
                        <hash>0e8848ebca11706768fd748dd0282672acad35ac</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="operachromium">
                <version id="79">
                    <bitrate sixtyfourbit="true">
                        <filelocation>https://github.com/operasoftware/operachromiumdriver/releases/download/v.79.0.3945.79/operadriver_linux64.zip</filelocation>
                        <hash>e8df50d0d62f5155821174e47b54d91272919807</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="marionette">
                <version id="0.26.0">
                    <bitrate sixtyfourbit="true">
                        <filelocation>https://github.com/mozilla/geckodriver/releases/download/v0.26.0/geckodriver-v0.26.0-linux64.tar.gz</filelocation>
                        <hash>7d3d597146b82989030f20bdb0b973916c5042a3</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                    <bitrate thirtytwobit="true">
                        <filelocation>https://github.com/mozilla/geckodriver/releases/download/v0.26.0/geckodriver-v0.26.0-linux32.tar.gz</filelocation>
                        <hash>544bae5a729e720f3d694dd85b821b7b74acf65e</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                    <bitrate arm="true">
                        <filelocation>https://github.com/mozilla/geckodriver/releases/download/v0.23.0/geckodriver-v0.23.0-arm7hf.tar.gz</filelocation>
                        <hash>1788cb6756d72c50ac2408e0dc60b778cb40f626</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
        </linux>
        <osx>
            <driver id="edge">
                <version id="82">
                    <bitrate sixtyfourbit="true">
                        <filelocation>https://msedgedriver.azureedge.net/82.0.425.0/edgedriver_mac64.zip</filelocation>
                        <hash>e7045843d8a31b845c7935022e64087ca280fc62</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="googlechrome">
                <version id="80">
                    <bitrate sixtyfourbit="true">
                        <filelocation>https://chromedriver.storage.googleapis.com/80.0.3987.106/chromedriver_mac64.zip</filelocation>
                        <hash>3b58b8039f363de3b13a8bea7d4646105fbbd177</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="operachromium">
                <version id="79">
                    <bitrate sixtyfourbit="true">
                        <filelocation>https://github.com/operasoftware/operachromiumdriver/releases/download/v.79.0.3945.79/operadriver_mac64.zip</filelocation>
                        <hash>a81933f73ebbbff3efddff9e7cb82811807169ed</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="marionette">
                <version id="0.26.0">
                    <bitrate thirtytwobit="true" sixtyfourbit="true">
                        <filelocation>https://github.com/mozilla/geckodriver/releases/download/v0.26.0/geckodriver-v0.26.0-macos.tar.gz</filelocation>
                        <hash>577d33995972626550e7ee7bdb3ad7a9aaf76414</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
        </osx>
    </root>

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

```xml
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
```

By default the plugin will download the most recent binary specified in the RepositoryMap.xml for every driver/os/bitrate.
If you want to filter out the ones you don't need have a look at the advanced usage options below.

Advanced Usage
-----

```xml
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
```

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

```xml
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
            <version id="91">
                <bitrate sixtyfourbit="true">
                    <filelocation>https://msedgedriver.azureedge.net/91.0.849.0/edgedriver_win64.zip</filelocation>
                    <hash>c4b2c31dc3bec1e914f08cda27e2c24a85eb892a</hash>
                    <hashtype>sha1</hashtype>
                </bitrate>
                <bitrate thirtytwobit="true">
                    <filelocation>https://msedgedriver.azureedge.net/91.0.849.0/edgedriver_win32.zip</filelocation>
                    <hash>7cd00ff0716e137d55619e56008059d3ecd675d6</hash>
                    <hashtype>sha1</hashtype>
                </bitrate>
            </version>
        </driver>
        <driver id="googlechrome">
            <version id="90">
                <bitrate thirtytwobit="true" sixtyfourbit="true">
                    <filelocation>https://chromedriver.storage.googleapis.com/90.0.4430.24/chromedriver_win32.zip</filelocation>
                    <hash>0741cdedd5c3b5acf35bb4baf29b19d59b3d6371</hash>
                    <hashtype>sha1</hashtype>
                </bitrate>
            </version>
        </driver>
        <driver id="operachromium">
            <version id="89">
                <bitrate sixtyfourbit="true">
                    <filelocation>https://github.com/operasoftware/operachromiumdriver/releases/download/v.89.0.4389.82/operadriver_win64.zip</filelocation>
                    <hash>a257045198153c4ced64feaeafdda6dc7b02ef03</hash>
                    <hashtype>sha1</hashtype>
                </bitrate>
                <bitrate thirtytwobit="true">
                    <filelocation>https://github.com/operasoftware/operachromiumdriver/releases/download/v.89.0.4389.82/operadriver_win32.zip</filelocation>
                    <hash>ff1fbd114ec4ecdeb11ce294f614c5cd655065b3</hash>
                    <hashtype>sha1</hashtype>
                </bitrate>
            </version>
        </driver>
        <driver id="marionette">
            <version id="0.29.0">
                <bitrate sixtyfourbit="true">
                    <filelocation>https://github.com/mozilla/geckodriver/releases/download/v0.29.0/geckodriver-v0.29.0-win64.zip</filelocation>
                    <hash>1e3062ac8262750463f05310ea3a8053a20f42e6</hash>
                    <hashtype>sha1</hashtype>
                </bitrate>
                <bitrate thirtytwobit="true">
                    <filelocation>https://github.com/mozilla/geckodriver/releases/download/v0.29.0/geckodriver-v0.29.0-win32.zip</filelocation>
                    <hash>7036e1a6f5d87490e1267418cb0e303c9570d6ae</hash>
                    <hashtype>sha1</hashtype>
                </bitrate>
            </version>
        </driver>
    </windows>
    <linux>
        <driver id="edge">
            <version id="91">
                <bitrate arm="true">
                    <filelocation>https://msedgedriver.azureedge.net/91.0.849.0/edgedriver_arm64.zip</filelocation>
                    <hash>2199480c20d2638c6478923b69aa6d439f3fb29d</hash>
                    <hashtype>sha1</hashtype>
                </bitrate>
            </version>
        </driver>
        <driver id="googlechrome">
            <version id="90">
                <bitrate sixtyfourbit="true">
                    <filelocation>https://chromedriver.storage.googleapis.com/90.0.4430.24/chromedriver_linux64.zip</filelocation>
                    <hash>50d02031681c8f32f20b5fa3ce35c29efc8fdb98</hash>
                    <hashtype>sha1</hashtype>
                </bitrate>
            </version>
        </driver>
        <driver id="operachromium">
            <version id="89">
                <bitrate sixtyfourbit="true">
                    <filelocation>https://github.com/operasoftware/operachromiumdriver/releases/download/v.89.0.4389.82/operadriver_linux64.zip</filelocation>
                    <hash>cbed52d82ab56734921b56cc75f5b62ea55ae988</hash>
                    <hashtype>sha1</hashtype>
                </bitrate>
            </version>
        </driver>
        <driver id="marionette">
            <version id="0.29.0">
                <bitrate sixtyfourbit="true">
                    <filelocation>https://github.com/mozilla/geckodriver/releases/download/v0.29.0/geckodriver-v0.29.0-linux64.tar.gz</filelocation>
                    <hash>21590d1be8cfdf3f2d152790ce89aad5a7e563f9</hash>
                    <hashtype>sha1</hashtype>
                </bitrate>
                <bitrate thirtytwobit="true">
                    <filelocation>https://github.com/mozilla/geckodriver/releases/download/v0.29.0/geckodriver-v0.29.0-linux32.tar.gz</filelocation>
                    <hash>30c771284e1fee0ae2008410885a50f547b87253</hash>
                    <hashtype>sha1</hashtype>
                </bitrate>
            </version>
            <version id="0.23.0">
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
            <version id="91">
                <bitrate sixtyfourbit="true">
                    <filelocation>https://msedgedriver.azureedge.net/91.0.848.0/edgedriver_mac64.zip</filelocation>
                    <hash>6f90b11eaa91295e5adc523984db47bdd08ed05f</hash>
                    <hashtype>sha1</hashtype>
                </bitrate>
            </version>
        </driver>
        <driver id="googlechrome">
            <version id="89">
                <bitrate sixtyfourbit="true">
                    <filelocation>https://chromedriver.storage.googleapis.com/89.0.4389.23/chromedriver_mac64.zip</filelocation>
                    <hash>b0857b123b956317ed5a858bac400a6d9ad01f8c</hash>
                    <hashtype>sha1</hashtype>
                </bitrate>
            </version>
        </driver>
        <driver id="operachromium">
            <version id="89">
                <bitrate sixtyfourbit="true">
                    <filelocation>https://github.com/operasoftware/operachromiumdriver/releases/download/v.89.0.4389.82/operadriver_mac64.zip</filelocation>
                    <hash>d3964b603632161fa3cfe3e516a03adc968ec8e6</hash>
                    <hashtype>sha1</hashtype>
                </bitrate>
            </version>
        </driver>
        <driver id="marionette">
            <version id="0.29.0">
                <bitrate thirtytwobit="true" sixtyfourbit="true">
                    <filelocation>https://github.com/mozilla/geckodriver/releases/download/v0.29.0/geckodriver-v0.29.0-macos.tar.gz</filelocation>
                    <hash>f6e624f20e2edd8ef09f7220c5aaed4d59023008</hash>
                    <hashtype>sha1</hashtype>
                </bitrate>
            </version>
        </driver>
    </osx>
</root>
```

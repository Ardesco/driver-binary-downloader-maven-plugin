Selenium driver-binary-downloader-maven-plugin
=================================

[![Join the chat at https://gitter.im/Ardesco/selenium-standalone-server-plugin](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/Ardesco/selenium-standalone-server-plugin?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/Ardesco/selenium-standalone-server-plugin.svg?branch=master)](https://travis-ci.org/Ardesco/selenium-standalone-server-plugin)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.lazerycode.selenium/driver-binary-downloader-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.lazerycode.selenium/driver-binary-downloader-maven-plugin)

A Maven plugin that will download the WebDriver stand alone server binaries for use in your mavenised Selenium project.

What's changed?  See the [Changelog](https://github.com/Ardesco/selenium-standalone-server-plugin/blob/master/CHANGELOG.md).

Default Usage
-----

    <plugins>
        <plugin>
            <groupId>com.lazerycode.selenium</groupId>
            <artifactId>driver-binary-downloader-maven-plugin</artifactId>
            <version>1.0.7</version>
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
                <version>1.0.7</version>
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

How do I get the SHA1/MD5 hases for the binaries I ant to download?

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
                <version id="2.45.0">
                    <bitrate sixtyfourbit="true">
                        <filelocation>http://selenium-release.storage.googleapis.com/2.45/IEDriverServer_x64_2.45.0.zip</filelocation>
                        <hash>b3cdacc846d7b9c3f8fb8b70af0a9cfc5839bd83</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                    <bitrate thirtytwobit="true">
                        <filelocation>http://selenium-release.storage.googleapis.com/2.45/IEDriverServer_Win32_2.45.0.zip</filelocation>
                        <hash>cc822d30efe3119b76af9265c47d42fca208f85a</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="googlechrome">
                <version id="2.14">
                    <bitrate thirtytwobit="true" sixtyfourbit="true">
                        <filelocation>http://chromedriver.storage.googleapis.com/2.14/chromedriver_win32.zip</filelocation>
                        <hash>4fe4aaf625073c39c29da994d815ffcc2c314c40</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="operachromium">
                <version id="2.14">
                    <bitrate thirtytwobit="true" sixtyfourbit="true">
                        <filelocation>https://github.com/operasoftware/operachromiumdriver/releases/download/v0.1.0/operadriver_win32.zip</filelocation>
                        <hash>4a4ad051c315e4141048f0ae587c05f4c8720c24</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="phantomjs">
                <version id="2.0.0">
                    <bitrate thirtytwobit="true" sixtyfourbit="true">
                        <filelocation>https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-2.0.0-windows.zip</filelocation>
                        <hash>ca0c753e5d8820a271dd7c2d6a9fad6ff86fb09f</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
                <version id="1.9.8">
                    <bitrate thirtytwobit="true" sixtyfourbit="true">
                        <filelocation>https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-1.9.8-windows.zip</filelocation>
                        <hash>4531bd64df101a689ac7ac7f3e11bb7e77af8eff</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
        </windows>
        <linux>
            <driver id="googlechrome">
                <version id="2.14">
                    <bitrate sixtyfourbit="true">
                        <filelocation>http://chromedriver.storage.googleapis.com/2.14/chromedriver_linux64.zip</filelocation>
                        <hash>acb76a3eb2bc94ee96b6a17121980e2662c88650</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                    <bitrate thirtytwobit="true">
                        <filelocation>http://chromedriver.storage.googleapis.com/2.14/chromedriver_linux32.zip</filelocation>
                        <hash>237a5ed160bb23118a9ea5b84700e8799e897bd4</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="operachromium">
                <version id="2.14">
                    <bitrate thirtytwobit="true">
                        <filelocation>https://github.com/operasoftware/operachromiumdriver/releases/download/v0.1.0/operadriver_linux32.zip</filelocation>
                        <hash>feda76d61190161bd9923f8f1613447f722f12fc</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                    <bitrate sixtyfourbit="true">
                        <filelocation>https://github.com/operasoftware/operachromiumdriver/releases/download/v0.1.0/operadriver_linux64.zip</filelocation>
                        <hash>c36234222efccc1f874682b2ce2add639d544e9d</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="phantomjs">
                <version id="1.9.8">
                    <bitrate sixtyfourbit="true">
                        <filelocation>https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-1.9.8-linux-x86_64.tar.bz2</filelocation>
                        <hash>d29487b2701bcbe3c0a52bc176247ceda4d09d2d</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                    <bitrate thirtytwobit="true">
                        <filelocation>https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-1.9.8-linux-i686.tar.bz2</filelocation>
                        <hash>efac5ae5b84a4b2b3fa845e8390fca39e6e637f2</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
        </linux>
        <osx>
            <driver id="googlechrome">
                <version id="2.14">
                    <bitrate thirtytwobit="true" sixtyfourbit="true">
                        <filelocation>http://chromedriver.storage.googleapis.com/2.14/chromedriver_mac32.zip</filelocation>
                        <hash>64ef44893a87a0e470b60ff8f5fc83a588b78023</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="operachromium">
                <version id="2.14">
                    <bitrate thirtytwobit="true">
                        <filelocation>https://github.com/operasoftware/operachromiumdriver/releases/download/v0.1.0/operadriver_mac32.zip</filelocation>
                        <hash>7ab79a1c70bb0f5998b9c5c8d08160ef86b618e9</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                    <bitrate sixtyfourbit="true">
                        <filelocation>https://github.com/operasoftware/operachromiumdriver/releases/download/v0.1.0/operadriver_mac64.zip</filelocation>
                        <hash>32e5e0fc63bed0f61bb4e8695fd7a8faaebd7b37</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="phantomjs">
                <version id="2.0.0">
                    <bitrate thirtytwobit="true" sixtyfourbit="true">
                        <filelocation>https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-2.0.0-macosx.zip</filelocation>
                        <hash>97f87188bb2fc81e0c57ec3a376b722e3bcc30c9</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
                <version id="1.9.8">
                    <bitrate thirtytwobit="true" sixtyfourbit="true">
                        <filelocation>https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-1.9.8-macosx.zip</filelocation>
                        <hash>d70bbefd857f21104c5961b9dd081781cb4d999a</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
        </osx>
    </root>

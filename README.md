Selenium driver-binary-downloader-maven-plugin
=================================

[![Join the chat at https://gitter.im/Ardesco/selenium-standalone-server-plugin](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/Ardesco/selenium-standalone-server-plugin?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/Ardesco/selenium-standalone-server-plugin.svg?branch=master)](https://travis-ci.org/Ardesco/selenium-standalone-server-plugin)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.lazerycode.selenium/driver-binary-downloader-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.lazerycode.selenium/driver-binary-downloader-maven-plugin)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/com.lazerycode.selenium/driver-binary-downloader-maven-plugin/badge.svg)](http://www.javadoc.io/doc/com.lazerycode.selenium/driver-binary-downloader-maven-plugin)


A Maven plugin that will download the WebDriver stand alone server binaries for use in your mavenised Selenium project.

What's changed?  See the [Changelog](https://github.com/Ardesco/selenium-standalone-server-plugin/blob/master/CHANGELOG.md).

Default Usage
-----

    <plugins>
        <plugin>
            <groupId>com.lazerycode.selenium</groupId>
            <artifactId>driver-binary-downloader-maven-plugin</artifactId>
            <version>1.0.13</version>
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
                <version>1.0.13</version>
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
                <version id="3.3.0">
                    <bitrate sixtyfourbit="true">
                        <filelocation>http://selenium-release.storage.googleapis.com/3.3/IEDriverServer_x64_3.3.0.zip</filelocation>
                        <hash>e9058e4fc876491a5df86dc64192c36e1d740678</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                    <bitrate thirtytwobit="true">
                        <filelocation>http://selenium-release.storage.googleapis.com/3.3/IEDriverServer_Win32_3.3.0.zip</filelocation>
                        <hash>06eaccef59a5fba204439d1ac3fc3a10d05a8f68</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="edge">
                <version id="3.14393">
                    <bitrate sixtyfourbit="true" thirtytwobit="true">
                        <filelocation>https://download.microsoft.com/download/3/2/D/32D3E464-F2EF-490F-841B-05D53C848D15/MicrosoftWebDriver.exe</filelocation>
                        <hash>6f9e81e5f60fa3e8dccba15a3715ba20d44d0775</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="googlechrome">
                <version id="2.29">
                    <bitrate thirtytwobit="true" sixtyfourbit="true">
                        <filelocation>https://chromedriver.storage.googleapis.com/2.29/chromedriver_win32.zip</filelocation>
                        <hash>2f02f28d3ff1b8f2a63cb3bc32c26ade60ac4737</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="operachromium">
                <version id="0.2.2">
                    <bitrate sixtyfourbit="true">
                        <filelocation>https://github.com/operasoftware/operachromiumdriver/releases/download/v0.2.2/operadriver_win64.zip</filelocation>
                        <hash>8b84d334ca6dc5e30c168d8df080c1827e4c6fdb</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                    <bitrate thirtytwobit="true">
                        <filelocation>https://github.com/operasoftware/operachromiumdriver/releases/download/v0.2.2/operadriver_win32.zip</filelocation>
                        <hash>daa9ba52eeca5ea3cb8a9020b85642ec047e9890</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="phantomjs">
                <version id="2.1.1">
                    <bitrate thirtytwobit="true" sixtyfourbit="true">
                        <filelocation>https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-2.1.1-windows.zip</filelocation>
                        <hash>eb61e6dc49832a3d60f708a92fa7299c57cad7db</hash>
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
            <driver id="marionette">
                <version id="0.15.0">
                    <bitrate sixtyfourbit="true">
                        <filelocation>https://github.com/mozilla/geckodriver/releases/download/v0.15.0/geckodriver-v0.15.0-win64.zip</filelocation>
                        <hash>18222997ab63edb94d7ba03950bc42f52ca1fa54</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                    <bitrate thirtytwobit="true">
                        <filelocation>https://github.com/mozilla/geckodriver/releases/download/v0.15.0/geckodriver-v0.15.0-win32.zip</filelocation>
                        <hash>896d88662371932dd666f8268c9718bda09f280a</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
        </windows>
        <linux>
            <driver id="googlechrome">
                <version id="2.29">
                    <bitrate sixtyfourbit="true">
                        <filelocation>https://chromedriver.storage.googleapis.com/2.29/chromedriver_linux64.zip</filelocation>
                        <hash>025a098cde0a6ad8aef53d6734979c9845bf49b5</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                    <bitrate thirtytwobit="true">
                        <filelocation>https://chromedriver.storage.googleapis.com/2.29/chromedriver_linux32.zip</filelocation>
                        <hash>36d4082a6fb3b3cbb31b013a08b1900baf13743d</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="operachromium">
                <version id="0.2.2">
                    <bitrate thirtytwobit="true">
                        <filelocation>https://github.com/operasoftware/operachromiumdriver/releases/download/v0.2.2/operadriver_linux32.zip</filelocation>
                        <hash>8b0b92a870a1b4ba619eb0b85ec587caa2942e5b</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                    <bitrate sixtyfourbit="true">
                        <filelocation>https://github.com/operasoftware/operachromiumdriver/releases/download/v0.2.2/operadriver_linux64.zip</filelocation>
                        <hash>c207c6916e20ecbbc7157e3bdeb4737f14f15fe3</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="phantomjs">
                <version id="2.1.1">
                    <bitrate sixtyfourbit="true">
                        <filelocation>https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-2.1.1-linux-x86_64.tar.bz2</filelocation>
                        <hash>f8afc8a24eec34c2badccc93812879a3d6f2caf3</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                    <bitrate thirtytwobit="true">
                        <filelocation>https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-2.1.1-linux-i686.tar.bz2</filelocation>
                        <hash>9870663f5c2826501508972b8a201d9210d27b59</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
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
            <driver id="marionette">
                <version id="0.15.0">
                    <bitrate sixtyfourbit="true">
                        <filelocation>https://github.com/mozilla/geckodriver/releases/download/v0.15.0/geckodriver-v0.15.0-linux64.tar.gz</filelocation>
                        <hash>e96aed98289c85bee46f6b57740c4694aee7bd28</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
        </linux>
        <osx>
            <driver id="googlechrome">
                <version id="2.29">
                    <bitrate sixtyfourbit="true">
                        <filelocation>https://chromedriver.storage.googleapis.com/2.29/chromedriver_mac64.zip</filelocation>
                        <hash>cec18df4ef736d6712593faf91b462352217214a</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="operachromium">
                <version id="0.2.2">
                    <bitrate sixtyfourbit="true">
                        <filelocation>https://github.com/operasoftware/operachromiumdriver/releases/download/v0.2.2/operadriver_mac64.zip</filelocation>
                        <hash>d58a3b676dda7ede5c38e5df218e7c619495c4ed</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
            <driver id="phantomjs">
                <version id="2.1.1">
                    <bitrate thirtytwobit="true" sixtyfourbit="true">
                        <filelocation>https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-2.1.1-macosx.zip</filelocation>
                        <hash>c6e1a16bb9e89ce1e392a4768e99177797c93350</hash>
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
            <driver id="marionette">
                <version id="0.15.0">
                    <bitrate thirtytwobit="true" sixtyfourbit="true">
                        <filelocation>https://github.com/mozilla/geckodriver/releases/download/v0.15.0/geckodriver-v0.15.0-macos.tar.gz</filelocation>
                        <hash>e98b42e30ef9ba56d18e0dc20e59327505da5acf</hash>
                        <hashtype>sha1</hashtype>
                    </bitrate>
                </version>
            </driver>
        </osx>
    </root>
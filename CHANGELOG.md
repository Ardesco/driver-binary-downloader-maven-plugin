# Changelog

##Next Version (Release Date TBC) Release Notes

##Version 1.0.14 Release Notes

* Fix #55 Relative file paths are now supported again. 

##Version 1.0.13 Release Notes

* Fix #47 Support to read the RepositoryMap.xml from plugin dependencies added (Thanks [Haroon Sheikh](https://github.com/haroon-sheikh))

##Version 1.0.12 Release Notes

* Fix #50 Remove the @Execute annotation causing test-compile to be executed twice
* Fix #49 Fix a couple of logging typos
* Fix a typo in the README.md that referred 'edgedriver' instead of 'edge'

##Version 1.0.11. Release Notes

* Fix #38 Add in support for Edge Driver

##Version 1.10.0 Release Notes

* Fix #36 Change expected filenames for Marionette binaries.  We now support Marionette binaries version 0.7.1 onwards.  If you are using earlier binary versions things will break for you.

##Version 1.0.9 Release Notes

* Update default RepositoryMap.xml to latest binary versions
* Fix #29 Clean up logging to make it clearer when file is downloaded, and when an existing file is used
* Fix #34 Add support for wires (Marionette) binaries.

##Version 1.0.8 Release Notes

* Fix #24 documentation updated to show correct usage of &lt;operatingSystems&gt; for mac
* Fix #33 Use Travis CI for build, and Rultor for release.
* Fix bug where file stream may not have been closed correctly (Thanks [Aneesh Joseph](https://github.com/aneesh-josephUpdate))

##Version 1.0.7 Release Notes

* Fix #21 properly.  The plugin now sets maven properties which can be imported into projects as environmental properties.

##Version 1.0.6 Release Notes

* **BREAKING CHANGE** The 64bit and 32bit options are false by default.  Default will now work out what architecture your OS is and download the appropriate binary.  This may cause issues on Windows machines if you want to use the 32bit binary by default
* **BREAKING CHANGE** When specifying operating systems you now need to use &lt;mac&gt;true&lt;/mac&gt; instead of &lt;osx&gt;true&lt;/osx&gt;
* XML Parsing rewritten
* Fix #21 webdriver system property now set by default
* Fix #22 if overwriteFilesThatExist == false we now don't open the zip if a file with one of the expected binary names for that zip exists on the file system.
* Fix #23 Setting default proxy to null in error

##Version 1.0.5 Release Notes

* Add support for Opera Chromium Driver
* Update RepositoryMap.xml to latest versions of driver binaries

##Version 1.0.4 Release Notes

* Fixed bug where archives were not closed after files were extracted
* Fixed bug wherearchives may have been downloaded multiple times
* Proxy auto detection added
* Proxy Support added (Thanks [Nassos A. Machas](https://github.com/NMichas))

##Version 1.0.3 Release Notes

* Additional logging added when defaulting to onlyGetDriversForHostOperatingSystem
* Fix 14 IE Driver hashes are incorrect in default RepositoryMap.xml
* Fix 'Unsupported Content-Coding: None' error
* Fix 15 Improved logging on IOException/Invalid File hash

##Version 1.0.2 Release Notes

* Add missing documentation to README.md
* Fix #13 Only check if the custom RepositoryMap.xml is valid if one is specified
* More verbose logging added to help debug OS selection issues

##Version 1.0.1 Release Notes

* Fix #7 Added the ability to turn off the hash check for downloaded file
* Fix #8 Add support for tar.bz2
* Updated to maven plugin API 3.1.1
* Use Apache HttpClient to create the download input stream to ensure redirects are followed

##Version 1.0.0 Release Notes

* First stable release
* Improved performance of unzip code
* Support for PhantomJS binaries added
* Fix #5 Files in zip subdirectories now extracted successfully
* Fix bug with file hash checking algorithms


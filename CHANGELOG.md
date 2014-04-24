# Changelog

##Next Version (Release Date TBC) Release Notes

##Version 1.0.3 Release Notes

* Additional logging added when defaulting to onlyGetDriversForHostOperatingSystem
* Fix 14 IE Driver hases incorrect in default RepositoryMap.xml
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


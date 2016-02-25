package com.lazerycode.selenium.extract;

public enum ArchiveType {
    GZ("gz"),
    BZ2("bz2"),
    ZIP("zip"),
    TAR("tar");

    private final String archiveFileExtension;

    ArchiveType(String archiveFileExtension) {
        this.archiveFileExtension = archiveFileExtension;
    }
}
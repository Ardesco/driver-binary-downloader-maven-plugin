package com.lazerycode.selenium.download;

import com.lazerycode.selenium.Bit;
import com.lazerycode.selenium.Driver;
import com.lazerycode.selenium.OperatingSystem;
import com.lazerycode.selenium.repository.RepositoryParser;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

public class DownloadHandler {

private Map<String,String> driversToGet;
    private FileDownloader getter = new FileDownloader();
    private File repositoryMap;
    private RepositoryParser getStandaloneExecutible;

    public DownloadHandler(Map <String, String> driversToGet, File repositoryMap) throws Exception{
        this.driversToGet = driversToGet;
        this.repositoryMap = repositoryMap;
        this.getStandaloneExecutible = new RepositoryParser(this.repositoryMap);
    }

    public void getStandaloneExecutables() throws Exception{
        Iterator it = this.driversToGet.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            //TODO build XPATH to getStandaloneExecutible location
            //root/${BROWSER}/${VERSION}/${OS}[${BITRATE}/filelocation
            ;
            this.getter.localFilePath("");
            this.getter.remoteURL(new URL(this.getStandaloneExecutible.forDriver(Driver.IE, "2.21.0").andOS(Bit.SIXTYFOURBIT, OperatingSystem.WINDOWS).returnFilePath()));
        }
    }

}

package com.lazerycode.selenium.hash;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class HashTypeAdaptor extends XmlAdapter<String, HashType> {

    @Override
    public HashType unmarshal(String str) throws Exception {
        return HashType.valueOf(str.toUpperCase());
    }

    @Override
    public String marshal(HashType hashType) throws Exception {
        return hashType.toString().toLowerCase();
    }
}

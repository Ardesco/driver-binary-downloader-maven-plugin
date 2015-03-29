package com.lazerycode.selenium.repository;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

public class unmarshallingEventHandler implements ValidationEventHandler {

    public boolean handleEvent(ValidationEvent event) {
        return false;
    }
}

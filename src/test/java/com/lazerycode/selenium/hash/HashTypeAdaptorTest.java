package com.lazerycode.selenium.hash;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class HashTypeAdaptorTest {

    @Test
    public void returnsAValidHashTypeWithLowerCaseText() throws Exception {
        HashTypeAdaptor hashTypeAdaptor = new HashTypeAdaptor();

        assertThat(hashTypeAdaptor.unmarshal("sha1"),
                is(equalTo(HashType.SHA1)));
    }

    @Test
    public void returnsAValidHashTypeWithUpperCaseText() throws Exception {
        HashTypeAdaptor hashTypeAdaptor = new HashTypeAdaptor();

        assertThat(hashTypeAdaptor.unmarshal("SHA1"),
                is(equalTo(HashType.SHA1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsAnIllegalArgumentExceptionIfHashTypeIsInvalid() throws Exception {
        HashTypeAdaptor hashTypeAdaptor = new HashTypeAdaptor();
        hashTypeAdaptor.unmarshal("FOO");
    }

    @Test
    public void returnsALowerCaseStringWhenMarshalling() throws Exception {
        HashTypeAdaptor hashTypeAdaptor = new HashTypeAdaptor();

        assertThat(hashTypeAdaptor.marshal(HashType.MD5),
                is(equalTo("md5")));
    }
}

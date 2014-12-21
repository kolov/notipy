package com.akolov.notipy;

import org.junit.Assert;
import org.junit.Test;

public class OsDetectorTest {

    @Test
    public void testGetOsName() throws Exception {

        String osName = new OsDetector().getOsName();
        System.out.print(osName);
        Assert.assertEquals("x", osName);
    }
}
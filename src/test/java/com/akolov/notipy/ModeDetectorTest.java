package com.akolov.notipy;

import org.junit.Assert;
import org.junit.Test;

public class ModeDetectorTest {

    @Test
    public void testGetOsName() throws Exception {
        Mode mode = new ModeDetector().getOsName();
        Assert.assertEquals(Mode.SCAN, mode);
    }
}
package com.akolov.notipy;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class NotipyTest {



    private Notipy subject = new Notipy();

    @Test
    public void testAddWatch() throws Exception {

        String tempDir = System.getProperty("java.io.tmpdir");
        tempDir = tempDir.substring(0, tempDir.length() - 1);


        TestListener testListener = new TestListener();
        subject.addWatch(tempDir, Notipy.FILE_ANY, false, testListener);

        Assert.assertEquals(0, testListener.getAdded().size());

        File file = File.createTempFile("test", "", new File(tempDir));
        file.createNewFile();
        Thread.sleep(500);
        Assert.assertEquals(1, testListener.getAdded().size());
    }

}
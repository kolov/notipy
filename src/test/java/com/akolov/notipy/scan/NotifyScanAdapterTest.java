package com.akolov.notipy.scan;

import com.akolov.notipy.Notipy;
import com.akolov.notipy.TestListener;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class NotifyScanAdapterTest {

    private NotifyScanAdapter subject;

    @Before
    public void init() {
        subject = new NotifyScanAdapter();
    }

    @Test
    public void testAddWatch() throws Exception {
        String tempDir = System.getProperty("java.io.tmpdir");
        tempDir = tempDir.substring(0, tempDir.length() - 1);


        TestListener testListener = new TestListener();
        subject.addWatch(tempDir, Notipy.FILE_ANY, false, testListener);

        Assert.assertEquals(0, testListener.getAdded().size());

        File file = File.createTempFile("test", "", new File(tempDir));
        file.createNewFile();
        Thread.sleep(1500);
        Assert.assertEquals(1, testListener.getAdded().size());
    }
}
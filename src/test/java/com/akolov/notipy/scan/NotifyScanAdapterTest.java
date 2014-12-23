package com.akolov.notipy.scan;

import com.akolov.notipy.NotipyListener;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

        final List<String> l = new ArrayList<>();
        subject.addWatch(tempDir, 2, false, new NotipyListener() {
            @Override
            public void fileCreated(int wd, String rootPath, String name) {
                l.add(name);
            }

            @Override
            public void fileDeleted(int wd, String rootPath, String name) {

            }

            @Override
            public void fileModified(int wd, String rootPath, String name) {

            }

            @Override
            public void fileRenamed(int wd, String rootPath, String oldName, String newName) {

            }
        });

        Assert.assertEquals(0, l.size());

        File file = File.createTempFile("test", "", new File(tempDir));
        file.createNewFile();
        Thread.sleep(1500);
        Assert.assertEquals(1, l.size());
    }
}
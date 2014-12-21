package com.akolov.notipy;

import org.junit.Test;

public class NotipyTest {

    private NotipyListener listener = new NotipyListener() {
        @Override
        public void fileCreated(int wd, String rootPath, String name) {
            System.out.println("created ");
        }

        @Override
        public void fileDeleted(int wd, String rootPath, String name) {
            System.out.println("deleted ");
        }

        @Override
        public void fileModified(int wd, String rootPath, String name) {
            System.out.println("modified ");
        }

        @Override
        public void fileRenamed(int wd, String rootPath, String oldName, String newName) {
            System.out.println("renamed ");
        }
    };

    @Test

    public void testAddWatch() throws Exception {

        int handle = Notipy.addWatch("/", 1, true, listener);
        Notipy.removeWatch(handle);
    }

}
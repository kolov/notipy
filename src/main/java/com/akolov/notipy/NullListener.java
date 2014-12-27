package com.akolov.notipy;


public class NullListener implements NotipyListener {
    @Override
    public void fileCreated(int wd, String rootPath, String name) {

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
}

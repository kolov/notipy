package com.akolov.notipy;

import java.util.ArrayList;
import java.util.List;


public class TestListener implements NotipyListener {

    private List<String> added = new ArrayList();
    private List<String> deleted = new ArrayList();
    private List<String> modified = new ArrayList();
    private List<String> renamed = new ArrayList();

    @Override
    public void fileCreated(int wd, String rootPath, String name) {
        added.add(name);
    }

    @Override
    public void fileDeleted(int wd, String rootPath, String name) {
        deleted.add(name);

    }

    @Override
    public void fileModified(int wd, String rootPath, String name) {
        modified.add(name);
    }

    @Override
    public void fileRenamed(int wd, String rootPath, String oldName, String newName) {
        renamed.add(oldName);
    }

    public List<String> getAdded() {
        return added;
    }

    public List<String> getDeleted() {
        return deleted;
    }

    public List<String> getModified() {
        return modified;
    }

    public List<String> getRenamed() {
        return renamed;
    }
}

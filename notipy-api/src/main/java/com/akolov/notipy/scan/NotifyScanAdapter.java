package com.akolov.notipy.scan;


import com.akolov.notipy.Notipy;
import com.akolov.notipy.NotipyAdapter;
import com.akolov.notipy.NotipyException;
import com.akolov.notipy.NotipyListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotifyScanAdapter implements NotipyAdapter {

    private static final Logger LOG = Logger.getLogger(NotifyScanAdapter.class.getName());

    private static int counter = 0;
    private Map<WatchDesc, WatchersGroup> listeners = new HashMap();

    private static class RegisteredListener {
        public NotipyListener listener;
        private boolean watchSubtree;
        private int mask;

        private String key;

        public RegisteredListener(NotipyListener listener, String key, boolean watchSubtree, int mask) {
            this.listener = listener;
            this.watchSubtree = watchSubtree;
            this.mask = mask;
            this.key = key;
        }

        public boolean subscribedToNew() {
            return (mask & Notipy.FILE_CREATED) != 0;
        }

        public boolean subscribedToModify() {
            return (mask & Notipy.FILE_MODIFIED) != 0;
        }
    }

    private static class WatchDesc {
        public String path;

        public boolean watchSubtree;

        public WatchDesc(String path, boolean watchSubtree) {
            this.path = path;
            this.watchSubtree = watchSubtree;
        }

    }

    private static class WatcherSnapshot {

        private Map<String, Long> files = new HashMap();

        public WatcherSnapshot(Map<String, Long> files) {
            this.files = files;
        }

        public static WatcherSnapshot fromFolder(String path, boolean inclSubtree) {
            Map<String, Long> files = getFilesInFolder(new File(path), inclSubtree);
            return new WatcherSnapshot(files);
        }

    }

    private static void listFilesForFolder(final File folder, boolean nested, Map<String, Long> files) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory() && nested) {
                listFilesForFolder(fileEntry, nested, files);
            } else {
                files.put(fileEntry.getAbsolutePath(), fileEntry.lastModified());
            }
        }
    }

    private static Map<String, Long> getFilesInFolder(final File folder, boolean nested) {
        Map<String, Long> files = new HashMap();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory() && nested) {
                listFilesForFolder(fileEntry, nested, files);
            } else {
                files.put(fileEntry.getAbsolutePath(), fileEntry.lastModified());
            }
        }
        return files;
    }

    private static class WatchersGroup {
        public WatcherSnapshot snapshot;

        public List<RegisteredListener> listeners;

        public WatchersGroup(final String path, final boolean watchSubtree) {
            snapshot = WatcherSnapshot.fromFolder(path, watchSubtree);
            listeners = new ArrayList();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            //
                        }
                        check(new File(path), watchSubtree);
                    }
                }
            }).start();
        }

        public void add(RegisteredListener registeredListener) {
            listeners.add(registeredListener);
        }

        public void check(final File folder, boolean nested) {
            Map<String, Long> files = getFilesInFolder(folder, nested);

            for (Map.Entry<String, Long> entry : files.entrySet()) {
                Long previousEntry = snapshot.files.get(entry.getKey());
                if (previousEntry == null) {
                    notifyNew(entry.getKey());
                } else if (previousEntry.longValue() != entry.getValue().longValue()) {
                    notifyChanged(entry.getKey());
                }
            }
            snapshot.files = files;
        }

        private void notifyNew(String name) {
            for (RegisteredListener listener : listeners) {
                if (listener.subscribedToNew()) {
                    listener.listener.fileCreated(2, name, name);
                }
            }
        }

        private void notifyChanged(String name) {
            for (RegisteredListener listener : listeners) {
                if (listener.subscribedToModify()) {
                    listener.listener.fileModified(2, name, name);
                }
            }
        }

    }

    @Override
    public synchronized String addWatch(String path, int mask, boolean watchSubtree, NotipyListener listener) throws
            NotipyException {

        LOG.log(Level.FINE, "addWatch");
        File folder = new File(path);

        if (!folder.isDirectory()) {
            throw new RuntimeException(path + " is not a directory");
        }

        WatchDesc wd = new WatchDesc(path, watchSubtree);
        WatchersGroup watchers = listeners.get(wd);
        if (watchers == null) {
            watchers = new WatchersGroup(path, watchSubtree);
            listeners.put(wd, watchers);
        }
        String key = Integer.toString(counter++);
        watchers.add(new RegisteredListener(listener, key, watchSubtree, mask));


        return key;
    }


    @Override
    public boolean removeWatch(String wd) throws NotipyException {
        return false;
    }
}

package com.akolov.notipy.scan;


import com.akolov.notipy.INotipy;
import com.akolov.notipy.JNotifyException;
import com.akolov.notipy.NotipyListener;

import java.util.HashMap;
import java.util.Map;

public class NotifyScanAdapter implements INotipy {

    private static class RegisteredListener {
        public NotipyListener listener;
        private boolean watchSubtree;
        private int mask;
        private String path;

        public RegisteredListener(NotipyListener listener, boolean watchSubtree, int mask, String path) {
            this.listener = listener;
            this.watchSubtree = watchSubtree;
            this.mask = mask;
            this.path = path;
        }
    }

    private static int counter = 0;
    private Map<String, RegisteredListener> listeners = new HashMap<>();

    @Override
    public String addWatch(String path, int mask, boolean watchSubtree, NotipyListener listener) throws
            JNotifyException {
        String key = Integer.toString(counter++);
        listeners.put(key,
                new RegisteredListener(listener, watchSubtree, mask, path));
        return key;
    }

    @Override
    public boolean removeWatch(String wd) throws JNotifyException {
        return false;
    }
}

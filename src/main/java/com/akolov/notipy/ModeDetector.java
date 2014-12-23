package com.akolov.notipy;


public class ModeDetector {

    private final String NOTIPY_MODE = "notipy.mode";
    private final String MODE_NATIVE_INOTIFY = "inotify";
    private final String MODE_SCAN = "scan";

    public Mode getMode() {
        String modeString = System.getProperty(NOTIPY_MODE);
        if (modeString == null || modeString.equals(MODE_SCAN)) {
            return Mode.SCAN;
        }
        if (modeString.equals(MODE_NATIVE_INOTIFY)) {
            return Mode.INOTIFY;
        }
        throw new RuntimeException("Invalid value for " + NOTIPY_MODE + ": " + modeString);
    }
}

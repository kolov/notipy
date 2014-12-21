package com.akolov.notipy;


public class OsDetector {
    public enum Os {
        WINDOWS, LINUX, MACOSX;
    }


    public String getOsName() {
        String name = System.getProperty("os.name");
        if (name == null) {
            throw new RuntimeException("Can't determine OS");
        }
        return name;
    }
}

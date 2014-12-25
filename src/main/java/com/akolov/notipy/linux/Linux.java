package com.akolov.notipy.linux;


public class Linux {

    public static String getMaskDesc(int linuxMask) {

        String s = "";
        s += appendIf(linuxMask, s, Notipy_linux.IN_ACCESS, "IN_ACCESS");
        s += appendIf(linuxMask, s, Notipy_linux.IN_MODIFY, "IN_MODIFY");
        s += appendIf(linuxMask, s, Notipy_linux.IN_ATTRIB, "IN_ATTRIB");
        s += appendIf(linuxMask, s, Notipy_linux.IN_CLOSE_WRITE, "IN_CLOSE_WRITE");
        s += appendIf(linuxMask, s, Notipy_linux.IN_CLOSE_NOWRITE, "IN_CLOSE_NOWRITE");
        s += appendIf(linuxMask, s, Notipy_linux.IN_OPEN, "IN_OPEN");
        s += appendIf(linuxMask, s, Notipy_linux.IN_MOVED_FROM, "IN_MOVED_FROM");
        s += appendIf(linuxMask, s, Notipy_linux.IN_MOVED_TO, "IN_MOVED_TO");
        s += appendIf(linuxMask, s, Notipy_linux.IN_CREATE, "IN_CREATE");
        s += appendIf(linuxMask, s, Notipy_linux.IN_DELETE, "IN_DELETE");
        s += appendIf(linuxMask, s, Notipy_linux.IN_DELETE_SELF, "IN_DELETE_SELF");
        s += appendIf(linuxMask, s, Notipy_linux.IN_MOVE_SELF, "IN_MOVE_SELF");
        s += appendIf(linuxMask, s, Notipy_linux.IN_UNMOUNT, "IN_ACCESS");
        s += appendIf(linuxMask, s, Notipy_linux.IN_Q_OVERFLOW, "IN_Q_OVERFLOW");
        s += appendIf(linuxMask, s, Notipy_linux.IN_IGNORED, "IN_IGNORED");

        return s;
    }

    private static String appendIf(int linuxMask, String s, int flag, String text) {
        if ((flag & linuxMask) != 0) {
            if (s.length() > 0) {
                s = s + " | ";
            }
            return s + text;
        }
        return s;
    }

}

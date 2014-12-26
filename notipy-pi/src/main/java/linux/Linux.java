package linux;


public class Linux {


    /* the following are legal, implemented events that user-space can watch for */
    public final static int IN_ACCESS = 0x00000001; /* File was accessed */
    public final static int IN_MODIFY = 0x00000002; /* File was modified */
    public final static int IN_ATTRIB = 0x00000004; /* Metadata changed */
    public final static int IN_CLOSE_WRITE = 0x00000008; /* Writtable file was closed */
    public final static int IN_CLOSE_NOWRITE = 0x00000010; /* Unwrittable file closed */
    public final static int IN_OPEN = 0x00000020; /* File was opened */
    public final static int IN_MOVED_FROM = 0x00000040; /* File was moved from X */
    public final static int IN_MOVED_TO = 0x00000080; /* File was moved to Y */
    public final static int IN_CREATE = 0x00000100; /* Subfile was created */
    public final static int IN_DELETE = 0x00000200; /* Subfile was deleted */
    public final static int IN_DELETE_SELF = 0x00000400; /* Self was deleted */
    public final static int IN_MOVE_SELF = 0x00000800; /* Self was moved */

    /* the following are legal events. they are sent as needed to any watch */
    public final static int IN_UNMOUNT = 0x00002000; /* Backing fs was unmounted */
    public final static int IN_Q_OVERFLOW = 0x00004000; /* Event queued overflowed */
    public final static int IN_IGNORED = 0x00008000; /* File was ignored */

    /* helper events */
    public final static int IN_CLOSE = (IN_CLOSE_WRITE | IN_CLOSE_NOWRITE); /* close */
    public final static int IN_MOVE = (IN_MOVED_FROM | IN_MOVED_TO); /* moves */

    /* special flags */
    public final static int IN_ISDIR = 0x40000000; /*
                                                     * event occurred against
													 * dir
													 */
    public final static int IN_ONESHOT = 0x80000000; /* only send event once */
    public static String getMaskDesc(int linuxMask) {

        String s = "";
        s += appendIf(linuxMask, s, IN_ACCESS, "IN_ACCESS");
        s += appendIf(linuxMask, s, IN_MODIFY, "IN_MODIFY");
        s += appendIf(linuxMask, s, IN_ATTRIB, "IN_ATTRIB");
        s += appendIf(linuxMask, s, IN_CLOSE_WRITE, "IN_CLOSE_WRITE");
        s += appendIf(linuxMask, s, IN_CLOSE_NOWRITE, "IN_CLOSE_NOWRITE");
        s += appendIf(linuxMask, s, IN_OPEN, "IN_OPEN");
        s += appendIf(linuxMask, s, IN_MOVED_FROM, "IN_MOVED_FROM");
        s += appendIf(linuxMask, s, IN_MOVED_TO, "IN_MOVED_TO");
        s += appendIf(linuxMask, s, IN_CREATE, "IN_CREATE");
        s += appendIf(linuxMask, s, IN_DELETE, "IN_DELETE");
        s += appendIf(linuxMask, s, IN_DELETE_SELF, "IN_DELETE_SELF");
        s += appendIf(linuxMask, s, IN_MOVE_SELF, "IN_MOVE_SELF");
        s += appendIf(linuxMask, s, IN_UNMOUNT, "IN_ACCESS");
        s += appendIf(linuxMask, s, IN_Q_OVERFLOW, "IN_Q_OVERFLOW");
        s += appendIf(linuxMask, s, IN_IGNORED, "IN_IGNORED");

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

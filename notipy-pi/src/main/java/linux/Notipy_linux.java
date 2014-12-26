/*******************************************************************************
 * Notipy - distibuted under LGPL 3.0
 * Based on JNotify, see original copiright notice below
 *
 *******************************************************************************
 * JNotify - Allow java applications to register to File system events.
 *
 * Copyright (C) 2005 - Content Objects
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 ******************************************************************************
 *
 * You may also redistribute and/or modify this library under the terms of the
 * Eclipse Public License. See epl.html.
 *
 ******************************************************************************
 *
 * Content Objects, Inc., hereby disclaims all copyright interest in the
 * library `JNotify' (a Java library for file system events).
 *
 * Yahali Sherman, 21 November 2005
 *    Content Objects, VP R&D.
 *
 ******************************************************************************
 * Author : Omry Yadan
 ******************************************************************************/

package linux;


import com.akolov.notipy.INotifyListener;
import com.akolov.notipy.NotipyException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Notipy_linux {

    private static final Logger LOG = Logger.getLogger(Notipy_linux.class.getName());

    static {
        int res = nativeInit();
        if (res != 0) {
            throw new RuntimeException("Error initializing fshook_inotify library. linux error code #" + res + ", man errno for more info");
        }
        init();
    }

    private static void init() {
        LOG.log( Level.INFO, "init()");
        Thread thread = new Thread("Notipy_linux") {
            public void run() {
                int n = nativeNotifyLoop();
            }
        };
        thread.setDaemon(true);
        thread.start();
    }




    private static INotifyListener _notifyListener;

    private static native int nativeInit();

    private static native int nativeAddWatch(String path, int mask);

    private static native int nativeRemoveWatch(int wd);

    private static native int nativeNotifyLoop();

    private static native String getErrorDesc(long errorCode);


    public static int addWatch(String path, int mask) throws NotipyException {
        int wd = nativeAddWatch(path, mask);
        if (wd < 0) {
            throw new NotipyException_linux("Error watching " + path + " : " + getErrorDesc(-wd), -wd);
        }

        LOG.log(Level.FINE, wd + " = addWatch(" + path + "," + Linux.getMaskDesc(mask) + ")");

        return wd;
    }

    public static void removeWatch(int wd) throws NotipyException {
        int ret = nativeRemoveWatch(wd);
        LOG.log(Level.FINE, ret + " = removeWatch(" + wd + ")");
        if (ret != 0) {
            throw new NotipyException_linux("Error removing watch " + wd, ret);
        }
    }


    static void callbackProcessEvent(String name, int wd, int mask, int cookie) {
        LOG.log(Level.FINE, "event(name=" + name + ", wd=" + wd + ", " + Linux.getMaskDesc(mask) + (cookie !=
                0 ? ", cookie=" + cookie : "") + ")");

        if (_notifyListener != null) {
            _notifyListener.notify(name, wd, mask, cookie);
        }
    }

    public static void setNotifyListener(INotifyListener notifyListener) {
        if (_notifyListener == null) {
            _notifyListener = notifyListener;
        } else {
            throw new RuntimeException("Notify listener is already set. multiple notify listeners are not supported.");
        }
    }


}

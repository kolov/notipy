/*******************************************************************************
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

package com.akolov.notipy;

import com.akolov.notipy.linux.JNotifyAdapterLinux;
import com.akolov.notipy.scan.NotifyScanAdapter;


public class Notipy {
    public static final int FILE_CREATED = 0x1;
    public static final int FILE_DELETED = 0x2;
    public static final int FILE_MODIFIED = 0x4;
    public static final int FILE_RENAMED = 0x8;
    public static final int FILE_ANY = FILE_CREATED | FILE_DELETED | FILE_MODIFIED | FILE_RENAMED;

    private INotipyAdapter _instance;
    
    public Notipy() {
        Mode mode = new ModeDetector().getOsName();
        initInMode(mode);
    }

    public Notipy(Mode mode) {
        initInMode(mode);
    }

    private void initInMode(Mode mode) {
        switch (mode) {
            case SCAN:
                _instance = new NotifyScanAdapter();
                break;
            case INOTIFY:
                _instance = new JNotifyAdapterLinux();
                break;
            default:
                throw new RuntimeException("Unexpected");
        }
    }

    public String addWatch(String path, int mask, boolean watchSubtree, NotipyListener listener) throws
            NotipyException {
        return _instance.addWatch(path, mask, watchSubtree, listener);
    }

    public boolean removeWatch(String watchId) throws NotipyException {
        return _instance.removeWatch(watchId);
    }


}

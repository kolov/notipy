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

import com.akolov.notipy.linux.NotipyAdapterLinux;
import com.akolov.notipy.scan.NotifyScanAdapter;

import java.util.HashMap;
import java.util.Map;


public class Notipy {
    public static final int FILE_CREATED = 0x1;
    public static final int FILE_DELETED = 0x2;
    public static final int FILE_MODIFIED = 0x4;
    public static final int FILE_RENAMED = 0x8;
    public static final int FILE_ANY = FILE_CREATED | FILE_DELETED | FILE_MODIFIED | FILE_RENAMED;

    private Notipy() {

    }
    private static Map<Mode, NotipyAdapter> instances = new HashMap<>();

    public static NotipyAdapter getInstance() {
        Mode mode = new ModeDetector().getMode();
        return getInstance(mode);
    }

    public static synchronized NotipyAdapter getInstance(Mode mode) {
        NotipyAdapter result = instances.get(mode);
        if (result == null) {
            result = create(mode);
            instances.put(mode, result);
        }
        return result;
    }


    private static NotipyAdapter create(Mode mode) {
        switch (mode) {
            case SCAN:
                return new NotifyScanAdapter();
            case INOTIFY:
                try {
                    LibLoader.extractAndLoadLibraryFile("notipy.so", true);
                } catch (Exception e) {
                    System.err.println("Couls nor load linux library. Was this built with the linux profile?");
                    throw e;
                }
                return new NotipyAdapterLinux();
            default:
                throw new RuntimeException("Unexpected");
        }
    }


}

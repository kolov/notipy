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

import java.io.File;
import java.io.IOException;


public class NotipyDemo {


    public static void main(String[] args) throws InterruptedException, IOException {
        String dir = new File(args.length == 0 ? "." : args[0]).getCanonicalFile().getAbsolutePath();
        new Notipy().addWatch(dir, Notipy.FILE_ANY, true, new NotipyListener() {
            public void fileRenamed(int wd, String rootPath, String oldName,
                                    String newName) {
                System.out.println("renamed " + rootPath + " : " + oldName + " -> " + newName);
            }

            public void fileModified(int wd, String rootPath, String name) {
                System.out.println("modified " + rootPath + " : " + name);
            }

            public void fileDeleted(int wd, String rootPath, String name) {
                System.out.println("deleted " + rootPath + " : " + name);
            }

            public void fileCreated(int wd, String rootPath, String name) {
                System.out.println("created " + rootPath + " : " + name);
            }
        });

        System.out.println("Monitoring " + dir + ", ctrl+c to stop");
        while (true) Thread.sleep(10000);
    }
}

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


package com.akolov.notipy.linux;


import com.akolov.notipy.INotifyListener;
import com.akolov.notipy.Notipy;
import com.akolov.notipy.NotipyAdapter;
import com.akolov.notipy.NotipyException;
import com.akolov.notipy.NotipyListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


public class NotipyAdapterLinux implements NotipyAdapter {

    private static final Logger LOG = Logger.getLogger(Notipy_linux.class.getName());

    private Map<Integer, Integer> linuxIndecesToOurIndices;
    private Map<Integer, WatchData> indicesOfWatchdata;
    private Set<String> autowatchPaths;

    private static int counter = 0;

    public NotipyAdapterLinux() {
        Notipy_linux.setNotifyListener(new INotifyListener() {
            public void notify(String name, int wd, int mask, int cookie) {
                try {
                    notifyChangeEvent(name, wd, mask, cookie);
                } catch (RuntimeException e) {
                    e.printStackTrace(System.out);
                }
            }
        });

        indicesOfWatchdata = new Hashtable<Integer, WatchData>();
        linuxIndecesToOurIndices = new Hashtable<Integer, Integer>();
        autowatchPaths = new HashSet<>();
    }

    public String addWatch(String path, int mask, boolean watchSubtree, NotipyListener listener)
            throws NotipyException {


        // map mask to linux inotify mask.
        int linuxMask = 0;
        if ((mask & Notipy.FILE_CREATED) != 0) {
            linuxMask |= Linux.IN_CREATE;
        }
        if ((mask & Notipy.FILE_DELETED) != 0) {
            linuxMask |= Linux.IN_DELETE;
            linuxMask |= Linux.IN_DELETE_SELF;
        }
        if ((mask & Notipy.FILE_MODIFIED) != 0) {
            linuxMask |= Linux.IN_ATTRIB;
            linuxMask |= Linux.IN_MODIFY;
        }
        if ((mask & Notipy.FILE_RENAMED) != 0) {
            linuxMask |= Linux.IN_MOVED_FROM;
            linuxMask |= Linux.IN_MOVED_TO;
        }

        // if watching subdirs, listen on create anyway.
        // to know when new sub directories are created.
        // these events should not reach the client code.
        if (watchSubtree) {
            linuxMask |= Linux.IN_CREATE;
        }

        WatchData watchData = createWatch(null, true, new File(path), mask, linuxMask, watchSubtree, listener);
        if (watchSubtree) {
            try {
                File file = new File(path);
                registerToSubTree(true, watchData, file, false);
            } catch (NotipyException e) {
                // cleanup
                removeWatch(Integer.toString(watchData._wd));
                // and throw.
                throw e;
            }
        }
        return Integer.toString(watchData._wd);
    }

    private WatchData createWatch(WatchData parentWatchData,
                                  boolean user,
                                  File path,
                                  int mask,
                                  int linuxMask,
                                  boolean watchSubtree,
                                  NotipyListener listener) throws NotipyException {
        String absPath = path.getPath();
        int wd = counter++;
        int linuxWd = Notipy_linux.addWatch(absPath, linuxMask);
        WatchData watchData = new WatchData(parentWatchData, user, absPath, wd, linuxWd, mask, linuxMask, watchSubtree, listener);
        linuxIndecesToOurIndices.put(Integer.valueOf(linuxWd), Integer.valueOf(wd));
        indicesOfWatchdata.put(Integer.valueOf(wd), watchData);
        if (!user) {
            autowatchPaths.add(absPath);
        }
        return watchData;
    }


    private void registerToSubTree(boolean isRoot,
                                   WatchData parentWatch,
                                   File root,
                                   boolean fireCreatedEvents) throws NotipyException {
        if (!parentWatch._user) {
            throw new RuntimeException("!parentWatch._user");
        }

        // make sure user really requested to be notified on this event.
        // (in case of recursive listening, this IN_CREATE flag is always on, even if
        // the user is not interester in creation events).
        if (fireCreatedEvents && (parentWatch._mask & Notipy.FILE_CREATED) != 0) {
            String name = root.toString().substring(parentWatch._path.length() + 1);
            parentWatch.notifyFileCreated(name);
        }


        if (root.isDirectory()) {
            // root was already registered by the calling method.
            if (!isRoot) {
                try {
                    createWatch(parentWatch, false, root, parentWatch._mask, parentWatch._linuxMask, parentWatch._watchSubtree, parentWatch._listener);
                } catch (NotipyException e) {
                    if (e.getErrorCode() == NotipyException.ERROR_WATCH_LIMIT_REACHED)
                        LOG.log(Level.WARNING, "JNotifyAdapterLinux.registerToSubTree : warning, failed to register "
                                + root +
                                " :" + e.getMessage());
                    {
                        throw e;
                    }
                    // else, on any other error, try subtree anyway..
                }
            }

            String files[] = root.list();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    String file = files[i];
                    registerToSubTree(false, parentWatch, new File(root, file), fireCreatedEvents);
                }
            }
        }
    }

    public boolean removeWatch(String wd) throws NotipyException {
        LOG.log(Level.FINE, "JNotifyAdapterLinux.removeWatch(" + wd + ")");

        synchronized (indicesOfWatchdata) {
            if (indicesOfWatchdata.containsKey(Integer.valueOf(wd))) {
                WatchData watchData = (WatchData) indicesOfWatchdata.get(Integer.valueOf(wd));
                unwatch(watchData);
                return true;
            } else {
                return false;
            }
        }
    }


    private void unwatch(WatchData data) throws NotipyException {
        NotipyException ex = null;
        boolean ok = true;
        try {
            Notipy_linux.removeWatch(data._linuxWd);
        } catch (NotipyException e) {
            e.printStackTrace();
            ex = e;
            ok = false;
        }

        if (data._user) {
            for (int i = 0; i < data._subWd.size(); i++) {

                int wd = data._subWd.get(i).intValue();
                try {
                    Notipy_linux.removeWatch(wd);
                } catch (NotipyException e) {
                    e.printStackTrace();
                    ex = e;
                    ok = false;
                }
            }
        }
        if (!ok) {
            throw ex;
        }
    }

    protected void notifyChangeEvent(String name, int linuxWd, int linuxMask, int cookie) {


        debugLinux(name, linuxWd, linuxMask, cookie);


        synchronized (indicesOfWatchdata) {
            Integer iwd = (Integer) linuxIndecesToOurIndices.get(Integer.valueOf(linuxWd));
            if (iwd == null) {
                // This happens if an exception is thrown because used too many watches.
                System.out.println("JNotifyAdapterLinux: warning, recieved event for an unregisted LinuxWD " + linuxWd + " ignoring...");
                return;
            }

            WatchData watchData = indicesOfWatchdata.get(iwd);
            if (watchData != null) {
                if ((linuxMask & Linux.IN_CREATE) != 0) {
                    File newRootFile = new File(watchData._path, name);
                    if (watchData._watchSubtree) {
                        try {
                            createWatch(watchData.getParentWatch(), false, newRootFile, watchData._mask, watchData._linuxMask, watchData._watchSubtree, watchData._listener);
                            // fire events for newly found directories under the new root.
                            WatchData parent = watchData.getParentWatch();
                            registerToSubTree(true, parent, newRootFile, true);
                        } catch (NotipyException e) {
                            // ignore missing files while registering subtree, may have already been deleted
                            if (e.getErrorCode() != NotipyException.ERROR_NO_SUCH_FILE_OR_DIRECTORY)
                                LOG.log(Level.WARNING, "registerToSubTree : warning, failed to register " + newRootFile + " :" + e.getMessage() + " code = " + e.getErrorCode());
                        }
                    }

                    // make sure user really requested to be notified on this event.
                    // (in case of recursive listening, this IN_CREATE flag is always on, even if
                    // the user is not interester in creation events).
                    if ((watchData._mask & Notipy.FILE_CREATED) != 0) {
                        // fire an event only if the path is not in the path2Watch,
                        // meaning no watch has been created on it.
                        if (!autowatchPaths.contains(newRootFile.getPath())) {
                            watchData.notifyFileCreated(name);
                        } else {
                            LOG.log(Level.FINE, "Assuming already sent event for " + newRootFile.getPath());
                        }
                    }
                } else if ((linuxMask & Linux.IN_DELETE_SELF) != 0) {
                    watchData.notifyFileDeleted(name);
                } else if ((linuxMask & Linux.IN_DELETE) != 0) {
                    watchData.notifyFileDeleted(name);
                } else if ((linuxMask & Linux.IN_ATTRIB) != 0 || (linuxMask & Linux.IN_MODIFY) != 0) {
                    watchData.notifyFileModified(name);
                } else if ((linuxMask & Linux.IN_MOVED_FROM) != 0) {
                    watchData.renaming(cookie, name);
                } else if ((linuxMask & Linux.IN_MOVED_TO) != 0) {
                    watchData.notifyFileRenamed(name, cookie);
                } else if ((linuxMask & Linux.IN_IGNORED) != 0) {
                    linuxIndecesToOurIndices.remove(Integer.valueOf(watchData._linuxWd));
                    indicesOfWatchdata.remove(Integer.valueOf(watchData._wd));
                    if (!watchData._user) {
                        autowatchPaths.remove(watchData._path);
                        watchData.removeFromParent();
                    }
                }
            } else {
                System.out.println("JNotifyAdapterLinux: warning, recieved event for an unregisted WD " + iwd + ". ignoring...");
            }
        }
    }

    private void debugLinux(String name, int linuxWd, int linuxMask, int cookie) {

        String s = Linux.getMaskDesc(linuxMask);
        int wd = linuxIndecesToOurIndices.get(Integer.valueOf(linuxWd)).intValue();
        WatchData wdata = indicesOfWatchdata.get(Integer.valueOf(wd));
        String path;
        if (wdata != null) {
            path = wdata._path;
            if (path != null && name != "") {
                path += File.separator + name;
            }
        } else {
            path = name;
        }
        LOG.log(Level.FINE, "Linux event : wd=" + linuxWd + " | " + s + " path: " + path + (cookie != 0 ? ", cookie=" +
                cookie : ""));
    }

    private static class WatchData {
        boolean _user;
        int _wd;
        private int _linuxWd;
        private ArrayList<Integer> _subWd;
        int _mask;
        int _linuxMask;
        boolean _watchSubtree;
        NotipyListener _listener;
        /*
         * if the cookie hashtable is static every WatchData can access the
         * cookies from other WatchData (directories). The static key word wont
         * be a problem because:
         *      i)   a cookie is deleted right after its retrival
         *      ii)  cookies are only used with rename actions
         *
         * BUG: discovered by Fabio Bernasconi
         */
        static Hashtable<Integer, String> _cookieToOldName = new Hashtable<Integer, String>();
        String _path;
        WatchData _parentWatchData;

        WatchData(WatchData parentWatchData, boolean user, String path, int wd, int linuxWd, int mask, int linuxMask, boolean watchSubtree, NotipyListener listener) {
            if (listener == null) {
                throw new IllegalArgumentException("Null listener");
            }
            _parentWatchData = parentWatchData;
            _user = user;
            _subWd = new ArrayList<Integer>();
            _path = path;
            _wd = wd;
            _linuxMask = linuxMask;
            _linuxWd = linuxWd;
            _mask = mask;
            _watchSubtree = watchSubtree;
            _listener = listener;

            if (parentWatchData != null) {
                parentWatchData.addSubwatch(_linuxWd);
            }
        }

        public WatchData getParentWatch() {
            return _user ? this : _parentWatchData;
        }

        public void removeFromParent() {
            if (_parentWatchData == null) throw new RuntimeException("no parent");
            _parentWatchData.remveSubwatch(_linuxWd);
        }

        public void renaming(int cookie, String name) {
            _cookieToOldName.put(Integer.valueOf(cookie), getOutName(name));
        }

        public void notifyFileRenamed(String name, int cookie) {
            String oldName = _cookieToOldName.remove(Integer.valueOf(cookie));
            String outRoot = getOutRoot();
            String outNewName = getOutName(name);
            _listener.fileRenamed(getParentWatchID(), outRoot, oldName, outNewName);
        }

        public void notifyFileModified(String name) {
            String outRoot = getOutRoot();
            String outName = getOutName(name);
            _listener.fileModified(getParentWatchID(), outRoot, outName);
        }

        public void notifyFileDeleted(String name) {
            String outRoot = getOutRoot();
            String outName = getOutName(name);
            _listener.fileDeleted(getParentWatchID(), outRoot, outName);
        }


        public void notifyFileCreated(String name) {
            String outRoot = getOutRoot();
            String outName = getOutName(name);
            _listener.fileCreated(getParentWatchID(), outRoot, outName);
        }

        void remveSubwatch(int linuxWd) {
            if (!_subWd.remove(Integer.valueOf(linuxWd))) {
                throw new RuntimeException("Error removing " + linuxWd + " from list");
            }
        }

        void addSubwatch(int linuxWd) {
            _subWd.add(Integer.valueOf(linuxWd));
        }

        public String toString() {
            return "WatchData " + _path + ", wd=" + _wd + ", linuxWd=" + _linuxWd + (_watchSubtree ? ", recursive" : "") + (_user ? ", user" : ", auto");
        }

        private String getOutRoot() {
            String outRoot;
            if (_user) {
                outRoot = _path;
            } else // auto watch.
            {
                outRoot = getParentWatch()._path;
            }
            return outRoot;

        }

        private String getOutName(String name) {
            String outName;
            if (_user) {
                outName = name;
            } else // auto watch.
            {
                outName = _path.substring(getParentWatch()._path.length() + 1);
                if (name != "") {
                    outName += File.separatorChar + name;
                }
            }
            return outName;
        }

        public int getParentWatchID() {
            return _parentWatchData == null ? _wd : _parentWatchData._wd;
        }
    }

    public int unitTest_getNumWatches() {
        return indicesOfWatchdata.size();
    }


}

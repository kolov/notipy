Notipy
======

Java library to detect file system changes that works on the Raspberry Pi (based on inotify)

[JNotify](http://jnotify.sourceforge.net/) didn't work ot of the box on the raspberry Pi, so this is basically a clone of JNotify with some shanges:
 - Got rid of the makefile, plain Maven build. 
 - Doesn't detect the OS, operation mode is controlled explicitely
 - An OS-neutral ineficient implementation for developen (e.g. develop on Mac, deploy on the Pi)

Usage
======

Buld anywhere with 

    mvn clean install
or on the Raspberry Pi with

    mvn clean install -P pi
    
Test anywhere with:

    java -cp target/notipy-1.0-SNAPSHOT.jar com.akolov.notipy.NotipyDemo
Or on the Raspberry Pi with

    java -cp target/notipy-1.0-SNAPSHOT.jar -Dnotipy.mode=inotify com.akolov.notipy.NotipyDemo
    
This starts watching the files in the current directory.

To select the watch implementation:
 - new Notipy() will use the scan implementation by default
 - If environment variable notipy.mode is defined, (values inotify or scan), it will ne honoured
 - Or jus creat new Notipy(Mode.INOTIFY) etc.

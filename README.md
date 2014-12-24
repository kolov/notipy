Notipy
======

Java library to detect file system changes that works on the Raspberry Pi (based on inotify)

[JNotify](http://jnotify.sourceforge.net/) didn't work ot of the box on the Raspberry Pi, so this is basically a clone of JNotify with some changes:
 - Got rid of the makefile, Maven build. 
 - No separate CVS modules, one Maven project
 - Native Library packaged in Jar and loaded automatically, no -Djava.library.path 
 - Doesn't detect the OS, operation mode is controlled explicitely
 - An OS-neutral ineficient implementation for development (e.g. develop on Mac, deploy on the Pi)

Usage
======

Buld anywhere with 

    mvn clean install
or on the Raspberry Pi with

    mvn clean install -P pi
    
Test anywhere with:

    java -cp target/notipy-{version}.jar com.akolov.notipy.NotipyDemo
Or test on the Raspberry Pi with

    java -cp target/notipy-pi-{version}.jar -Dnotipy.mode=inotify com.akolov.notipy.NotipyDemo
    
This starts watching the files in the current directory.

To select the watch implementation:
 - new Notipy() will use the platform-independent inefficient scan implementation by default
 - If environment variable notipy.mode is defined, (values inotify or scan), it will ne honoured
 - Or pass a parameter to constructor: new Notipy(Mode.INOTIFY) etc.

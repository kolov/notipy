Notipy
======

Java library to detect file system changes that works on the Raspberry Pi (based on inotify)

[JNotify](http://jnotify.sourceforge.net/) didn't work ot of the box on the Raspberry Pi, so this is basically a clone of JNotify with some changes:
 - Got rid of the makefile, Maven build. 
 - No separate CVS modules, one github project
 - Native Library packaged in Jar and loaded automatically, no -Djava.library.path 
 - Doesn't detect the OS, operation mode is controlled explicitely
 - An OS-neutral primitive implementation for development (e.g. develop on Mac, deploy on the Pi)

Why
======
I bought a Rapberry Pi with a camera ant I wanted to set it up do watch my aquarium while away, and the only way I found was [mjpg-streamer](http://sourceforge.net/projects/mjpg-streamer/). It works and it is nice, but it is a whole application with UI and everything, and I just wanted a component to use in my own app.

Usage
======

Buld the OS-neutral version anywhere with 

    mvn clean install
or Raspbery Pi native version *on a Raspberry Pi* with

    mvn clean install -P pi
    
or download it on your own risk from [this nexus repo](http://nexus.akolov.com/content/repositories/releases/com/akolov/notipy/notipy/0.1.6/)
    
Test anywhere with:

    java -cp target/notipy-{version}.jar com.akolov.notipy.NotipyDemo
Or test on the Raspberry Pi with

    java -cp target/notipy-pi-{version}.jar -Dnotipy.mode=inotify com.akolov.notipy.NotipyDemo
    
This starts watching the files in the current directory.

To select the watch implementation:
 - new Notipy() will use the platform-independent primitive scan implementation by default
 - If environment variable notipy.mode is defined, (values inotify or scan), it will ne honoured
 - Or pass a parameter to constructor: new Notipy(Mode.INOTIFY) etc.
A full example of a web application streaming an imag from the Pi camera available [here](https://github.com/kolov/goby) 

What about the fish?
======

The web abb app using *notipy* to stream the Raspberry camera output works at the max raspistill speed which is still disappointing:see project [Goby](https://github.com/kolov/goby) on github. 

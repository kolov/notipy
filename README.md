Notipy
======

Java library to detect file system changes that works on the Raspberry Pi (based on inotify)

JNotify didn't work ot of the box on the raspberry Pi, http://jnotify.sourceforge.net/, so 

To Test:

java -cp target/notipy-1.0-SNAPSHOT.jar com.akolov.notipy.NotipyDemo
java -cp target/notipy-1.0-SNAPSHOT.jar -Dnotipy.mode=inotify com.akolov.notipy.NotipyDemo

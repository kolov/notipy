JDK_HOME  = /home/russ/jdk1.6.0_18
CFLAGS    = -I $(JDK_HOME)/include -I $(JDK_HOME)/include/linux
JNI_NAME  = syscall
TARGETDIR = ./target
OBJECTS   = errno_jni.o sne.o system.o
CLASSNAME = com.etretatlogiciels.jni.SystemCall
IDE_PATH  = .
HEADER    = systemcall.h
JAVAH     = $(JDK_HOME)/bin/javah
SOURCEDIR = /mnt/russ/dev/hotchocolate/tutorials/jni


install:
	echo javac src/main/java/com/akolov/notipy/Notipy.java
	javah -d target/c/gen -classpath src/main/java com.akolov.notipy.Notipy_linux
	gcc -I/usr/lib/jvm/java-6-sun/include -I/usr/lib/jvm/java-6-sun/include/linux -O3 -Wall -Werror -c -fmessage-length=0 -fPIC -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -o"$@" "$<"






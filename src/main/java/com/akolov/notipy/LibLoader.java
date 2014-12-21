package com.akolov.notipy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Hello world!
 */
public class LibLoader {
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }


    private static boolean extractAndLoadLibraryFile(
            String libFolderForCurrentOS, String libraryFileName,
            String targetFolder) {
        String nativeLibraryFilePath = libFolderForCurrentOS + "/"
                + libraryFileName;
        final String prefix = "javaaffinity-2-";

        String extractedLibFileName = prefix + libraryFileName;
        File extractedLibFile = new File(targetFolder, extractedLibFileName);

        try {
            if (extractedLibFile.exists()) {
                // test md5sum value
                return loadNativeLibrary(targetFolder, extractedLibFileName);
            }

            // extract file into the current directory
            InputStream reader = LibLoader.class
                    .getResourceAsStream(nativeLibraryFilePath);
            FileOutputStream writer = new FileOutputStream(extractedLibFile);
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, bytesRead);
            }

            writer.close();
            reader.close();

            if (!isWindows()) {
                try {
                    Runtime.getRuntime()
                            .exec(new String[]{"chmod", "755",
                                    extractedLibFile.getAbsolutePath()})
                            .waitFor();
                } catch (Throwable e) {
                }
            }

        } catch (IOException e) {
            // TODO something with exception - don't know what to do with it
            // using JUL

            return false;
        }
        return loadNativeLibrary(targetFolder, extractedLibFileName);

    }

    public static boolean isWindows() {
        return System.getProperty("os.name").contains("Windows");
    }

    private static synchronized boolean loadNativeLibrary(String path,
                                                          String name) {
        File libPath = new File(path, name);

        if (libPath.exists()) {
            String absolutePath = libPath.getAbsolutePath();
            try {
                System.load(absolutePath);
                return true;
            } catch (UnsatisfiedLinkError e) {
                // TODO something with e

                return false;
            }
        } else {
            return false;
        }
    }
}

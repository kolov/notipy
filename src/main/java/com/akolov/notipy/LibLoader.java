package com.akolov.notipy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class LibLoader {

    public static void extractAndLoadLibraryFile(String libraryFileName, boolean doChmod) {
        String nativeLibraryResourcePath = "/" + libraryFileName;
        String tempDir = System.getProperty("java.io.tmpdir");

        File targetFolder = new File(tempDir);
        File extractedLibFile = new File(targetFolder, libraryFileName);

        try {
            if (extractedLibFile.exists()) {
                // test md5sum value
                loadNativeLibrary(tempDir, libraryFileName);
                return;
            }

            // extract file into the current directory
            InputStream reader = LibLoader.class
                    .getResourceAsStream(nativeLibraryResourcePath);
            if (reader == null) {
                throw new RuntimeException("Can't find resource " + nativeLibraryResourcePath);
            }
            FileOutputStream writer = new FileOutputStream(extractedLibFile);
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, bytesRead);
            }

            writer.close();
            reader.close();

            if (doChmod) {
                try {
                    Runtime.getRuntime()
                            .exec(new String[]{"chmod", "755",
                                    extractedLibFile.getAbsolutePath()})
                            .waitFor();
                } catch (Throwable e) {
                    throw new RuntimeException("Can't chmod " + extractedLibFile.getAbsolutePath());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadNativeLibrary(tempDir, libraryFileName);

    }


    private static void loadNativeLibrary(String path, String name) {
        File libPath = new File(path, name);
        String absolutePath = libPath.getAbsolutePath();
        System.out.println("Loading " + absolutePath);
        System.load(absolutePath);
        System.out.println("Loaded " + absolutePath);

    }
}

package com.akolov.notipy;

import java.io.File;
import java.io.FileInputStream;
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
            byte[] lib = readLibrary(nativeLibraryResourcePath);
            if (extractedLibFile.exists()) {
                byte[] buf = new byte[(int) extractedLibFile.length()];
                FileInputStream fis = new FileInputStream(extractedLibFile);
                fis.read(buf);
                if (sameBytes(buf, lib)) {

                    loadNativeLibrary(tempDir, libraryFileName);
                    return;
                }
            }

            // extract file into the current directory

            FileOutputStream writer = new FileOutputStream(extractedLibFile);
            writer.write(lib, 0, lib.length);


            writer.close();

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

    private static boolean sameBytes(byte[] b1, byte[] b2) {
        if (b1.length != b2.length) {
            return false;
        }
        for (int i = 0; i < b1.length; i++) {
            if (b1[i] != b2[i]) {
                return false;
            }
        }
        return true;
    }


    private static byte[] readLibrary(String resourcePath) throws IOException {
        InputStream reader = LibLoader.class
                .getResourceAsStream(resourcePath);
        if (reader == null) {
            throw new RuntimeException("Can't find resource " + resourcePath);
        }
        byte[] buffer = new byte[20000];
        int bytesRead = 0;
        bytesRead = reader.read(buffer);

        reader.close();
        byte[] result = new byte[bytesRead];
        System.arraycopy(buffer, 0, result, 0, bytesRead);
        return result;
    }

    private static void loadNativeLibrary(String path, String name) {
        File libPath = new File(path, name);
        String absolutePath = libPath.getAbsolutePath();
        System.out.println("Loading " + absolutePath);
        System.load(absolutePath);
        System.out.println("Loaded " + absolutePath);

    }
}

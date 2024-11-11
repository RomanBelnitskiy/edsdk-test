package org.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class EDSDKLoader {

    static {
        try {
            loadLibraryFromResource("/library/EDSDK.dll", "EDSDK.dll");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadLibraryFromResource(String resourcePath, String fileName) throws IOException {
        // Locate a temporary directory for the DLL
        Path tempDir = Files.createTempDirectory("nativeLibs");
        tempDir.toFile().deleteOnExit();

        // Specify the path with the original file name
        File tempFile = new File(tempDir.toFile(), fileName);
        tempFile.deleteOnExit();

        // Extract the DLL
        try (InputStream input = EDSDKLoader.class.getResourceAsStream(resourcePath);
             OutputStream output = new FileOutputStream(tempFile)) {
            if (input == null) {
                throw new FileNotFoundException("Resource not found: " + resourcePath);
            }

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        }

        // Load the DLL with the original file name
        System.out.println("Loaded library: " + tempFile.getAbsolutePath());
        System.load(tempFile.getAbsolutePath());
    }
}



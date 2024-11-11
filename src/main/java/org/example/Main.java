package org.example;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class Main {
    public static void main(String[] args) {
        EDSDKLoader start = new EDSDKLoader();
        EDSDKLibrary sdk = EDSDKLibrary.INSTANCE;

        // Initialize the SDK
        if (sdk.EdsInitializeSDK() != 0) {
            System.err.println("Failed to initialize Canon SDK.");
            return;
        }
        System.out.println("Canon SDK initialized successfully.");

        try {
            // Get the camera list
            PointerByReference cameraListRef = new PointerByReference();
            if (sdk.EdsGetCameraList(cameraListRef) != 0) {
                System.err.println("Failed to get camera list.");
                return;
            }

            // Get the first camera
            PointerByReference cameraRef = new PointerByReference();
            if (sdk.EdsGetChildAtIndex(cameraListRef.getValue(), 0, cameraRef) != 0) {
                System.err.println("Failed to get camera.");
                return;
            }

            // Open a session with the camera
            if (sdk.EdsOpenSession(cameraRef.getValue()) != 0) {
                System.err.println("Failed to open camera session.");
                return;
            }
            System.out.println("Camera session opened successfully.");

            // Take a picture
            if (sdk.EdsSendCommand(cameraRef.getValue(), CanonConstants.kEdsCameraCommand_TakePicture, 0) != 0) {
                System.err.println("Failed to take picture.");
                return;
            }
            System.out.println("Picture taken successfully.");

            // Wait for the camera to save the image, then access the directory item
            PointerByReference directoryItemRef = new PointerByReference();
            if (sdk.EdsGetChildAtIndex(cameraRef.getValue(), 0, directoryItemRef) != 0) {
                System.err.println("Failed to access directory item.");
                return;
            }

            // Get the file size of the directory item
            NativeLongByReference fileSizeRef = new NativeLongByReference();
            sdk.EdsGetChildAtIndex(directoryItemRef.getValue(), 0, directoryItemRef);

            // Prepare file stream for download
            File outputFile = new File("captured_image.jpg");
            try (OutputStream outputStream = new FileOutputStream(outputFile)) {

                // Download the image to the specified file
                if (sdk.EdsDownload(directoryItemRef.getValue(), fileSizeRef, Pointer.NULL) != 0) {
                    System.err.println("Failed to download image.");
                } else {
                    System.out.println("Image downloaded successfully to " + outputFile.getAbsolutePath());
                }

            } catch (Exception e) {
                System.err.println("Error saving image: " + e.getMessage());
            }

            // Release directory item and camera list
            sdk.EdsRelease(directoryItemRef.getValue());
            sdk.EdsRelease(cameraListRef.getValue());

            // Close the camera session
            if (sdk.EdsCloseSession(cameraRef.getValue()) != 0) {
                System.err.println("Failed to close camera session.");
            }

        } finally {
            // Terminate the SDK
            if (sdk.EdsTerminateSDK() != 0) {
                System.err.println("Failed to terminate Canon SDK.");
            } else {
                System.out.println("Canon SDK terminated successfully.");
            }
        }
    }
}
package org.example;

import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;

import java.io.File;

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
            int openSessionResult = sdk.EdsOpenSession(cameraRef.getValue());
            if (openSessionResult != 0) {
                System.err.println("Error opening session with code " + openSessionResult);
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
                // Retry (wait for capturing and saving image by camera)
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignore) {}
                if (sdk.EdsGetChildAtIndex(cameraRef.getValue(), 0, directoryItemRef) != 0) {
                    System.err.println("Failed to access directory item.");
                    return;
                }
            }

            // Get the directory item
            sdk.EdsGetChildAtIndex(directoryItemRef.getValue(), 0, directoryItemRef);

            // Prepare file for download
            File outputFile = new File("captured_image.jpg");

            // 2. Create file stream
            PointerByReference streamRef = new PointerByReference();
            sdk.EdsCreateFileStream(
                    outputFile.getAbsolutePath(),
                    CanonConstants.kEdsFileCreateDisposition_CreateAlways,
                    CanonConstants.kEdsAccess_ReadWrite,
                    streamRef);

            // 4. Download image
            NativeLongByReference fileSizeRef = new NativeLongByReference();
            if (sdk.EdsDownload(directoryItemRef.getValue(), fileSizeRef, streamRef.getValue()) != 0) {
                System.err.println("Failed to download image file.");
            }

            // 5. Complete download
            sdk.EdsDownloadComplete(streamRef.getValue());

            System.out.println("Image downloaded successfully to " + outputFile.getAbsolutePath());


            // Release directory item and camera list
            sdk.EdsRelease(streamRef.getValue());
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
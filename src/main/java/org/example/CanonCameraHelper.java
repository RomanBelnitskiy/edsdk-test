package org.example;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.example.CanonConstants.*;
import static org.example.CanonConstants.kEdsAccess_ReadWrite;

public class CanonCameraHelper {

    private static final Logger log = LogManager.getLogger(CanonCameraHelper.class);

    private EDSDKLoader start = new EDSDKLoader();
    private EDSDKLibrary sdk = EDSDKLibrary.INSTANCE;

    private PointerByReference cameraRef = new PointerByReference();
    private boolean isSessionOpen = false;

    private void init() {
        // Step 1: Initialize SDK
        int result = sdk.EdsInitializeSDK();
        if (result != 0) {
            log.info("Failed to initialize Canon SDK.");
            return;
        }
        log.info("Canon SDK initialized successfully.");

        // Step 2: Get camera list and first connected camera
        PointerByReference cameraListRef = new PointerByReference();
        result = sdk.EdsGetCameraList(cameraListRef);
        if (result != 0) {
            log.info("Failed to get camera list.");
            return;
        }

        result = sdk.EdsGetChildAtIndex(cameraListRef.getValue(), 0, cameraRef);
        if (result != 0) {
            log.info("Failed to get camera.");
            sdk.EdsRelease(cameraRef.getValue());
        }
        sdk.EdsRelease(cameraListRef.getValue());
        log.info("Canon SDK initialized successfully.");
    }

    public void openSession() {
        init();

        // Step 3: Open session
        int result = sdk.EdsOpenSession(cameraRef.getValue());
        if (result != 0) {
            log.info("Error opening session with code {}", result);
            log.info("Failed to open camera session.");
            return;
        }
        log.info("Camera session opened successfully.");

        // Step 4: (Optional) Adjust camera settings, if necessary
        result = sdk.EdsSetPropertyData(cameraRef.getValue(), kEdsPropID_AFMode, 0, Native.getNativeSize(Integer.class), new IntByReference(1));
        if (result != 0) {
            log.info("Failed to set autofocus mode. Error code: {}", result);
        } else {
            log.info("Autofocus mode set to Single Shot AF successfully.");
        }
        isSessionOpen = true;
    }

    public void takePicture() {
        if (!isSessionOpen) {
            log.info("Couldn't take picture, session not open");
            return;
        }

        // Step 5: Capture image
        int result = sdk.EdsSendCommand(cameraRef.getValue(), kEdsCameraCommand_TakePicture, 0);
        if (result != 0) {
            log.info("Failed to take picture. Error: {}", result);
            return;
        }
        log.info("Picture taken successfully.");

        // Step 6: Wait for image to be available (e.g., using a delay or event listener)
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignore) {}

        // Step 7: Access the volume (memory card)
        PointerByReference volumeRef = new PointerByReference();
        result = sdk.EdsGetChildAtIndex(cameraRef.getValue(), 0, volumeRef);
        if (result != 0) {
            log.info("Failed to take volume. Error: {}", result);
            return;
        }
        log.info("Volume taken successfully.");

        PointerByReference dirDcimItemRef = new PointerByReference();
        result = sdk.EdsGetChildAtIndex(volumeRef.getValue(), 0, dirDcimItemRef);
        if (result != 0) {
            log.info("Failed to retrieve DCIM item. Error: {}", result);
        }
        log.info("DCIM Directory item retrieved successfully.");

        PointerByReference dir100CanonItemRef = new PointerByReference();
        result = sdk.EdsGetChildAtIndex(dirDcimItemRef.getValue(), 0, dir100CanonItemRef);
        if (result != 0) {
            log.info("Failed to retrieve 100CANON item. Error: {}", result);
        }
        log.info("100CANON Directory item retrieved successfully.");

        LongByReference imageCount = new LongByReference();
        result = sdk.EdsGetChildCount(dir100CanonItemRef.getValue(), imageCount);
        if (result != 0) {
            log.info("Failed to retrieve images from 100CANON directory. Error: {}", result);
            return;
        }
        log.info("Images count: {}", imageCount.getValue());

        // Get ref for image
        PointerByReference imageItemRef = new PointerByReference();
        result = sdk.EdsGetChildAtIndex(dir100CanonItemRef.getValue(), (int)(imageCount.getValue() - 1), imageItemRef);
        if (result != 0) {
            log.info("Failed to retrieve image item. Error: {}", result);
        }

        // Get image info
        EdsDirectoryItemInfo imageItemInfo = new EdsDirectoryItemInfo();
        result = sdk.EdsGetDirectoryItemInfo(imageItemRef.getValue(), imageItemInfo);
        if (result != 0 || imageItemInfo.size == 0) {
            log.info("Failed to retrieve image item info or size is 0. Error: {}", result);
            log.info("Image file size: {}", imageItemInfo.size);
            log.info("Image file name: {}", new String(imageItemInfo.szFileName));
            sdk.EdsRelease(imageItemRef.getValue());  // Release invalid ref
        }
        log.info("Image file size: {}", imageItemInfo.size);

        // Check if this is an image file
        if (imageItemInfo.size > 0) {
            // Step 10: Download the image
            PointerByReference outStream = new PointerByReference();
            result = sdk.EdsCreateFileStream("captured_image.jpg",
                    kEdsFileCreateDisposition_CreateAlways,
                    kEdsAccess_ReadWrite,
                    outStream);
            if (result != 0) {
                log.info("Failed to create output stream. Error: {}", result);
            }

            result = sdk.EdsDownload(imageItemRef.getValue(), new NativeLong(imageItemInfo.size), outStream.getValue());
            if (result != 0) {
                log.info("Failed to download image. Error: {}", result);
            }

            result = sdk.EdsDownloadComplete(imageItemRef.getValue());
            if (result != 0) {
                log.info("Failed to download complete. Error: {}", result);
            }

            // Release the file stream
            sdk.EdsRelease(outStream.getValue());
        }
        // Release the image reference
        sdk.EdsRelease(imageItemRef.getValue());


        // Release directory item reference
        sdk.EdsRelease(dir100CanonItemRef.getValue());
        sdk.EdsRelease(dirDcimItemRef.getValue());

        // Step 11: Release volume reference
        sdk.EdsRelease(volumeRef.getValue());
    }

    public void closeSession() {
        isSessionOpen = false;
        // Step 12: Close session and release resources
        sdk.EdsCloseSession(cameraRef.getValue());
        sdk.EdsRelease(cameraRef.getValue());

        // Terminate the SDK
        if (sdk.EdsTerminateSDK() != 0) {
            log.info("Failed to terminate Canon SDK.");
        } else {
            log.info("Canon SDK terminated successfully.");
        }
    }
}

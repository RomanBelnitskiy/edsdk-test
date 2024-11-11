package org.example;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;

public interface EDSDKLibrary extends Library {

    EDSDKLibrary INSTANCE = (EDSDKLibrary) Native.load("EDSDK.dll", EDSDKLibrary.class);


    int EdsInitializeSDK();
    int EdsTerminateSDK();

    int EdsGetCameraList(PointerByReference cameraList);
    int EdsOpenSession(Pointer camera);
    int EdsCloseSession(Pointer camera);
    int EdsRelease(Pointer camera);

    // Command to take a picture, use constant for kEdsCameraCommand_TakePicture
    int EdsSendCommand(Pointer camera, int command, int param);

    // New methods for image download
    int EdsGetChildAtIndex(Pointer camera, int index, PointerByReference directoryItem);
    int EdsDownload(Pointer directoryItem, NativeLongByReference fileSize, Pointer outputStream);

    int EdsCreateFileStream(String inFileName, int inCreateDisposition, int inDesiredAccess, PointerByReference outStream);

    void EdsDownloadComplete(Pointer value);
}

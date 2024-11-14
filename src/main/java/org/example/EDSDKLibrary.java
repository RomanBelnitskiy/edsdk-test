package org.example;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;

public interface EDSDKLibrary extends Library {

    EDSDKLibrary INSTANCE = (EDSDKLibrary) Native.load("EDSDK.dll", EDSDKLibrary.class);


    int EdsInitializeSDK();
    int EdsTerminateSDK();
    int EdsGetCameraList(PointerByReference outCameraListRef);
    int EdsOpenSession(Pointer inCameraRef);
    int EdsCloseSession(Pointer inCameraRef);
    int EdsRelease(Pointer inRef);
    int EdsSendCommand(Pointer inCameraRef, int inCommand, int inParam);
    int EdsGetChildAtIndex(Pointer inRef, int inIndex, PointerByReference outRef);
    int EdsGetChildCount(Pointer inRef, LongByReference outCount );
    int EdsDownload(Pointer inDirItemRef, NativeLong inReadSize, Pointer outStream);
    int EdsDownloadCancel(Pointer inDirItemRef);
    int EdsDownloadComplete(Pointer inDirItemRef);
    int EdsCreateFileStream(String inFileName, int inCreateDisposition, int inDesiredAccess, PointerByReference outStream);
    int EdsSetProgressCallback(Pointer inRef, EdsProgressCallback inProgressCallback, int inProgressOption, Pointer inContext);
    int EdsGetDirectoryItemInfo(Pointer inDirItemRef, EdsDirectoryItemInfo outDirItemInfo);
    int EdsCreateEvfImageRef(Pointer inStreamRef, PointerByReference outEvfImageRef);
    int EdsDownloadEvfImage(Pointer inCameraRef, Pointer inEvfImageRef);
    int EdsSetPropertyData(Pointer inRef, int inPropertyID, int inParam, int inPropertySize, IntByReference inPropertyData);
    int EdsCreateMemoryStream(long inBufferSize, Pointer outStream );
    int EdsCreateMemoryStreamFromPointer(Pointer inUserBuffer, NativeLong inBufferSize, PointerByReference outStream);
}

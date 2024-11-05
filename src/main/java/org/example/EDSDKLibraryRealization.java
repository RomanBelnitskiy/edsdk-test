package org.example;

import com.sun.jna.Native;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class EDSDKLibraryRealization {

    static {
        String url = "C:\\Project\\PLWORK\\SDK\\Project\\src\\main\\resources\\library\\EDSDK.dll";//exportLibrary("EDSDK.dll");
        System.load(url.replace("\\", "/"));
    }

}

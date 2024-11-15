package org.example;

public class Main {
    public static void main(String[] args) {
        CanonCameraHelper camera = new CanonCameraHelper();
        camera.openSession();
        camera.takePicture();
        camera.closeSession();
    }
}
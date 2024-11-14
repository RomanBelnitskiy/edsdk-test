package org.example;

public class CanonConstants {
    public static final int kEdsCameraCommand_TakePicture =             0x00000000; //получает картинку - включает сделать фото
    public static final int kEdsCameraCommand_ExtendShutDownTimer =     0x00000001;
    public static final int kEdsCameraCommand_BulbStart =               0x00000002;
    public static final int kEdsCameraCommand_BulbEnd =                 0x00000003;
    public static final int kEdsCameraCommand_DoEvfAf =                 0x00000102;
    public static final int kEdsCameraCommand_DriveLensEvf =            0x00000103;
    public static final int kEdsCameraCommand_DoClickWBEvf =            0x00000104;
    public static final int kEdsCameraCommand_MovieSelectSwON =         0x00000107;
    public static final int kEdsCameraCommand_MovieSelectSwOFF =        0x00000108;

    public static final int kEdsCameraCommand_PressShutterButton =      0x00000004; //сделать фото
    public static final int kEdsCameraCommand_RequestRollPitchLevel =   0x00000109;
    public static final int kEdsCameraCommand_DrivePowerZoom =          0x0000010d;
    public static final int kEdsCameraCommand_SetRemoteShootingMode =   0x0000010f;
    public static final int kEdsCameraCommand_RequestSensorCleaning =   0x00000112;
    public static final int kEdsCameraCommand_SetModeDialDisable =      0x00000113;

                                                                                    // начало видео\ конец

    public static final int kEdsAccess_Read             = 0;
    public static final int kEdsAccess_Write            = 1;
    public static final int kEdsAccess_ReadWrite        = 2;

    public static final int kEdsFileCreateDisposition_CreateNew         = 0;
    public static final int kEdsFileCreateDisposition_CreateAlways      = 1;
    public static final int kEdsFileCreateDisposition_OpenExisting      = 2;
    public static final int kEdsFileCreateDisposition_OpenAlways        = 3;
    public static final int kEdsFileCreateDisposition_TruncateExisting  = 4;

    public static final int kEdsProgressOption_Done             = 1;
    public static final int kEdsProgressOption_Periodically = 2;

    public static final int kEdsPropID_AFMode = 0x00000404;
    public static final int kEdsAFMode_Manual = 0x00000003;
    public static final int kEdsPropID_ContinuousAfMode = 0x01000433;
}

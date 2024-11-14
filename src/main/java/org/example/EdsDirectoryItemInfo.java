package org.example;

import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;

public class EdsDirectoryItemInfo extends Structure {
    public long size;                 // EdsUInt64 - 8 bytes
    public int isFolder;              // EdsBool - assumed 4 bytes
    public int groupID;               // EdsUInt32 - 4 bytes
    public int option;                // EdsUInt32 - 4 bytes
    public byte[] szFileName = new byte[256];  // EdsChar[EDS_MAX_NAME]
    public int format;                // EdsUInt32 - 4 bytes
    public int dateTime;              // EdsUInt32 - 4 bytes

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("size", "isFolder", "groupID", "option", "szFileName", "format", "dateTime");
    }
}

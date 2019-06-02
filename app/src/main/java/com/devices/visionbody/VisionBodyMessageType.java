package com.devices.visionbody;

public class VisionBodyMessageType {
    public static final byte LOAD_PROGRAM_COMMAND = 2;
    public static final byte START_PROGRAM_COMMAND = 7;
    public static final byte STOP_PROGRAM_COMMAND = 8;
    public static final byte SET_INTENSITY_COMMAND = 9;
    public static final byte GET_INTENSITY_COMMAND = 10;
    public static final byte GET_BATTERY_LEVEL_COMMAND = 13;
    public static final byte IDENTIFY_COMMAND = 14;
    public static final byte SYNCHRONIZE_COMMAND = 17;
    public static final byte GET_FIRMWARE_VERSION_COMMAND = 18;
    public static final byte BEGIN_IMAGE_TRANSFER_COMMAND = 100;
    public static final byte TRANSFER_IMAGE_CHUNK_RCOMMAND = 101;
    public static final byte END_IMAGE_TRANSFER_COMMAND = 103;
    public static final byte LOGIN_COMMAND = 113;
}

package com.devices.lock;

public class LockConstants {
    public static final byte BLELOCK_STATE_CLOSE = (byte) 0;
    public static final byte BLELOCK_STATE_OPEN = (byte) 1;
    public static final byte CLOSE_BLELOCK = (byte) 2;
    public static final byte COMMAND_AUTH_PWD = (byte) 65;
    public static final byte COMMAND_ENERGY = (byte) 32;
    public static final byte COMMAND_KEY_OPERATE_PWD = (byte) 80;
    public static final byte COMMAND_OPERATE_LOCK = (byte) 16;
    public static final byte COMMAND_SETTING_PWD_MODE = (byte) 96;
    public static final byte COMMAND_SET_PASSWORD = (byte) 64;
    public static final byte COMMAND_STATUS = (byte) 48;
    public static final byte OPEN_BLELOCK = (byte) 1;
    public static final byte REQUEST_ID = (byte) 85;
    public static final byte RESPOND_ID = (byte) -86;
}

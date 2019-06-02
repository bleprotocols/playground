package com.devices.visionbody;

public class VisionBodyMessageKind {
    public static final byte MESSAGE_KIND_CMD = 0; //A command message. We send these.
    public static final byte MESSAGE_KIND_ACK = 1; //An acknowledge message, response to a command
    public static final byte MESSAGE_KIND_RSP = 2; //A response message, response to a command
    public static final byte MESSAGE_KIND_EVT = 3;//An event sent from the box. We use an event filter to catch these.
}

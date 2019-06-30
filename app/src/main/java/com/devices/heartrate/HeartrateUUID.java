package com.devices.heartrate;

import java.util.UUID;

public class HeartrateUUID {
    //Main service to control the VisionBody box.
    public static final UUID HEARTRATE_SERVICE = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");
    //these are channels on the visionBody service
    public static final UUID HEARTRATE_RX_SERVICE = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");// tx channel
}

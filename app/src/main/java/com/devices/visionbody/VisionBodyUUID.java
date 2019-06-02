package com.devices.visionbody;

import java.util.UUID;

/**
 * Contains the bluetooth UUIDs for the VisionBody box's services.
 * Each of the UUIDs is hard-coded in the original software, these are unlikely to change in future versions.
 */
public class VisionBodyUUID {
    //Main service to control the VisionBody box.
    public static final UUID VISION_BODY_BOX_SERVICE = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    //these are channels on the visionBody service
    public static final UUID VISION_BODY_TX_CHANNEL = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");// tx channel
    public static final UUID VISION_BODY_RX_CHANNEL = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");// rx channel

    //Box information Service
    public static final UUID DEVICE_INFORMATION_SERVICE = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    //characteristics for this service
    public static final UUID HARDWARE_REVISION_CHANNEL = UUID.fromString("00002A27-0000-1000-8000-00805f9b34fb"); //Hardware revision characteristic
    public static final UUID MODEL_NUMBER_CHANNEL = UUID.fromString("00002A24-0000-1000-8000-00805f9b34fb"); //Model number characteristic
}

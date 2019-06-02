package com.devices.lovesense;

import java.util.UUID;

public class LovensenseUUID {
    //Main service to control the VisionBody box.
    public static final UUID LOVESENSE_TOY_SERVICE = UUID.fromString("5a300001-0023-4bd4-bbd5-a6920e4c5653");
    //these are channels on the visionBody service
    public static final UUID LOVESENSE_BODY_TX_CHANNEL = UUID.fromString("5a300002-0023-4bd4-bbd5-a6920e4c5653");// tx channel
    public static final UUID LOVESENSE_BODY_RX_CHANNEL = UUID.fromString("5a300003-0023-4bd4-bbd5-a6920e4c5653");// rx channel
}

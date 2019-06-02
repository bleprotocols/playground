package com.ble;

import java.util.UUID;

public class GattConstants {
    public static final long BLUETOOTH_TIMEOUT = 2000;
    public static final long CONNECTION_TIMEOUT = 500;
    public static final long DISCOVER_SERVICES_TIMEOUT = 150;

    public static final int MAX_MESSAGE_BUFFER_SIZE = 4096;
    public static final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");// client caracteristics channel, set on rx channel.

}

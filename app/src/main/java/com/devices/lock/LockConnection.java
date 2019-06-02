package com.devices.lock;

import com.ble.GattDeviceConnection;

import java.util.Arrays;

import static com.devices.lock.LockConstants.*;
import static com.devices.lock.LockUUID.*;


public abstract class LockConnection extends GattDeviceConnection {
    public LockConnection() {
        super();
        this.setServiceUUID(RX_SERVICE_UUID)
                .setTxUUID(TX_CHAR_UUID)
                .setRxUUID(RX_CHAR_UUID)
                .setConnectionTimeout(300)
                .setTxRxTimeout(500);
    }


    protected boolean sendPacket(byte command, byte[] parameter) {
        byte[] packet = new byte[3 + parameter.length];
        packet[0] = REQUEST_ID;
        packet[1] = command;
        System.arraycopy(parameter, 0, packet, 2, parameter.length);
        packet[parameter.length + 2] = checkSum(packet, packet.length - 1);

        return this.write(packet);
    }

    private static byte checkSum(byte[] data) {
        return checkSum(data, data.length);
    }

    private static byte checkSum(byte[] data, int length) {
        byte checksum = (byte) 0;
        for (int i = 0; i < length; i++) {
            checksum = (byte) (data[i] ^ checksum);
        }
        return checksum;
    }

    protected boolean recvPacket(byte id) {
        byte header[] = this.read(2);
        byte payload[];


        if (header.length != 2 || header[0] != RESPOND_ID) {
            getLogger().println("LockConnection::recvPacket("+id+"): Invalid lock response");

            return false;
        }

        switch (header[1]) {
            case COMMAND_ENERGY:
                payload = this.read(2);

                if (payload == null || payload.length != 2 || payload[1] != (checkSum(header) ^ payload[0])) {
                    getLogger().println("LockConnection::recvPacket("+id+"): Invalid lock battery power message");
                    return false;
                }

                getLogger().println("LockConnection::recvPacket("+id+"): Lock battery power: " + (int) payload[0]);
                break;
            case COMMAND_STATUS:
                payload = this.read(2);

                if (payload == null || payload.length != 2 || payload[1] != (checkSum(header) ^ payload[0])) {
                    getLogger().println("LockConnection::recvPacket("+id+"): Invalid lock status message");
                    return false;
                }

                getLogger().println("LockConnection::recvPacket("+id+"): Lock status: " + (int) payload[0]);
                break;
            case COMMAND_SET_PASSWORD:
                payload = this.read(4);
                if (payload == null || payload.length != 4 || payload[6] != (checkSum(header) ^ checkSum(Arrays.copyOf(payload, 3)))) {
                    getLogger().println("LockConnection::recvPacket("+id+"): Invalid set password message");
                    return false;
                }
                break;
            case COMMAND_KEY_OPERATE_PWD:
                payload = this.read(7);

                if (payload == null || payload.length != 7 || payload[1] != (checkSum(header) ^ checkSum(payload, 6))) {
                    getLogger().println("LockConnection::recvPacket("+id+"): Invalid set key message");
                    return false;
                }

                break;

            case COMMAND_AUTH_PWD:
                payload = this.read(1);

                if (payload[0] == (byte) -21) {
                    //read 1 byte + checksum
                    this.read(1);
                    getLogger().println("LockConnection::recvPacket("+id+"): Authentication failed: invalid password");
                } else {
                    //Read 3 bytes + checksum
                    this.read(7);
                    getLogger().println("LockConnection::recvPacket("+id+"): Authentication succesful.");
                }

                break;
        }


        return id == header[1];
    }

}

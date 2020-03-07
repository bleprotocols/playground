package com.devices.relay;

import android.bluetooth.BluetoothDevice;

import com.ble.GattDeviceConnection;
import com.devices.lovesense.LovensenseUUID;

import static com.common.Common.doUntil;
import static com.common.Common.doUntilTrue;
import static com.common.Common.sleep;

public class RelayConnection extends GattDeviceConnection {


    @Override
    protected void onConnect() {
    }

    @Override
    protected void onDisconnect() {

    }


    public RelayConnection() {
        super();
        this.setServiceUUID(RelayUUID.f5661b)
                .setTxUUID(RelayUUID.f5660a)
                .setRxUUID(RelayUUID.f5660a)
                .setConnectionTimeout(5000)
                .setTxRxTimeout(2000)
                .setAutoReconnect(false);
    }

    @Override
    public boolean isDevice(BluetoothDevice result) {
        return result.getName().startsWith("ZL-RELAY08");
    }

    public boolean sendCommand(byte i) {
        byte[] bArr = {-59, i, '1', '2', '3', '4', '5', '6', '7', '8', -86};
        return doUntilTrue(this, x -> x.write(bArr), 5000);
    }


}

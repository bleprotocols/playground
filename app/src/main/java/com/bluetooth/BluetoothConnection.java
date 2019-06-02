package com.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.devices.erostek.ErostekUUID;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import static com.common.Common.sleep;

public abstract class BluetoothConnection {
    //Parameters set by our getters/setters
    private PrintStream logger;
    private String deviceAddress;

    //Variables filled when we connect to our device
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;


    private Object rxLock = new Object();
    private Object txLock = new Object();

    //Getters and setters
    public BluetoothConnection setLogger(PrintStream logger) {
        this.logger = logger;
        return this;
    }

    protected PrintStream getLogger() {
        return this.logger;
    }

    public BluetoothConnection setaddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
        return this;
    }

    protected abstract void onConnect();


    protected boolean connect() {
        if (null != socket && socket.isConnected()) {
            return true;
        }

        if (null == bluetoothAdapter) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (null == bluetoothAdapter) {
            return false;
        }

        BluetoothDevice mmDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);

        try {
            socket = mmDevice.createRfcommSocketToServiceRecord(ErostekUUID.SERIAL_PORT_UUID);
            socket.connect();

            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();

            this.onConnect();
            return true;
        } catch (Exception ex) {
            return false;
        }

    }

    protected boolean write(byte buffer[]) {
        if (!connect()) {
            return false;
        }

        synchronized (txLock) {
            try {
                outputStream.write(buffer);
                outputStream.flush();
                return true;
            } catch (Exception ex) {
                close();
            }
            return false;
        }

    }


    protected boolean read(byte[] output, int timeout) {
        if (!connect()) {
            return false;
        }

        synchronized (rxLock) {
            long start = System.currentTimeMillis();
            int offset = 0;

            while (true) {
                try {
                    if (inputStream.available() > 0) {
                        int read = inputStream.read(output, offset, Math.min(output.length, inputStream.available()));

                        offset += read;

                        if (offset >= output.length) {
                            return true;
                        }
                    }
                } catch (Exception ex) {
                    close();
                }

                if ((System.currentTimeMillis() - start) > timeout) {
                    return false;
                }

                sleep(10);
            }
        }
    }


    protected void close() {
        try {
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (Exception ex) {
        }
    }

}


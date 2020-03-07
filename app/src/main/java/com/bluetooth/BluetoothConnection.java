package com.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.devices.et312b.Et312BUUID;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.common.Common.sleep;
import static com.common.Common.wrap;

public abstract class BluetoothConnection implements com.bluetooth.BluetoothDevice {
    //Parameters set by our getters/setters
    private PrintStream logger;
    private String deviceAddress;
    private Context context;

    //Variables filled when we connect to our device
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;

    private boolean isConnected = false;
    private Object rxLock = new Object();
    private Object txLock = new Object();

    private long sinceLastConnect = System.currentTimeMillis();
    ExecutorService callbacksOnEvents = Executors.newSingleThreadExecutor();

    @Override
    public void setLogger(PrintStream logger) {
        this.logger = logger;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void setaddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }


    protected Context getContext() {
        return this.context;
    }

    protected PrintStream getLogger() {
        return this.logger;
    }


    protected abstract void onConnect();


    protected synchronized boolean connect() {
        if (isConnected) {
            return true;
        }

        sleep(1000 - (System.currentTimeMillis() - sinceLastConnect));
        sinceLastConnect = System.currentTimeMillis();

        if (null == bluetoothAdapter) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (null == bluetoothAdapter) {
            return false;
        }

        BluetoothDevice mmDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);

        if (mmDevice == null) {
            return false;
        }

        try {
            socket = mmDevice.createRfcommSocketToServiceRecord(Et312BUUID.SERIAL_PORT_UUID);

            if (socket.isConnected()) {
                return false;
            }

            socket.connect();

            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();

            isConnected = true;

            callbacksOnEvents.submit(wrap(()->this.onConnect()));

            return true;
        } catch (Exception ex) {
            return false;
        }

    }

    protected synchronized boolean write(byte buffer[]) {
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


    protected synchronized boolean read(byte[] output, int timeout) {
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


    protected synchronized void close() {
        try {
            outputStream.close();
            inputStream.close();
            socket.close();
            isConnected = false;
        } catch (Exception ex) {
        }
    }

}


package com.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import com.common.ByteBuffer;
import com.common.ReturnValue;
import com.common.ThreadsafeBoolean;

import java.io.PrintStream;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.bluetooth.BluetoothProfile.GATT;
import static com.ble.GattConstants.*;
import static com.common.Common.*;
import static com.common.CommonConstants.*;


/*
 * Class that does a bluetooth GATT device connection much in the same way a standard serial port connection is handled.
 * It's thread-safe, and anything called during the connection lifetime is run on a different thread.
 *
 * This class handles reconnects automatically, and much of the complexity of dealing with BLE is hidden by it.
 * The downside is that it's not *that* flexible supporting only one TX/RX channel.
 * For most devices this however is enough.
 */
public abstract class GattDeviceConnection extends BluetoothGattCallback implements com.bluetooth.BluetoothDevice {
    //Variables that are set directly via getters/setters
    private Context context;
    private String deviceAddress;
    private PrintStream logger;

    private boolean autoReconnect;

    private UUID txUUID;
    private UUID rxUUID;
    private UUID serviceUIID;
    private long connectionTimeout = CONNECTION_TIMEOUT;
    private long txRxTimeout = BLUETOOTH_TIMEOUT;
    private long discoverServicesTimeout = DISCOVER_SERVICES_TIMEOUT;


    //Variables initialized to interface with android's BLE stack.
    private BluetoothGatt bleGatt;
    private BluetoothDevice device;
    private BluetoothManager bluetoothManager;


    //Streams that can temporarily store read buffers
    private ByteBuffer rxStream = new ByteBuffer(MAX_MESSAGE_BUFFER_SIZE);
    private ByteBuffer characteristicStream = new ByteBuffer(MAX_MESSAGE_BUFFER_SIZE);

    //Return values we can await.
    private ReturnValue txReturnValue = new ReturnValue();

    private ThreadsafeBoolean isConnecting = new ThreadsafeBoolean();
    private ReturnValue connectReturnValue = new ReturnValue();

    private ThreadsafeBoolean isDiscoveringServices = new ThreadsafeBoolean();
    private ReturnValue discoverServicesReturnValue = new ReturnValue();

    private ThreadsafeBoolean isSubscribingToRXChannel = new ThreadsafeBoolean();
    private ReturnValue subscribeRxReturnValue = new ReturnValue();


    //We own two threads. One for actions we perform internally on events that happen on the connection
    //And one for the functions that child classes can override.
    ExecutorService actionsOnEvents = Executors.newSingleThreadExecutor();
    ExecutorService callbacksOnEvents = Executors.newSingleThreadExecutor();

    //Locks for TX and RX. Only one thread should transmit or recieve at a time to avoid
    //one thread reading another's response or writing during anothers' write.
    private final Object rxLock = new Object();
    private final Object txLock = new Object();


    //methods our device-specific child classes should override.
    protected abstract void onConnect();

    protected abstract void onDisconnect();


    //Getters/setters go here:
    @Override
    public void setLogger(PrintStream logger) {
        this.logger = logger;
    }

    @Override
    public void setContext(Context context) {
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.context = context;
    }

    @Override
    public void setaddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public GattDeviceConnection setTxUUID(UUID uuid) {
        this.txUUID = uuid;
        return this;
    }

    public GattDeviceConnection setRxUUID(UUID uuid) {
        this.rxUUID = uuid;
        return this;
    }

    public GattDeviceConnection setServiceUUID(UUID uuid) {
        this.serviceUIID = uuid;
        return this;
    }

    public GattDeviceConnection setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public GattDeviceConnection setTxRxTimeout(long txRxTimeout) {
        this.txRxTimeout = txRxTimeout;
        return this;
    }

    public GattDeviceConnection setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
        return this;
    }

    public GattDeviceConnection setDiscoverServicesTimeout(long discoverServicesTimeout) {
        this.discoverServicesTimeout = discoverServicesTimeout;
        return this;
    }


    protected PrintStream getLogger() {
        return logger;
    }

    protected Context getContext() {
        return this.context;
    }

    //Socket lifecycle operations. First we construct it, then we connect.
    //On first connection we discover the services, which we then subscribe to.
    //Discovering services needs to be done only once for each GATT device
    public GattDeviceConnection() {
        logger = null;
        deviceAddress = null;
        context = null;
        bleGatt = null;
        device = null;
        autoReconnect = true;
    }

    protected synchronized boolean connect() {
        //are we still connecting? if yes: do nothing
        if (isConnecting.get()) {
            if (0 == connectReturnValue.await(connectionTimeout)) {
                return true;
            } else {
                isConnecting.set(false);
            }
        }


        int connectionValue = connectReturnValue.await(connectionTimeout);

        //are we just pretending to be connected? if yes: reconnect.
        if (0 == connectionValue && (BluetoothProfile.STATE_CONNECTED != bluetoothManager.getConnectionState(device, GATT))) {
            connectionValue = -1;
        }

        if (0 == connectionValue) {
            return true;
        }

        //Now we really are connecting
        isConnecting.set(true);
        connectReturnValue.reset();


        if (null == logger || null == deviceAddress || null == context || null == bluetoothManager) {
            isConnecting.set(false);
            return false;
        }


        BluetoothAdapter adapter = bluetoothManager.getAdapter();

        if (null == adapter) {
            isConnecting.set(false);
            return false;
        }


        if (null == device) {
            device = adapter.getRemoteDevice(deviceAddress);
        }

        if (null == device) {
            isConnecting.set(false);
            return false;
        }


        if(null != bleGatt ){
            bleGatt.close();
        }


        bleGatt = device.connectGatt(context, autoReconnect, this);

        if (null == bleGatt) {
                getLogger().println("Can't connect to " + device.getName());
                isConnecting.set(false);
                return false;

        } else {
            if (!bleGatt.connect()) {
                isConnecting.set(false);
                return false;
            }
        }

        int ret = connectReturnValue.await(connectionTimeout);
        isConnecting.set(false);
        return ret == 0;
    }


    private void doDiscoverServices() {
        if (!isDiscoveringServices.get() && 0 != discoverServicesReturnValue.await(discoverServicesTimeout)) {
            isDiscoveringServices.set(true);
            discoverServicesReturnValue.reset();
            actionsOnEvents.submit(wrap(bleGatt::discoverServices));
        }
    }

    protected boolean setSubscribeToRxChannel() {
        if (!connect()) {
            return false;
        }

        if (isSubscribingToRXChannel.get()) {
            return 0 == subscribeRxReturnValue.await(connectionTimeout);
        }

        if (0 == subscribeRxReturnValue.await(connectionTimeout)) {
            return true;
        }

        if (isDiscoveringServices.get()) {
            discoverServicesReturnValue.await(connectionTimeout);
        }

        isSubscribingToRXChannel.set(true);
        subscribeRxReturnValue.reset();

        BluetoothGattService gattService = bleGatt.getService(serviceUIID);

        if (null == gattService) {
            actionsOnEvents.submit(this::doDiscoverServices);

            gattService = bleGatt.getService(serviceUIID);

            if (null == gattService) {
                subscribeRxReturnValue.doReturn(-1);
isSubscribingToRXChannel.set(false);
                return false;
            }
        }

        BluetoothGattCharacteristic channel = gattService.getCharacteristic(rxUUID);

        if (null == channel) {
            subscribeRxReturnValue.doReturn(-1);
            isSubscribingToRXChannel.set(false);
            return false;
        }

        bleGatt.setCharacteristicNotification(channel, true);

        //subscribe to client characteristic notifications
        BluetoothGattDescriptor descriptor = channel.getDescriptor(CCCD);


        if (null == descriptor) {
            subscribeRxReturnValue.doReturn(0);
            isSubscribingToRXChannel.set(false);
            return true;
        }

        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

        return bleGatt.writeDescriptor(descriptor);
    }

    public void close() {
        isConnecting.set(false);
        connectReturnValue.reset();
        isSubscribingToRXChannel.set(false);
        subscribeRxReturnValue.reset();
        isDiscoveringServices.set(false);
        discoverServicesReturnValue.reset();


       if (null != bleGatt) {
            bleGatt.close();
        }
    }


    //GATT callbacks. These get called when something changed in our bluetooth connection.
    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        txReturnValue.doReturn(status);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        rxStream.push(characteristic.getValue());
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        characteristicStream.push(characteristic.getValue());
    }

    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        isSubscribingToRXChannel.set(false);
        subscribeRxReturnValue.doReturn(0);
    }


    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        isDiscoveringServices.set(false);
        discoverServicesReturnValue.doReturn(status);
    }


    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            rxStream.reset();
            characteristicStream.reset();
            isSubscribingToRXChannel.set(false);
            subscribeRxReturnValue.reset();
            isConnecting.set(false);
            connectReturnValue.doReturn(0);
            callbacksOnEvents.submit(wrap(this::onConnect));

            this.doDiscoverServices();
        } else {
            isConnecting.set(false);
            connectReturnValue.reset();
            isSubscribingToRXChannel.set(false);
            subscribeRxReturnValue.reset();
            isDiscoveringServices.set(false);
            discoverServicesReturnValue.reset();

            callbacksOnEvents.submit(wrap(this::onDisconnect));
        }
    }

    protected byte[] read(int length) {
        return read(length,txRxTimeout);
    }

    //R/W operations. These are the functions that do the actual communication.
    protected byte[] read(int length, long timeout) {
        if (!setSubscribeToRxChannel()) {
            return EMPTY_ARRAY;
        }

        synchronized (rxLock) {
            if (doUntilTrue(rxStream, x -> x.size() >= length, timeout)) {
                return rxStream.pop(length);
            }

            return EMPTY_ARRAY;
        }
    }

    protected byte[] readCharacteristic(UUID serviceUIID, UUID characteristicUUID, int length) {
        if (!setSubscribeToRxChannel()) {
            return EMPTY_ARRAY;
        }

        synchronized (rxLock) {
            BluetoothGattCharacteristic characteristic = bleGatt.getService(serviceUIID).getCharacteristic(characteristicUUID);

            if (!doUntilTrue(bleGatt, x -> x.readCharacteristic(characteristic), txRxTimeout)) {
                return EMPTY_ARRAY;
            }

            if (!doUntilTrue(characteristicStream, x -> x.size() >= length, txRxTimeout)) {
                return EMPTY_ARRAY;
            }

            return characteristicStream.pop(length);
        }
    }


    protected boolean write(byte[] toSend) {
        if (!setSubscribeToRxChannel()) {
            return false;
        }

        synchronized (txLock) {
            BluetoothGattService service = bleGatt.getService(serviceUIID);

            if (null == service) {
                actionsOnEvents.submit(this::doDiscoverServices);
                return false;
            }

            BluetoothGattCharacteristic channel = service.getCharacteristic(txUUID);

            if (null == channel) {
                return false;
            }

            channel.setValue(toSend);
            txReturnValue.reset();

            if (!doUntilTrue(bleGatt, x -> x.writeCharacteristic(channel), txRxTimeout) || txReturnValue.await(txRxTimeout) != 0) {
                return false;
            }
        }

        return true;
    }

}

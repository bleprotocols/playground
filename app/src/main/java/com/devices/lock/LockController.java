package com.devices.lock;

import android.bluetooth.BluetoothDevice;

import com.bluetooth.Controller;
import com.rpc.RpcFunction;
import com.rpc.RpcIntentHandler;

import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.common.Common.hexStringToByteArray;
import static com.common.Common.wrap;
import static com.devices.lock.LockConstants.COMMAND_AUTH_PWD;
import static com.devices.lock.LockConstants.COMMAND_ENERGY;
import static com.devices.lock.LockConstants.COMMAND_KEY_OPERATE_PWD;
import static com.devices.lock.LockConstants.COMMAND_OPERATE_LOCK;
import static com.devices.lock.LockConstants.COMMAND_SETTING_PWD_MODE;
import static com.devices.lock.LockConstants.COMMAND_SET_PASSWORD;
import static com.devices.lock.LockConstants.COMMAND_STATUS;
import static com.devices.lock.LockConstants.OPEN_BLELOCK;


public class LockController extends LockConnection implements Controller {
    boolean locked = false;
    private RpcIntentHandler intentHandler = new RpcIntentHandler<>(LockController.class, this);
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public LockController() {
        super();
    }

    public static byte[] keyStrToBytes(String pwd) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(6);
        for (int i = 0; i <= 5; i++) {
            if (pwd.charAt(i) == '1') {
                byteBuffer.put((byte) 1);
            } else {
                byteBuffer.put((byte) 0);
            }
        }
        return byteBuffer.array();
    }


    @Override
    public void onDisconnect() {
        //this.connect();
    }


    //the parameter seems to do nothing. you get the status, then operate the lock.
    //if it's closed it will open and then close again.
    //blame it on buggy chinese hardware man.
    //only the open parameter seems to work though the protocol defines both open and close
    public boolean operateLock() {
        if (!this.sendPacket(COMMAND_OPERATE_LOCK, new byte[]{OPEN_BLELOCK})) {
            return false;
        }

        return this.recvPacket(COMMAND_OPERATE_LOCK);
    }

    //Retrieves the battery energy of the lock as percentage.
    public boolean getLockEnergy() {
        this.sendPacket(COMMAND_ENERGY, new byte[]{});
        return this.recvPacket(COMMAND_ENERGY);
    }

    public boolean getLockStatus() {
        this.sendPacket(COMMAND_STATUS, new byte[]{});
        return this.recvPacket(COMMAND_STATUS);
    }

    public void setAuthPassword(int password) {
        this.sendPacket(COMMAND_SET_PASSWORD, hexStringToByteArray(String.format("%06x", password)));
    }

    public boolean authPassword(int pwd) {
        if (!this.sendPacket(COMMAND_AUTH_PWD, hexStringToByteArray(String.format("%06x", pwd)))) {
            return false;
        }

        return this.recvPacket(COMMAND_AUTH_PWD);
    }

    public void intoPasswordSettingMode() {
        this.sendPacket(COMMAND_SETTING_PWD_MODE, new byte[]{});
    }

    public void setOperateKey(String pwd) {
        this.sendPacket(COMMAND_KEY_OPERATE_PWD, keyStrToBytes(pwd));
    }

    @Override
    public boolean isDevice(BluetoothDevice result) {
        return result.getName().equals("smart lock");
    }


    @Override
    public String getTypeName() {
        return "SmartLock";
    }

    @Override
    protected synchronized void onConnect() {
        if (!locked) {
            //you have to authenticate with a password before any of the other messages can be sent.
            if (this.authPassword(0)) {
                this.getLockEnergy();
                this.operateLock();
            }
        }
    }


    @Override
    public void startControlling() {
        intentHandler.registerHandler(getContext(), "lock_command");
        scheduler.scheduleWithFixedDelay(wrap(() -> this.connect()), 1000, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stopControlling() {
        intentHandler.unregisterHandler(getContext());
        this.close();
    }

    @RpcFunction
    public synchronized void setLocked(boolean locked) {
        this.locked = locked;
    }

}

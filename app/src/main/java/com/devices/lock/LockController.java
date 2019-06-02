package com.devices.lock;

import android.bluetooth.BluetoothDevice;

import java.nio.ByteBuffer;

import static com.common.Common.hexStringToByteArray;
import static com.devices.lock.LockConstants.COMMAND_AUTH_PWD;
import static com.devices.lock.LockConstants.COMMAND_ENERGY;
import static com.devices.lock.LockConstants.COMMAND_KEY_OPERATE_PWD;
import static com.devices.lock.LockConstants.COMMAND_OPERATE_LOCK;
import static com.devices.lock.LockConstants.COMMAND_SETTING_PWD_MODE;
import static com.devices.lock.LockConstants.COMMAND_SET_PASSWORD;
import static com.devices.lock.LockConstants.COMMAND_STATUS;
import static com.devices.lock.LockConstants.OPEN_BLELOCK;


public class LockController extends LockConnection implements com.bluetooth.BluetoothDevice {
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
    protected void onConnect() {

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
}

package com.devices.et312b;

import com.ble.GattDeviceConnection;
import com.bluetooth.BluetoothConnection;

import static com.common.Common.doUntilTrue;
import static com.common.Common.sleep;
import static com.devices.et312b.Et312BConstants.*;
import static com.devices.et312b.Et312BUUID.RX_UUID;
import static com.devices.et312b.Et312BUUID.SERIAL_PORT_UUID;
import static com.devices.et312b.Et312BUUID.TX_UUID;

public abstract class Et312BPort extends GattDeviceConnection {
    private byte connection_key;
    private static final byte[] NULL_BYTE = new byte[]{0};

    public Et312BPort() {
        this.
                setServiceUUID(SERIAL_PORT_UUID)
                .setRxUUID(RX_UUID)
                .setTxUUID(TX_UUID)
                .setAutoReconnect(false)
                .setTxRxTimeout(2000)
                .setConnectionTimeout(5000);

    }

    /*
    Section: logged commands. Commands that are neccesary to initialize our connection.
    They will log regardless of success or failure - because they aren't called that often.
    */
    abstract void onBoxConnected();

    private synchronized void flushCommandBuffer() {
        byte[] buffer = {0};

        getLogger().println("flushCommandBuffer: Flushing command buffer on box.");

        for (int i = 0; i < 4096; i++) if (this.read(1, SMALL_TIMEOUT).length == 0) break;

        for (int i = 0; i < 11; i++) {
            this.write(NULL_BYTE);

            buffer = this.read(1, SMALL_TIMEOUT);
            if (buffer.length > 0 && Et312BConstants.response_code.ERROR == buffer[0]) {
                getLogger().println("  flushCommandBuffer: buffer flushed after " + i + " bytes.");
                return;
            }
        }
    }

    protected synchronized boolean testConnection() {
        byte bbuffer[] = {0};

        if (readMem(bbuffer, Et312BConstants.FLASH_MEMORY.BOX_MODEL, false, SMALL_TIMEOUT)) {
            return true;
        } else {
            return false;
        }
    }

    private synchronized void setBoxKey() {
        getLogger().println("  setBoxKey: Overwriting box key with our own.");

        if (!writeMem(Et312BConstants.RAM.BOX_KEY, DEFAULT_KEY, false, SMALL_TIMEOUT))
            getLogger().println("  setBoxKey: Failed to write box key.");
        else {
            getLogger().println("  setBoxKey: Encryption set up. Box key set to " + (int) DEFAULT_KEY + ".");
            this.connection_key = DEFAULT_KEY;
        }
    }


    protected synchronized void onConnect() {
        this.resumeWithKey();
    }

    private synchronized void initialKeyExchange() {
        this.flushCommandBuffer();
        this.connection_key = 0;

        byte result[] = {0, 0, 0};
        byte exchange[] = {Et312BConstants.command.EXCHANGE_KEY, 0};
        getLogger().println("  init_initial: Setting up encryption.");
        this.doCommand(exchange, result, 1500);

        if (result[0] != (byte) (Et312BConstants.response_code.KEY_EXCHANGE | 0x20)) {
            getLogger().println("  initialKeyExchange: Unexpected return code from device. Trying previously exchanged key.");
            this.resumeWithKey();
            return;
        }

        if (result[2] != packetChecksum(result, (byte) 2)) {
            getLogger().println("  initialKeyExchange: Checksum ERROR in reply from device. Trying previously exchanged key.");
            this.resumeWithKey();
            return;
        }

        getLogger().println("  initialKeyExchange: recieved box key " + (int) result[1]);
        this.connection_key = (byte) (result[1] ^ (byte) 0x55);
        this.setBoxKey();
        this.onBoxConnected();
    }

    private synchronized void resumeWithKey() {
        byte buffer[] = {0};
        getLogger().println("  resumeWithKey: Resuming previous connection.");
        this.connection_key = DEFAULT_KEY;
        this.flushCommandBuffer();

        if (this.readMem(buffer, Et312BConstants.FLASH_MEMORY.BOX_MODEL, false, SMALL_TIMEOUT)) {
            getLogger().println("  resumeWithKey: Box previously configured by this program, connected.");
            this.onBoxConnected();
            return;
        }

        getLogger().println("  resumeWithKey: Unable to connect - trying to setup key.");
        this.initialKeyExchange();

        if (KEY_GUESSING_ENABLED) {
            getLogger().println("  resumeWithKey: Box configured with different key. Guessing key.");

            for (int i = 0; i < 256; i++) {
                this.flushCommandBuffer();
                this.connection_key = (byte) i;

                if (testConnection()) {
                    getLogger().println("  resumeWithKey: Discovered connection key:" + (int) this.connection_key);
                    this.setBoxKey();
                    this.onBoxConnected();
                    return;
                }
            }
        }
    }

    protected synchronized void boxCommand(byte command) {
        this.writeMem(Et312BConstants.RAM.COMMAND_1, command, true, COMMAND_TIMEOUT);
        sleep(25);//wait for the command to run.
    }


    protected synchronized boolean writeMem(int address, byte value) {
        return this.writeMem(address, value, true, COMMAND_TIMEOUT);
    }

    /*
      Section: unlogged commands. These commands do not log to stdout by default because they will be called frequently.
    */

    protected synchronized boolean writeMem(int address, byte value, boolean disconnect_on_error, int timeout) {
        byte buffer[] = {Et312BConstants.command.WRITE, (byte) (address >> 8), (byte) (address & 0x00ff), value};
        byte result[] = {0};

        this.doCommand(buffer, result, timeout);

        if (result[0] != Et312BConstants.response_code.OK) {
            getLogger().println("  writeMem(" + address + "," + (int) value + "): Serial port Unexpected return code from device: " + (int) result[0] + ".");

            if (disconnect_on_error)
                this.onConnect();

            sleep(40);

            return false;
        }
        sleep(40);

        return true;
    }

    protected synchronized boolean readMem(byte[] dest, int address, boolean disconnect_on_error, int timeout) {
        byte send_buffer[] = {Et312BConstants.command.READ, (byte) (address >> 8), (byte) (address & 0x00ff)};
        byte result[] = new byte[]{0, 0, 0};

        this.doCommand(send_buffer, result, timeout);

        if (result[0] != (Et312BConstants.response_code.READ | 0x20)) {
            getLogger().println("  readMem(" + dest + "," + address + "): Unexpected return code from device: " + (int) result[0] + ".");

            if (disconnect_on_error)
                this.onConnect();
            return false;
        }

        if (result[2] != this.packetChecksum(result, (byte) 2)) {
            getLogger().println("  readMem(" + dest + "," + address + "): Checksum ERROR in response from device.");

            if (disconnect_on_error)
                this.onConnect();

            return false;
        }

        System.arraycopy(result, 1, dest, 0, 1);

        return true;
    }


    private synchronized boolean doCommand(byte buffer[], byte[] output, int timeout) {
        int new_length = buffer.length == 1 ? 1 : buffer.length + 1;
        byte send_buffer[] = new byte[new_length];


        if (buffer.length > 16)
            throw new RuntimeException("doCommand: Maximum command length of 16 bytes exceeded.");

        System.arraycopy(buffer, 0, send_buffer, 0, buffer.length);

        if (new_length > 1) {
            send_buffer[0] |= (buffer.length << 4);
            send_buffer[buffer.length] = packetChecksum(send_buffer, (byte) buffer.length);
        }

        if (this.connection_key != 0) {
            for (int c = 0; c < new_length; c++)
                send_buffer[c] = (byte) (send_buffer[c] ^ this.connection_key);
        }

        if (!this.connect()) return false;

        write(send_buffer);
        byte[] result = read(output.length, timeout);
        System.arraycopy(result, 0, output, 0, result.length);

        return result.length == output.length;
    }

    private static byte packetChecksum(byte buffer[], byte length) {
        if (length < 2)
            throw new RuntimeException("packetChecksum: Buffer must be at least two bytes long.");

        byte sum = 0;

        for (int c = 0; c < length; c++)
            sum += buffer[c];

        return sum;
    }


}

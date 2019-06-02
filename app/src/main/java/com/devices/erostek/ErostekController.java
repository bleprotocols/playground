package com.devices.erostek;

import android.bluetooth.BluetoothDevice;

import static com.common.Common.sleep;
import static com.devices.erostek.ErostekConstants.FADE_DOWN_DELAY;
import static com.devices.erostek.ErostekConstants.FADE_UP_DELAY;
import static com.devices.erostek.ErostekConstants.MAX_LEVEL;
import static com.devices.erostek.ErostekConstants.PERCENTAGE_STEP;
import static com.devices.erostek.ErostekConstants.mode_names;
import static com.devices.erostek.ErostekConstants.power_levels;

public class ErostekController extends ErostekPort implements com.bluetooth.BluetoothDevice {
    private int chAPower = 0;
    private int chBPower = 0;
    private byte chAMode = -1;
    private byte chBMode = -1;
    private byte powerLevel = -1;
    private byte rampLevel = -1;
    private int multiAdjustLevel = -1;
    private boolean buttonsEnabled = true;


    private void boxCommand(byte command) {
        this.writeMem(ErostekConstants.RAM.COMMAND_1, command, true, 500);
        sleep(25);//wait for the command to run.
    }


    public void setPower(int chAPower, int chBPower) {
        if (chAPower > MAX_LEVEL || chBPower > MAX_LEVEL) {
            getLogger().println("   setPower: powerlevel cannot be higher than 100");
            return;
        }

        if (chAPower == this.chAPower && chBPower == this.chBPower)
            return;

        getLogger().println("   setPower(" + (int) chAPower + "," + (int) chBPower + "): setting power levels.");
        this.disableButtons();
        this.fadeParameter(ErostekConstants.ADC_REGISTERS.POT_A, (int) (this.chAPower * PERCENTAGE_STEP), (int) (chAPower * PERCENTAGE_STEP));
        this.fadeParameter(ErostekConstants.ADC_REGISTERS.POT_B, (int) (this.chBPower * PERCENTAGE_STEP), (int) (chBPower * PERCENTAGE_STEP));
        this.chAPower = chAPower;
        this.chBPower = chBPower;
    }
    //fix setpowerlevel

    public void setPowerLevel(String level) {
        byte level_byte = 0;

        for (byte i = 0; i < power_levels.length; i++) {
            if (level.compareTo(power_levels[i]) == 0)
                level_byte = i;
        }

        if (level_byte == this.powerLevel)
            return;

        getLogger().println("   setPowerLevel(\"" + level + "\"): setting power level to: " + (int) level_byte);
        this.writeMem(ErostekConstants.RAM.POWER_LEVEL, level_byte);
        this.powerLevel = level_byte;
    }


    public void setRamp(byte level) {
        if (level > MAX_LEVEL) {
            getLogger().println("   setRamp: level cannot be higher than 100");
            return;
        }

        if (this.rampLevel == level) return;

        getLogger().println("   setRamp(" + (int) level + "): setting ramp level.");
        this.writeMem(ErostekConstants.RAM.RAMP_TIME, (byte) 255);
        this.writeMem(ErostekConstants.RAM.RAMP_LEVEL, (byte) (this.rampLevel - 101));
        this.writeMem(ErostekConstants.RAM.COMMAND_1_PARAMETER, (byte) 255);
        this.writeMem(ErostekConstants.RAM.COMMAND_2_PARAMETER, (byte) 255);
        this.writeMem(ErostekConstants.RAM.COMMAND_1, ErostekConstants.box_command.RAMP_START);
        this.fadeParameter(ErostekConstants.RAM.RAMP_LEVEL, (byte) (this.rampLevel - 101), (byte) (level - 101));
        this.rampLevel = level;
    }

    public void setMultiAdjust(int level) {
        if (level > 100) {
            getLogger().println("   setMultiAdjust: level cannot be higher than 100");
            return;
        }

        if (this.multiAdjustLevel == level)
            return;

        getLogger().println("   setMultiAdjust(" + (int) level + "): setting multi-adjust level.");
        this.disableButtons();
        this.fadeParameter(ErostekConstants.ADC_REGISTERS.MULTI_ADJUST, (int) (this.multiAdjustLevel * PERCENTAGE_STEP), (int) (level * PERCENTAGE_STEP));
        this.multiAdjustLevel = level;
    }

    private void fadeParameter(short parameter, int from, int to) {
        int step = to > from ? 1 : -1;
        int fade = to > from ? FADE_UP_DELAY : FADE_DOWN_DELAY;

        for (int i = from; i != to; i += step) {
            writeMem(parameter, (byte) i);
            sleep(fade);
        }

        this.writeMem(parameter, (byte) to);
    }

    public void setMode(String channel_a_mode, String channel_b_mode) {
        byte byteA = modeToByte(channel_a_mode);
        byte byteB = modeToByte(channel_b_mode);

        if (byteA == this.chAMode && byteB == this.chBMode)
            return;

        getLogger().println("   setMode(\"" + channel_a_mode + "\",\"" + channel_b_mode + "\"): setting mode.");
        this.chAMode = byteA;
        this.chBMode = byteB;

        if (byteA == byteB) {
            this.writeMem(ErostekConstants.RAM.CURRENT_MODE, byteA);
            this.writeMem(ErostekConstants.RAM.COMMAND_1, ErostekConstants.box_command.EXIT_MENU);
            this.writeMem(ErostekConstants.RAM.COMMAND_2, ErostekConstants.box_command.SET_MODE);
        } else {
            this.writeMem(ErostekConstants.RAM.CURRENT_MODE, modeToByte("Split"));
            this.writeMem(ErostekConstants.RAM.SPLIT_MODE_CHANNEL_A, byteA);
            this.writeMem(ErostekConstants.RAM.SPLIT_MODE_CHANNEL_B, byteB);
            this.writeMem(ErostekConstants.RAM.COMMAND_1, ErostekConstants.box_command.EXIT_MENU);
            this.writeMem(ErostekConstants.RAM.COMMAND_2, ErostekConstants.box_command.SET_MODE);
        }

        sleep(50);//wait for the commands to run
    }

    private byte modeToByte(String mode) {
        for (int i = 0; i < mode_names.length; i++) {
            if (mode.compareTo(mode_names[i]) == 0)
                return (byte) (ErostekConstants.MODE_OFFSET + i);
        }

        return ErostekConstants.MODE_OFFSET;
    }

    public void writeMessage(String message) {
        if (message.length() > 16) {
            getLogger().println("   writeMessage: message cannot be longer than 16 characters");
            return;
        }

        getLogger().println("   writeMessage(\"" + message + "\"): writing message.");
        // message.resze(32, ' ');
        this.disableButtons();

        for (int c = 0; c < message.length(); c++) {
            this.writeMem(ErostekConstants.RAM.WRITE_LCD_PARAM, message.getBytes()[c]);
            this.writeMem(ErostekConstants.RAM.WRITE_LCD_POS, (byte) c);
            this.boxCommand(ErostekConstants.box_command.WRITE_LCD);
        }
    }

    public void disableButtons() {
        if (this.buttonsEnabled) {
            this.writeMem(ErostekConstants.CPU_REGISTERS.SYSTEM_FLAGS, (byte) (ErostekConstants.system_flags.ADC_DISABLED | ErostekConstants.system_flags.MULTI_ADJUST_DISABLE));  //disable front potentiometers
            this.writeMem(ErostekConstants.RAM.OUTPUT_MODE, (byte) (ErostekConstants.output_mode.DISABLE_BUTTONS | ErostekConstants.output_mode.PHASE_1)); //disable sub's control over box
            this.buttonsEnabled = false;
        }
    }

    public void enableButtons() {
        if (!this.buttonsEnabled) {
            this.writeMem(ErostekConstants.CPU_REGISTERS.SYSTEM_FLAGS, (byte) 0); //enable front potentiometers
            this.writeMem(ErostekConstants.RAM.OUTPUT_MODE, ErostekConstants.output_mode.PHASE_1); //enable switches
            this.buttonsEnabled = true;
        }
    }

    //Reset all our variables when a reconnect happens.
    @Override
    void onBoxConnected() {
        chAPower = -1;
        chBPower = -1;
        chAMode = -1;
        chBMode = -1;
        powerLevel = -1;
        rampLevel = 100;
        multiAdjustLevel = -1;
        this.buttonsEnabled = true;
    }

    @Override
    public boolean isDevice(BluetoothDevice result) {
        return result.getName().equals("EROSTEK");
    }

}

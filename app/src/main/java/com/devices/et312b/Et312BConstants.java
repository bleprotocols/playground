package com.devices.et312b;

/**
 * Most constants blatantly stolen from https://github.com/buttshock/buttshock-protocol-docs/blob/master/doc/et312-protocol.org
 * Credit to qdot for making them public - and whomever dissasembled the original ErosTek java application for discovering them.
 */
public class Et312BConstants {

    public static final String mode_names[] =
            {"Waves", "Stroke", "Climb", "Combo", "Intense", "Rythm", "Audio1", "Audio2", "Audio3", "Split", "Random1", "Random2", "Toggle", "Orgasm", "Torment", "Phase1", "Phase2", "Phase3", "User1", "User2", "User3", "User4", "User5", "User6", "User7"};

    public static final String power_levels[] = {"Low", "Normal", "High"};

    public static final short FADE_UP_DELAY = 50; //Time between power steps when raising the power level
    public static final short FADE_DOWN_DELAY = 10; //Time between power steps when lowering the power level
    public static final float PERCENTAGE_STEP = 2.55f; //One percent of a byte ( 255 max )
    public static final byte DEFAULT_KEY = 0x1f; //Default box XOR key

    public static final short COMMAND_TIMEOUT = 1000;// General command timeout. Will retry until reached.

    public static final short SMALL_TIMEOUT = 500;// Small timeout to use on connection for operators that might not return
    public static final boolean KEY_GUESSING_ENABLED = false; //Should we bruteforce the box key if we can't connect with our default key
    public static final short MAX_LEVEL = 100; //Maximum level for each of the knobs

    public class FLASH_MEMORY {

        static final short STRING_TABLE = 0x0000; //contains UI messages
        static final short DATA_SEGMENT = 0x0098; //contains constants/firmware
        static final short BOX_MODEL = 0x00fc; //contains hardware version of the box
        static final short MAJOR_FIRMWARE_VERSION = 0x00fd; //contains firmware version of the box
        static final short MINOR_FIRMWARE_VERSION = 0x00fe;
        static final short REVISION_FIRMWARE_VERSION = 0x00ff;
    }

    public class CPU_REGISTERS {
        static final short FIRST_REGISTER = 0x4000;
        static final short LAST_REGISTER = 0x401f;
        static final short SYSTEM_FLAGS = 0x400f;
        static final short SLAVE_MODE_FLAGS = 0x4010;
        static final short RUNTIME_FLAGS = 0x4011;
        static final short BUTTON_DOWN_ACTION = 0x4013;
        static final short BUTTON_UP_ACTION = 0x4014;
        static final short BUTTON_MENU_ACTION = 0x4015;
        static final short BUTTON_OK_ACTION = 0x4016;
    }

    ;

    public class IO_REGISTERS {

        static final short FIRST_REGISTER = 0x4020;
        static final short LAST_REGISTER = 0x405f;
    }

    ;

    public class ADC_REGISTERS {

        static final short OUTPUT_CURRENT = 0x4060;
        static final short MULTI_ADJUST = 0x4061;
        static final short PSU_VOLTAGE = 0x4062;
        static final short BATTERY_VOLTAGE = 0x4063;
        static final short POT_A = 0x4064;
        static final short POT_B = 0x4065;
        static final short AUDIO_A = 0x4066;
        static final short AUDIO_B = 0x4067;
    }


    public class RAM {
        static final short CURRENT_BUTTON = 0x4068;              // Current pushed buttons
        static final short LAST_BUTTON = 0x4069;        // Last pushed buttons
        static final short MASTER_TIMER_MSB = 0x406A;            // Master timer (MSB) (0x4073 LSB) runs 1.91Hz
        static final short OUTPUT_CALIBRATION_A = 0x406B;        // Channel A calibration (DAC power offset)
        static final short OUTPUT_CALIBRATION_B = 0x406C;        // Channel B calibration (DAC power offset)
        static final short MENU_STATE = 0x406D;                 // Menu State
        static final short COMMAND_1 = 0x4070;               // Execute Command (1)
        static final short COMMAND_2 = 0x4071;               // Execute Command (2)
        static final short RNG_OUT = 0x4072;                    // Last random number picked
        static final short MASTER_TIMER_LSB = 0x4073;            // Master timer (LSB) runs at 488Hz (8MHz/64(scaler)/256)
        static final short MENU_ITEM_DISPLAYED = 0x4078;         // Current displayed Menu Item/Mode (not yet selected)
        static final short MENU_ITEM_LOW_BOUNDARY = 0x4079;       // Lowest Selectable Menu Item/Mode
        static final short MENU_ITEM_HIGH_BOUNDARY = 0x407A;      // Highest Selectable Menu Item/Mode
        static final short CURRENT_MODE = 0x407b;               // Current Mode
        static final short OUTPUT_MODE = 0x4083;               // Output Control Flags - COMM_CONTROL_FLAG (0x00)
        static final short MULTI_ADJUST_RANGE_MIN = 0x4086;       // Multi Adjust Range Min (0x0f)
        static final short MULTI_ADJUST_RANGE_MAX = 0x4087;       // Multi Adjust Range Max (0xff)
        static final short MODULE_TIMER_LOW = 0x4088;            // Module timer (3 bytes) low - 244Hz (409uS)
        static final short MODULE_TIMER_MID = 0x4089;            // Module timer (3 bytes) mid - 0.953Hz (1.048S)
        static final short MODULE_TIMER_HIGH = 0x408a;           // Module timer (3 bytes) high - (268.43S)
        static final short MODULE_TIMER_ALTERNATIVE = 0x408b;    // Module timer (slower) - 30.5Hz
        static final short CHANNEL_A_GATE_VALUE = 0x4090;         // Channel A: Current Gate Value (0x06)
        static final short CHANNEL_A_GATE_ON_TIME = 0x4098;        // Channel A: Current Gate OnTime (0x3e)
        static final short CHANNEL_A_GATE_OFF_TIME = 0x4099;       // Channel A: Current Gate OffTime (0x3e)
        static final short CHANNEL_A_GATE_SELECT = 0x409a;        // Channel A: Current Gate Select (0x00)
        static final short CHANNEL_A_GATE_COUNTER = 0x409b;       // Channel A: number of Gate transitions done (0x00)
        static final short CHANNEL_A_RAMP_VALUE = 0x409c;         // Mode Switch Ramp Value Counter (0x9c)
        static final short CHANNEL_A_RAMP_MIN = 0x409d;           // Mode Switch Ramp Value Min (0x9c)
        static final short CHANNEL_A_RAMP_MAX = 0x409e;           // Mode Switch Ramp Value Max (0xff)
        static final short CHANNEL_A_RAMP_RATE = 0x409f;          // Mode Switch Ramp Value Rate (0x07)
        static final short CHANNEL_A_RAMP_STEP = 0x40a0;          // Mode Switch Ramp Value Step (0x01)
        static final short CHANNEL_A_RAMP_AT_MIN = 0x40a1;         // Mode Switch Ramp Action at Min (0xfc)
        static final short CHANNEL_A_RAMP_AT_MAX = 0x40a2;         // Mode Switch Ramp Action at Max (0xfc)
        static final short CHANNEL_A_RAMP_SELECT = 0x40a3;        // Mode Switch Ramp Select (0x01)
        static final short CHANNEL_A_RAMP_TIMER = 0x40a4;         // Mode Switch Ramp Current Timer (0x00)
        static final short CHANNEL_A_INTENSITY = 0x40a5;         // Channel A: Current Intensity Modulation Value (0xff)
        static final short CHANNEL_A_INTENSITY_MIN = 0x40a6;      // Channel A: Current Intensity Modulation Min (0xcd)
        static final short CHANNEL_A_INTENSITY_MAX = 0x40a7;      // Channel A: Current Intensity Modulation Max (0xff)
        static final short CHANNEL_A_INTENSITY_RATE = 0x40a8;     // Channel A: Current Intensity Modulation Rate (0x01)
        static final short CHANNEL_A_INTENSITY_STEP = 0x40a9;     // Channel A: Current Intensity Modulation Step (0x01)
        static final short CHANNEL_A_INTENSITY_AT_MIN = 0x40aa;    // Channel A: Current Intensity Action at Min (0xff)
        static final short CHANNEL_A_INTENSITY_AT_MAX = 0x40ab;    // Channel A: Current Intensity Action at Max (0xff)
        static final short CHANNEL_A_INTENSITY_SELECT = 0x40ac;   // Channel A: Current Intensity Modulation Select (0x00)
        static final short CHANNEL_A_INTENSITY_TIMER = 0x40ad;    // Channel A: Current Intensity Modulation Timer (0x00)
        static final short CHANNEL_A_FREQUENCY = 0x40ae;         // Channel A: Current Frequency Modulation Value (0x16)
        static final short CHANNEL_A_FREQUENCY_MIN = 0x40af;      // Channel A: Current Frequency Modulation Min (0x09)
        static final short CHANNEL_A_FREQUENCY_MAX = 0x40b0;      // Channel A: Current Frequency Modulation Max (0x64)
        static final short CHANNEL_A_FREQUENCY_RATE = 0x40b1;     // Channel A: Current Frequency Modulation Rate (0x01)
        static final short CHANNEL_A_FREQUENCY_STEP = 0x40b2;     // Channel A: Current Frequency Modulation Step (0x01)
        static final short CHANNEL_A_FREQUENCY_AT_MIN = 0x40b3;    // Channel A: Current Frequency Modulation Action Min (0xff)
        static final short CHANNEL_A_FREQUENCY_AT_MAX = 0x40b4;    // Channel A: Current Frequency Modulation Action Max (0xff)
        static final short CHANNEL_A_FREQUENCY_SELECT = 0x40b5;   // Channel A: Current Frequency Modulation Select (0x08)
        static final short CHANNEL_A_FREQUENCY_TIMER = 0x40b6;    // Channel A: Current Frequency Modulation Timer (0x00)
        static final short CHANNEL_A_WIDTH = 0x40b7;             // Channel A: Current Width Modulation Value (0x82)
        static final short CHANNEL_A_WIDTH_MIN = 0x40b8;          // Channel A: Current Width Modulation Min (0x32)
        static final short CHANNEL_A_WIDTH_MAX = 0x40b9;          // Channel A: Current Width Modulation Max (0xc8)
        static final short CHANNEL_A_WIDTH_RATE = 0x40ba;         // Channel A: Current Width Modulation Rate (0x01)
        static final short CHANNEL_A_WIDTH_STEP = 0x40bb;         // Channel A: Current Width Modulation Step (0x01)
        static final short CHANNEL_A_WIDTH_AT_MIN = 0x40bc;        // Channel A: Current Width Modulation Action Min (0xff)
        static final short CHANNEL_A_WIDTH_AT_MAX = 0x40bd;        // Channel A: Current Width Modulation Action Max (0xff)
        static final short CHANNEL_A_WIDTH_SELECT = 0x40be;       // Channel A: Current Width Modulation Select (0x04)
        static final short CHANNEL_A_WIDTH_TIMER = 0x40bf;        // Channel A: Current Width Modulation Timer (0x00)
        static final short SCRATCH_PAD_A = 0x40c0;               // Space for User Module Scratchpad A
        static final short WRITE_LCD_PARAM = 0x4180;         // Write LCD Parameter
        static final short WRITE_LCD_POS = 0x4181;          // Write LCD Position
        static final short COMMAND_1_PARAMETER = 0x4182;      // Parameter r26 for box command
        static final short COMMAND_2_PARAMETER = 0x4183;      // Parameter r27 for box command
        static final short CHANNE_B_GATE_VALUE = 0x4190;          // Channel B: Current Gate Value (0 when no output)
        static final short CHANNEL_B_GATE_ON_TIME = 0x4198;        // Channel B: Current Gate OnTime (0x3e)
        static final short CHANNEL_B_GATE_OFF_TIME = 0x4199;       // Channel B: Current Gate OffTime (0x3e)
        static final short CHANNEL_B_GATE_SELECT = 0x419a;        // Channel B: Current Gate Select (0x00)
        static final short CHANNEL_B_GATE_COUNTER = 0x419b;       // Channel B: number of Gate transitions done (0x00)
        static final short CHANNEL_B_RAMP_VALUE = 0x419c;         // Mode Switch Ramp Value Counter (0x9c)
        static final short CHANNEL_B_RAMP_MIN = 0x419d;           // Mode Switch Ramp Value Min (0x9c)
        static final short CHANNEL_B_RAMP_MAX = 0x419e;           // Mode Switch Ramp Value Max (0xff)
        static final short CHANNEL_B_RAMP_RATE = 0x419f;          // Mode Switch Ramp Value Rate (0x07)
        static final short CHANNEL_B_RAMP_STEP = 0x41a0;          // Mode Switch Ramp Value Step (0x01)
        static final short CHANNEL_B_RAMP_AT_MIN = 0x41a1;         // Mode Switch Ramp Action at Min (0xfc)
        static final short CHANNEL_B_RAMP_AT_MAX = 0x41a2;         // Mode Switch Ramp Action at Max (0xfc)
        static final short CHANNEL_B_RAMP_SELECT = 0x41a3;        // Mode Switch Ramp Select (0x01)
        static final short CHANNEL_B_RAMP_TIMER = 0x41a4;         // Mode Switch Ramp Current Timer (0x00)
        static final short CHANNEL_B_INTENSITY = 0x41a5;         // Channel B: Current Intensity Modulation Value (0xff)
        static final short CHANNEL_B_INTENSITY_MIN = 0x41a6;      // Channel B: Current Intensity Modulation Min (0xcd)
        static final short CHANNEL_B_INTENSITY_MAX = 0x41a7;      // Channel B: Current Intensity Modulation Max (0xff)
        static final short CHANNEL_B_INTENSITY_RATE = 0x41a8;     // Channel B: Current Intensity Modulation Rate (0x01)
        static final short CHANNEL_B_INTENSITY_STEP = 0x41a9;     // Channel B: Current Intensity Modulation Step (0x01)
        static final short CHANNEL_B_INTENSITY_AT_MIN = 0x41aa;    // Channel B: Current Intensity Action at Min (0xff)
        static final short CHANNEL_B_INTENSITY_AT_MAX = 0x41ab;    // Channel B: Current Intensity Action at Max (0xff)
        static final short CHANNEL_B_INTENSITY_SELECT = 0x41ac;   // Channel B: Current Intensity Modulation Select (0x00)
        static final short CHANNEL_B_INTENSITY_TIMER = 0x41ad;    // Channel B: Current Intensity Modulation Timer (0x00)
        static final short CHANNEL_B_FREQUENCY = 0x41ae;         // Channel B: Current Frequency Modulation Value (0x16)
        static final short CHANNEL_B_FREQUENCY_MIN = 0x41af;      // Channel B: Current Frequency Modulation Min (0x09)
        static final short CHANNEL_B_FREQUENCY_MAX = 0x41b0;      // Channel B: Current Frequency Modulation Max (0x64)
        static final short CHANNEL_B_FREQUENCY_RATE = 0x41b1;     // Channel B: Current Frequency Modulation Rate (0x01)
        static final short CHANNEL_B_FREQUENCY_STEP = 0x41b2;     // Channel B: Current Frequency Modulation Step (0x01)
        static final short CHANNEL_B_FREQUENCY_AT_MIN = 0x41b3;    // Channel B: Current Frequency Modulation Action Min (0xff)
        static final short CHANNEL_B_FREQUENCY_AT_MAX = 0x41b4;    // Channel B: Current Frequency Modulation Action Max (0xff)
        static final short CHANNEL_B_FREQUENCY_SELECT = 0x41b5;   // Channel B: Current Frequency Modulation Select (0x08)
        static final short CHANNEL_B_FREQUENCY_TIMER = 0x41b6;    // Channel B: Current Frequency Modulation Timer (0x00)
        static final short CHANNEL_B_WIDTH = 0x41b7;             // Channel B: Current Width Modulation Value (0x82)
        static final short CHANNEL_B_WIDTH_MIN = 0x41b8;          // Channel B: Current Width Modulation Min (0x32)
        static final short CHANNEL_B_WIDTH_MAX = 0x41b9;          // Channel B: Current Width Modulation Max (0xc8)
        static final short CHANNEL_B_WIDTH_RATE = 0x41ba;         // Channel B: Current Width Modulation Rate (0x01)
        static final short CHANNEL_B_WIDTH_STEP = 0x41bb;         // Channel B: Current Width Modulation Step (0x01)
        static final short CHANNEL_B_WIDTH_AT_MIN = 0x41bc;        // Channel B: Current Width Modulation Action Min (0xff)
        static final short CHANNEL_B_WIDTH_AT_MAX = 0x41bd;        // Channel B: Current Width Modulation Action Max (0xff)
        static final short CHANNEL_B_WIDTH_SELECT = 0x41be;       // Channel B: Current Width Modulation Select (0x04)
        static final short CHANNE_BA_WIDTH_TIMER = 0x41bf;        // Channel B: Current Width Modulation Timer (0x00)
        static final short AVERAGE_MA_SAMPLES = 0x41c0;          // last 16 MA knob readings used for averaging
        static final short SCRATCH_PAD_POINTERS = 0x41d0;        // User Module Scratchpad Pointers
        static final short TOP_MODE = 0x41f3;                   // CurrentTopMode (written during routine write) (0x87)
        static final short POWER_LEVEL = 0x41f4;                // PowerLevel - COMM_POWER_LEVEL / COMM_LMODE (0x02)
        static final short SPLIT_MODE_CHANNEL_A = 0x41f5;                // Split Mode Number A (0x77)
        static final short SPLIT_MODE_CHANNEL_B = 0x41f6;                // Split Mode Number B (0x76)
        static final short FAVOURITE_MODE = 0x41f7;             // Favourite Mode (0x76)
        static final short RAMP_LEVEL = 0x41f8;         // Advanced Parameter: RampLevel (0xe1)
        static final short RAMP_TIME = 0x41f9;          // Advanced Parameter: RampTime (0x14)
        static final short ADVANCED_DEPTH = 0x41fa;             // Advanced Parameter: Depth (0xd7)
        static final short ADVANCED_TEMPO = 0x41fb;             // Advanced Parameter: Tempo (0x01)
        static final short ADVANCED_FREQUENCY = 0x41fc;         // Advanced Parameter: Frequency (0x19)
        static final short ADVANCED_EFFECT = 0x41fd;            // Advanced Parameter: Effect (0x05)
        static final short ADVANCED_WIDTH = 0x41fe;             // Advanced Parameter: Width (0x82)
        static final short ADVANCED_PACE = 0x41ff;              // Advanced Parameter: Pace (0x05)
        static final short DEBUG_ENABLE = 0x4207;               // debug mode: displays current module number if not 0
        static final short SENSE_MULTI_ADJUST = 0x420d;          // Current Multi Adjust Value / COMM_MULTI_AVG
        static final short BOX_KEY = 0x4213;                    // com cipher key
        static final short POWER_SUPPLY = 0x4215;               // power status bits
        static final short MODULE_PARSED = 0x4218;              // decoded module instruction to parse
    }

    ///  Range 0x8000 - 0x81ff - Complete MAP of Microcontroller EEPROM (read/write).
    public class EEPROM {
        static final short BASE_EEPROM = (short) 0x8000;                // unused
        static final short IS_PROVISIONED = (short) 0x8001;             // Magic (0x55 means we’re provisioned)
        static final short BOX_SERIAL_LOW = (short) 0x8002;              // Box Serial 1
        static final short BOX_SERIAL_HIGH = (short) 0x8003;             // Box Serial 2
        static final short E_LINK_SIG_1 = (short) 0x8006;                 // E_LINK_SIG_1 - ELINK_SIG1_ADDR (default 0x01)
        static final short E_LINK_SIG_2 = (short) 0x8007;                 // E_LINK_SIG_2 - ELINK_SIG2_ADDR (default 0x01)
        static final short TOP_MODE_NV = (short) 0x8008;                 // TOP_MODE NonVolatile (written during routine write)
        static final short POWER_LEVEL_NV = (short) 0x8009;              // Power Level
        static final short SPLIT_MODE_ANV = (short) 0x800A;              // Split A Mode Num
        static final short SPLIT_MODE_BNV = (short) 0x800B;              // Split B Mode Num
        static final short FAVOURITE_MODE_NV = (short) 0x800C;           // Favourite Mode
        static final short ADVANCED_RAMP_LEVEL_NV = (short) 0x800D;       // Advanced Parameter: RampLevel
        static final short ADVANCED_RAMP_TIME_NV = (short) 0x800E;        // Advanced Parameter: RampTime
        static final short ADVANCED_DEPTH_NV = (short) 0x800F;           // Advanced Parameter: Depth
        static final short ADVANCED_TEMPO_NV = (short) 0x8010;           // Advanced Parameter: Tempo
        static final short ADVANCED_FREQUENCY_NV = (short) 0x8011;       // Advanced Parameter: Frequency
        static final short ADVANCED_EFFECT_NV = (short) 0x8012;          // Advanced Parameter: Effect
        static final short ADVANCED_WIDTH_NV = (short) 0x8013;           // Advanced Parameter: Width
        static final short ADVANCED_PACE_NV = (short) 0x8014;            // Advanced Parameter: Pace
        static final short USER_ROUTINE_VECTOR_1 = (short) 0x8018;        // Start Vector User 1 - COMM_USER_BASE
        static final short USER_ROUTINE_VECTOR_2 = (short) 0x8019;        // Start Vector User 2
        static final short USER_ROUTINE_VECTOR_3 = (short) 0x801A;        // Start Vector User 3
        static final short USER_ROUTINE_VECTOR_4 = (short) 0x801B;        // Start Vector User 4
        static final short USER_ROUTINE_VECTOR_5 = (short) 0x801C;        // Start Vector User 5
        static final short USER_ROUTINE_VECTOR_6 = (short) 0x801D;        // Start Vector User 6
        static final short USER_ROUTINE_VECTOR_7 = (short) 0x801E;        // Start Vector User 7 (not implemented)
        static final short USER_ROUTINE_VECTOR_8 = (short) 0x801F;        // Start Vector User 8 (not implemented)
        static final short USER_ROUTINE_POINTERS_A = (short) 0x8020;      // User routine module pointers 0x80-0x9f
        static final short USER_SPACE_A = (short) 0x8040;                // Space for User Modules
        static final short USER_ROUTINE_POINTERS_B = (short) 0x8100;      // User routine module pointers 0xa0-0xbf
        static final short USER_SPACE_B = (short) 0x8120;                // Space for User Modules
    }

    /// Possible bit field values for SystemFlags.
    public class system_flags {

        static public final byte ADC_DISABLED = 0x01;          // Disable ADC (pots etc) (SYSTEM_FLAG_POTS_DISABLE_MASK)
        static public final byte JUMP = 0x02;                // If set then we jump to a new module number given in $4084
        static public final byte SHAREABLE = 0x04;           // Can this program be shared with a SLAVE unit
        static public final byte MULTI_ADJUST_DISABLE = 0x08;  // Disable Multi Adjust(SYSTEM_FLAG_MULTIA_POT_DISABLE_MASK)
    }


    /// Possible bit field values for SlaveFlags.
    public class slave_mode_flags {
        static public final byte LINKED = 0x04;              // set if we are a linked SLAVE
        static public final byte SELECT = 0x40;              // in SLAVE mode determines which registers to send(toggles)
    }


    /// Possible bit field values for RuntimeFlags.
    public class RuntimeFlags {

        static public final byte APPLY_TO_A = 0x01;            // when module loading to apply module to channel A
        static public final byte APPLY_TO_B = 0x02;           // when module loading to apply module to channel B
        static public final byte TRIGGERED = 0x04;           // used to tell main code that the timer has triggered
        static public final byte ADC_CONV = 0x08;             // set while ADC conversion is running
        static public final byte BOX_RECEIVED = 0x20;         // set if received a full serial command to parse
        static public final byte SERIAL_ERROR = 0x40;         // set if serial comms ERROR
        static public final byte MASTER_MODE = (byte) 0x80;          // set if we are a linked MASTER
    }

    ;

    /// Possible values for MENU_STATE.
    public class MenuState {

        static public final byte ACTIVE = 0x01;              // In startup screen or in a menu
        static public final byte INACTIVE = 0x02;            // No menu; proram is running and displaying"
    }

    ;

    /// Commands to be executed via BoxCommand1 and BoxCommand2.
    public class box_command {
        static public final byte NOP = 0x01;                 // do nothing
        static public final byte STATUS_MENU = 0x02;          // Display Status Screen
        static public final byte SELECT = 0x03;              // Select current Menu Item
        static public final byte EXIT_MENU = 0x04;                // Exit Menu
        static public final byte FAVOURITE_MODE = 0x05;       // Same as 0x00
        static public final byte SET_POWER_MENU = 0x06;        // Set Power Level
        static public final byte ADVANCED_SELECT = 0x07;      // Edit Advanced Parameter
        static public final byte NEXT_ITEM = 0x08;            // display next menu item
        static public final byte PREVIOUS_ITEM = 0x09;        // display previous menu item
        static public final byte MAIN_MENU = 0x0a;            // Show Main Menu
        static public final byte SPLIT_MENU = 0x0b;           // JUMP to split mode settings menu
        static public final byte SPLIT_MODE = 0x0c;           // Activates Split Mode
        static public final byte VALUE_UP = 0x0d;             // Advanced Value Up
        static public final byte VALUE_DOWN = 0x0e;           // Advanced Value Down
        static public final byte ADVANCED_MENU = 0x0f;        // Show Advanced Menu
        static public final byte NEXT_MODE = 0x10;            // Switch to Next mode
        static public final byte PREVIOUS_MODE = 0x11;        // Switch to Previous mode
        static public final byte SET_MODE = 0x12;             // New Mode
        static public final byte WRITE_LCD = 0x13;   // Write Character to LCD
        static public final byte LCD_WRITE_NUMBER = 0x14;      // Write Number to LCD
        static public final byte LCD_WRITE_STRING = 0x15;      // Write String from Stringtable to LCD
        static public final byte LOAD_MODULE = 0x16;          // Load module
        static public final byte MUTE = 0x18;                // Clear module (MUTE)
        static public final byte SWAP_CHANNEL = 0x19;         // Swap Channel A and B
        static public final byte COPY_ATO_B = 0x1a;            // Copy Channel A to Channel B
        static public final byte COPY_BTO_A = 0x1b;            // Copy Channel B to Channel A
        static public final byte LOAD_DEFAULTS = 0x1c;        // Copy defaults from EEPROM
        static public final byte SET_UP_REGISTERS = 0x1d;      // Sets up running module registers
        static public final byte SINGLE_INSTRUCTION = 0x1e;   // Handles single instruction from a module
        static public final byte FUNCTION_CALL = 0x1f;        // General way to call these functions
        static public final byte UPDATE_ADVANCED = 0x20;      // Advanced Setting Update
        static public final byte RAMP_START = 0x21;           // Start Ramp
        static public final byte START_ADC = 0x22;            // Does an ADC conversion
        static public final byte LCD_SET_POSITION = 0x23;      // Set LCD position
        static public final byte NONE = (byte) 0xff;                // No command
    }


    static public final byte MODE_OFFSET = 0x76;         // MODE_NUM_POWER_ON


    /// Possible bit field values for OutputFlags.
    public class output_mode {
        static public final byte PHASE_1 = 0x01;          // Phase Control
        static public final byte MUTE = 0x02;            // MUTE
        static public final byte PHASE_2 = 0x04;          // Phase Control 2
        static public final byte PHASE_3 = 0x08;          // Phase Control 3
        static public final byte DISABLE_BUTTONS = 0x20; // Disable Frontpanel Switches
        static public final byte MONO = 0x40;            // MONO Mode (off=Stereo)
    }

    ;

    /// Possible bit field values for all "Gate Select" settings.
    public class Gate {
        static public final byte OFF = 0x00;             // No gating
        static public final byte TIMER_FAST = 0x01;       // Use the $4088 (244Hz) timer for gating
        static public final byte TIMER_MEDIUM = 0x02;     // Use the $4088 div 8 (30.5Hz) timer for gating
        static public final byte TIMER_SLOW = 0x03;       // Use the $4089 (.953Hz) timer for gating
        static public final byte OFF_FROM_TEMPO = 0x04;    // Off time is taken from the advanced parameter tempo default
        static public final byte OFF_FROM_MA = 0x08;       // Off time follows the value of the MA knob
        static public final byte ON_FROM_EFFECT = 0x20;    // On time is taken from the advanced parameter effect default
        static public final byte ON_FROM_MA = 0x40;        // On time follows the value of the MA knob
    }


    /// Possible bit field values for Frequency/Width/Intensity Ramp "AtMin" and "AtMax" settings.
    public class Ramp {
        static public final byte STOP = (byte) 0xfc;            // Stop when ramp min/max is reached
        static public final byte LOOP = (byte) 0xfd;            // Loop back round (if below min; set to max; if above max set to min
        static public final byte TOGGLE_GATE = (byte) 0xfc;      // Reverse direction; toggle gate and continue"
        static public final byte REVERSE = (byte) 0xff;         // Reverse Direction ramp min/max is reached
    }

    ;

    /// Possible bit field values for Frequency/Width/Intensity Select settings.
    public class Select {
        static public final byte STATIC = 0x00;          // Set the value to an absolute value determined by the other bits
        static public final byte TIMER_FAST = 0x01;       // Update the value based on timer at $4088 (244Hz)
        static public final byte TIMER_MEDIUM = 0x02;     // Update the value based on timer at $4088 divided by 8 (30.5Hz)
        static public final byte TIMER_SLOW = 0x03;       // Update the value based on timer at $4089 (.953Hz
        static public final byte ADVANCED = 0x04;        // Set the value to advanced_parameter default for this variable
        static public final byte MA = 0x08;              // Set the value to the current MA knob value
        static public final byte OTHER = 0x0c;           // Copy from the other channels value
        static public final byte ADVANCED_INVERTED = 0x14; // Set the value to the inverse of the advanced_parameter default
        static public final byte MA_INVERTED = 0x18;      // Set the value to the inverse of the current MA knob value
        static public final byte OTHER_INVERTED = 0x1c;   // Inverse of the other channels value
        static public final byte RATE_ADVANCED = 0x20;    // Rate is from advanced_parameter default
        static public final byte RATE_MA = 0x40;          // Rate is from MA value
        static public final byte RATE_OTHER = 0x60;       // Rate is rate from other channel
        static public final byte RATE_ABS_INVERTED = (byte) 0x80; // Rate is inverse of parameter (example $40ba)
        static public final byte RATE_ADVANCED_INVERTED = (byte) 0xa0; // Rate is inverse of advanced_parameter default
        static public final byte RATE_MA_INVERTED = (byte) 0xc0;  // Rate is inverse of MA value
        static public final byte RATE_OTHER_INVERTED = (byte) 0xf0; // Rate is inverse of rate from other channel
    }


    /// Possible bit field values for POWER_SUPPLY.
    public class PowerSupply {

        static public final byte BATTERY = 0x01;         // Set if we have a battery
        static public final byte PSU = 0x02;             // Set if we have a PSU connected
    }

    ;

    /// Serial Port Commands (Client -> Box).
    public class command {

        static public final byte RESET = 0x08;           // reset everything to default
        static public final byte READ = 0x0c;            // read from memory
        static public final byte WRITE = 0x0d;           // write to  memory
        static public final byte MASTER = 0x0e;          // set box as our SLAVE
        static public final byte EXCHANGE_KEY = 0x0f;
    }

    /// Serial Port Responses (Box -> Client).
    public class response_code {

        static public final byte KEY_EXCHANGE = 0x01;     // key exchanged
        static public final byte READ = 0x02;            // read result
        static public final byte SLAVE = 0x05;           // box set as alve
        static public final byte OK = 0x06;              // serial command executed
        static public final byte ERROR = 0x07;           // serial command error
    }

}


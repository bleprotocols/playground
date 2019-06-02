package com.devices.visionbody;

import java.util.Arrays;
import java.util.List;

public class VisionBodyConstants {
    public static final long MESSAGE_RETRIES = 10;
    public static final byte MAX_RESPONSE_LENGTH = 64; // Maximum length of a response packet. For verification.
    public static final int GET_INTENSITY_INTERVAL = 30000; //Interval at which we poll the box for the intensity vectors
    public static final int POLL_INTERVAL = 2000; //Interval at which we poll the website for settings
    public static final int INTENSITY_STEP_INTERVAL = 100; //Interval at which we increase the intensity
    public static final int INTENSITY_STEP_COUNT_MAX = 10; //Maximum number of intensity steps per poll interval.
    // this ensures interactivity. Because you don't always want to wait until the previous command is done to be able to change the intensity.

    public static final int MAX_MESSAGE_LENGTH = 130;
    public static final int TRANSFER_IMAGE_CHUNK_MESSAGE_LENGTH = 1026;
    public static final int MESSAGE_HEADER_LENGTH = 4;


    public static List<String> programs = Arrays.asList("GettingStarted", "Basic1", "Basic2", "Continuous1", "Continuous2", "Endurance1", "Endurance2", "Endurance3", "Strength1", "Strength2", "FatBurning1", "FatBurning2", "CoolDown1", "CoolDown2", "Massage", "AntiCell");
    public static List<String> feels = Arrays.asList("Soft", "Medium", "Hard");
    public static List<String> bodyParts = Arrays.asList("Upper back", "Rear", "Legs", "Lower back", "Arms", "Neck", "Chest", "Stomach");

}

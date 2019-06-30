package com.devices.visionbody;

import android.content.Context;

import com.rpc.RpcFunction;
import com.rpc.RpcFunctionCaller;

public class VisionBodyInterface {
    public static void loadProgram(Context context, String program, String feel, byte stimDuration, byte restDuration) {
        RpcFunctionCaller.callMethod(context, "visionbody_control", "loadProgram", program, feel, stimDuration, restDuration);
    }

    public static void setIntensity(Context context, byte upperBackIntensity, byte rearIntensity, byte legsIntesnsity, byte lowerBackIntensity, byte armsIntensity, byte neckIntensity, byte chestIntensity, byte stomachIntensity) {
        RpcFunctionCaller.callMethod(context, "visionbody_control", "setIntensity", upperBackIntensity, rearIntensity, legsIntesnsity, lowerBackIntensity, armsIntensity, neckIntensity, chestIntensity, stomachIntensity);
    }
}
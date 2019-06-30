package com.devices.et312b;

import android.content.Context;

import com.rpc.RpcFunctionCaller;

public class Et312BInterface {
    public static void setPower(Context context, int powerA, int powerB) {
        RpcFunctionCaller.callMethod(context, "erostek_control", "setPower", powerA, powerB);
    }

    public static void setPowerLevel(Context context, String level) {
        RpcFunctionCaller.callMethod(context, "erostek_control", "setPowerLevel", level);
    }

    public static void setMode(Context context, String modeA, String modeB) {
        RpcFunctionCaller.callMethod(context, "erostek_control", "setMode", modeA, modeB);
    }

    public static void setRamp(Context context, int level) {
        RpcFunctionCaller.callMethod(context, "erostek_control", "setRamp", level);
    }

    public static void setMultiAdjust(Context context, int level) {
        RpcFunctionCaller.callMethod(context, "erostek_control", "setMultiAdjust", level);
    }
}

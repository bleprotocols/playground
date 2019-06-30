package com.devices.lovesense;

import android.content.Context;

import com.rpc.RpcFunctionCaller;

public class LovesenseInterface {
    public static void setIntensity(Context context, int intensity) {
        RpcFunctionCaller.callMethod(context, "lovesense_control", "setIntensity", intensity);
    }
}

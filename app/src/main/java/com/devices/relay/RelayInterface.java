package com.devices.relay;

import android.content.Context;

import com.rpc.RpcFunctionCaller;

public class RelayInterface {
    public static void setRelay(Context context, int relay, boolean on) {
        RpcFunctionCaller.callMethod(context, "relay_control", "setRelay", relay, on);
    }

    public static void testConnection(Context context) {
        RpcFunctionCaller.callMethod(context, "relay_control", "testConnection");
    }

}

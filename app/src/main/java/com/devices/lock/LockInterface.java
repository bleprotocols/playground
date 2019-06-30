package com.devices.lock;

import android.content.Context;

import com.rpc.RpcFunction;
import com.rpc.RpcFunctionCaller;

public class LockInterface {
    @RpcFunction
    public static void setLocked(Context context, boolean locked) {
        RpcFunctionCaller.callMethod(context, "lock_command", "setLocked", locked);
    }

}

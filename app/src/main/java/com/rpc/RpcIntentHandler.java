package com.rpc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.common.Common.wrap;

public class RpcIntentHandler<T> extends BroadcastReceiver {
    T classInstance;
    Class<T> classForReflection;
    HashMap<String, RpcMethod> callableMethods = new HashMap<>();
    ExecutorService methodExecutor = Executors.newCachedThreadPool();

    public RpcIntentHandler(Class<T> classForReflection, T classInstance) {
        this.classForReflection = classForReflection;
        this.classInstance = classInstance;

        for (Method m : classForReflection.getDeclaredMethods()) {
            if (m.isAnnotationPresent(RpcFunction.class)) {
                callableMethods.put(m.getName(), new RpcMethod(m));
            }
        }
    }


    public synchronized void registerHandler(Context context, String intentName) {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        IntentFilter filter = new IntentFilter();
        filter.addAction(intentName);
        manager.registerReceiver(this, filter);
    }

    public synchronized void unregisterHandler(Context context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
    }

    public void onReceive(Context context, Intent intent) {
        String methodName = intent.getStringExtra("method");
        ArrayList<String> params = intent.getStringArrayListExtra("params");

        if (methodName == null) {
            return;
        }
        methodExecutor.submit(wrap(

                () -> {
                    RpcMethod method = callableMethods.get(methodName);
                    method.Call(classInstance, params);
                }));
    }
}

package com.rpc;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RpcFunctionCaller {

    public static void callMethod(Context context, String intentName, String method, Serializable... params) {
        ArrayList<String> intentParams = new ArrayList<>();

        for (int i = 0; i < params.length; i++) {
            intentParams.add(Reflection.objectToString(params[i]));
        }

        Intent messageIntent = new Intent(intentName);
        messageIntent.putExtra("method", method);
        messageIntent.putStringArrayListExtra("params", intentParams);

        LocalBroadcastManager.getInstance(context).sendBroadcast(messageIntent);

    }
}

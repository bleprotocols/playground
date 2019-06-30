package com.devices.textToSpeech;

import android.content.Context;

import com.rpc.RpcFunctionCaller;

public class TextToSpeechInterface {
    public static void speak(Context context, String message) {
        RpcFunctionCaller.callMethod(context, "textToSpeech", "speak", message);
    }
}

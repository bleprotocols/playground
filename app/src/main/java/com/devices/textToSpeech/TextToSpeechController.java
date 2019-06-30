package com.devices.textToSpeech;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import com.bluetooth.Controller;
import com.common.Common;
import com.devices.visionbody.VisionBodyController;
import com.rpc.RpcFunction;
import com.rpc.RpcIntentHandler;

public class TextToSpeechController implements Controller {
    private TextToSpeech textToSpeech;
    private Context context;
    private boolean ttsInitialized;
    private RpcIntentHandler intentHandler = new RpcIntentHandler<>(TextToSpeechController.class, this);

    @Override
    public synchronized void startControlling() {
        ttsInitialized = false;
        textToSpeech = new TextToSpeech(context, status ->
        {
            if (status == TextToSpeech.SUCCESS) {
                ttsInitialized = true;
            }
        });
        intentHandler.registerHandler(context, "textToSpeech");
    }

    @Override
    public synchronized void stopControlling() {
        intentHandler.unregisterHandler(context);
        textToSpeech.shutdown();
        textToSpeech = null;
    }

    @Override
    public String getTypeName() {
        return null;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @RpcFunction
    public synchronized void speak(String string) {
        if (ttsInitialized) {
            textToSpeech.speak(string, TextToSpeech.QUEUE_ADD, null, Common.generateString(8));
        }
    }
}

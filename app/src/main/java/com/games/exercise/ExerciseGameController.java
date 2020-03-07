package com.games.exercise;

import android.content.Context;

import com.bluetooth.Controller;

import com.devices.et302r.ET302RInterface;
import com.devices.lock.LockInterface;
import com.devices.textToSpeech.TextToSpeechInterface;
import com.rpc.RpcFunction;
import com.rpc.RpcIntentHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.common.Common.wrap;
import static com.devices.et302r.ET302RInterface.*;
import static java.lang.Math.max;
import static java.lang.Math.random;
import static java.lang.Math.sqrt;

public class ExerciseGameController implements Controller {
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private RpcIntentHandler intentHandler = new RpcIntentHandler<>(ExerciseGameController.class, this);

    private Context context;

    long lastShocked = 0;
    long targetHeartrate = 140;
    boolean gameHasStarted = false;
    static final long shock_delay = 20000;


    @Override
    public void startControlling() {
        scheduler = Executors.newSingleThreadScheduledExecutor();

        LockInterface.setLocked(context, false);

        scheduler.schedule(wrap(() -> {
            TextToSpeechInterface.speak(context, "Welcome to the exercise game. The game will begin as soon as you hit your target heartrate. Try not to get below it after that.");
            ET302RInterface.sendButton(context, BUTTON_MODE_UP, 1);
            ET302RInterface.sendButton(context, BUTTON_MODE_UP, 1);
        }), 1, TimeUnit.SECONDS);

        scheduler.scheduleAtFixedRate(wrap(() -> {
            ET302RInterface.sendButton(context, BUTTON_A_UP, 100);
        }), 0, (long) 10, TimeUnit.SECONDS);

        scheduler.scheduleAtFixedRate(wrap(() -> {
            ET302RInterface.sendButton(context, BUTTON_B_DOWN, 1000);
        }), 0, (long) 20, TimeUnit.SECONDS);


        intentHandler.registerHandler(context, "heartrate");
    }

    @RpcFunction
    public void onHeartrate(int heartrate) {
        if (!gameHasStarted && heartrate > targetHeartrate) {
            gameHasStarted = true;
            TextToSpeechInterface.speak(context, "Target heartrate reached.");
        }

        long currentTime = System.currentTimeMillis();
        if (gameHasStarted && heartrate < (targetHeartrate * 0.9) && (currentTime - lastShocked) > shock_delay) {
            lastShocked = currentTime;
            TextToSpeechInterface.speak(context, "Work harder.");
            ET302RInterface.sendButton(context, BUTTON_B_UP, 4000);
       //     ET302RInterface.sendButton(context, BUTTON_A_DOWN, 1400);
        }
    }


    @Override
    public void stopControlling() {
        intentHandler.unregisterHandler(context);
        this.scheduler.shutdown();
    }

    @Override
    public String getTypeName() {
        return "Exercise game";
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }
}

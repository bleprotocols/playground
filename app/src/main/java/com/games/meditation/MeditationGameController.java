package com.games.meditation;

import android.content.Context;

import com.bluetooth.Controller;

import com.devices.et302r.ET302RInterface;
import com.devices.et312b.Et312BInterface;
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

public class MeditationGameController implements Controller {
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private RpcIntentHandler intentHandler = new RpcIntentHandler<>(MeditationGameController.class, this);

    private Context context;

    boolean recordingMax = false;
    double maxMovement = 0;
    double timeFactor = 1;

    long lastShocked = 0;

    boolean isShockEnabled = false;
    static final long shock_delay = 5000;

    int punishmentLevel = 0;
    int rewardLevel = 15;


    void doReward() {
        rewardLevel++;
        if (rewardLevel > 99) rewardLevel = 99;

        punishmentLevel -= 4;
        if (punishmentLevel < 0) punishmentLevel = 0;
        refreshValues();

        ET302RInterface.sendButton(context, BUTTON_A_UP, 100);
        ET302RInterface.sendButton(context, BUTTON_B_DOWN, 1000);
    }

    void doPunishment() {
        rewardLevel -= 15;
        if (rewardLevel < 0) rewardLevel = 0;

        punishmentLevel += 20;
        if (punishmentLevel > 99) punishmentLevel = 99;
        refreshValues();

        ET302RInterface.sendButton(context, BUTTON_A_DOWN, 1400);
        ET302RInterface.sendButton(context, BUTTON_B_UP, 4000);
    }

    void refreshValues() {
        Et312BInterface.setPower(context, rewardLevel, punishmentLevel);
    }

    @Override
    public void startControlling() {
        scheduler = Executors.newSingleThreadScheduledExecutor();

        LockInterface.setLocked(context, false);

        scheduler.schedule(wrap(() -> {
            TextToSpeechInterface.speak(context, "Welcome to the meditation game. Please assume a comfortable position. In 2 minutes any locks you enabled will be locked and the game will begin.");
            ET302RInterface.sendButton(context, BUTTON_MODE_UP, 1);
            ET302RInterface.sendButton(context, BUTTON_MODE_UP, 1);

            Et312BInterface.setMode(context,"Waves","Waves");
            Et312BInterface.setMultiAdjust(context,100);
            Et312BInterface.setPowerLevel(context,"High");

        }), 1, TimeUnit.SECONDS);

        scheduler.schedule(wrap(() -> {
            LockInterface.setLocked(context, true);

            TextToSpeechInterface.speak(context, "Your restraints are now locked. The game is calibrating it's sensors. Please sit still.");
            recordingMax = true;
        }), (long) (130 * timeFactor), TimeUnit.SECONDS);


        scheduler.schedule(wrap(() -> {
            TextToSpeechInterface.speak(context, "Your movement level has been recorded. Please sit still and meditate.");
            isShockEnabled = true;
            recordingMax = false;
            scheduler.scheduleWithFixedDelay(wrap(() -> {
                doReward();
            }), 0, (long) (18 * timeFactor), TimeUnit.SECONDS);
        }), (long) (170 * timeFactor), TimeUnit.SECONDS);


        scheduler.schedule(wrap(() -> {
            LockInterface.setLocked(context, false);
            TextToSpeechInterface.speak(context, "You are free now. Click the button on a lock to unlock yourself.");
            recordingMax = false;
            this.scheduler.shutdown();
            isShockEnabled = false;
            ET302RInterface.sendButton(context, BUTTON_B_DOWN, 15000);
            ET302RInterface.sendButton(context, BUTTON_A_DOWN, 15000);
            rewardLevel = 0;
            punishmentLevel = 0;
            refreshValues();
        }), (long) (timeFactor * (170 + (random() * 60 * 15) + (60 * 15))), TimeUnit.SECONDS);


        intentHandler.registerHandler(context, "gyroscope");
    }

    @RpcFunction
    public void onGyroscope(float valueX, float valueY, float valueZ) {
        double movementVectorLength = sqrt(valueX * valueX + valueY * valueY + valueZ * valueZ);
        if (recordingMax) {
            maxMovement = max(maxMovement, movementVectorLength);
        } else {
            if (isShockEnabled && (movementVectorLength > (maxMovement * 1.6)) && (System.currentTimeMillis() > (lastShocked + shock_delay))) {
                TextToSpeechInterface.speak(context, "Sit still.");
                doPunishment();

                lastShocked = System.currentTimeMillis();
            }
        }
    }


    @Override
    public void stopControlling() {
        intentHandler.unregisterHandler(context);
        this.scheduler.shutdown();
    }

    @Override
    public String getTypeName() {
        return "Meditation game";
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }
}

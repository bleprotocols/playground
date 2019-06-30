package com.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.gui.MeditationActivity;

import java.security.SecureRandom;
import java.util.TimerTask;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class Common {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static TimerTask wrap(Runnable r) {
        return new TimerTask() {

            @Override
            public void run() {
                r.run();
            }
        };
    }

    public static byte[] hexStringToByteArray(String s) {
        byte data[] = new byte[s.length() / 2];
        for (int i = 0; i < s.length(); i += 2) {
            data[i / 2] = (Integer.decode("0x" + s.charAt(i) + s.charAt(i + 1))).byteValue();
        }
        return data;
    }


    public static synchronized String generateString(int length) {
        StringBuilder builder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            builder.append(ALPHABET.charAt(secureRandom.nextInt(ALPHABET.length())));
        }

        return builder.toString();
    }

    public static void sleep(long milis) {
        if(milis<=0){
            return;
        }

        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            throw new RuntimeException("Common::sleep(): Sleep was interrupted.");
        }

    }

    public static String printByteArray(byte[] array) {
        if (array == null) {
            return "null";
        }

        String ret = "[";

        for (int i = 0; i < array.length; i++) {
            ret += (int) array[i];
            if (i != (array.length - 1)) {
                ret += ",";
            }
        }

        ret += "]";
        return ret;
    }


    public static <T> boolean doUntilTrue(T obj, Predicate<T> function, long timeout) {
        for (long i = System.currentTimeMillis(); (System.currentTimeMillis() - i) < timeout; ) {
            if (function.test(obj)) {
                return true;
            }
            sleep(5);
        }

        return false;
    }

    public static IntStream intStream(byte[] array) {
        return IntStream.range(0, array.length).map(idx -> array[idx]);
    }

    public static void navigateToActivity(Activity context, Class activity) {
        Intent intent = new Intent(context, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        context.finish();
    }


}

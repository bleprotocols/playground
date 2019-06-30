package com.rpc;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Reflection {
    public static <T extends Serializable> T stringToType(String string, T defaultValue) {
        return (T) stringToObject(string, defaultValue);
    }


    public static Object stringToObject(String string, Object defaultValue) {
        if (string == null || string.length() == 0) {
            return defaultValue;
        }

        byte[] bytes = Base64.decode(string, 0);
        Object object = defaultValue;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
            object = objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return object;
    }

    public static String objectToString(Serializable object) {
        String encoded = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
            encoded = Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encoded;
    }

}

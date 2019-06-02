package com.common;

public class ThreadsafeBoolean {
    private final Object lock = new Object();
    private boolean returnValue = false;


    public boolean get() {
        synchronized (lock) {
            return returnValue;
        }
    }

    public void set(boolean value) {
        synchronized (lock) {
            returnValue = value;
        }
    }
}

package com.common;

import java.util.Arrays;

import static java.lang.Math.min;

//Simple constant-size queue of bytes useful for serial protocols.
//It's thread-safe.
public class ByteBuffer {
    private byte[] array;
    private int current_size;

    public ByteBuffer(int max_size) {
        array = new byte[max_size];
        current_size = 0;
    }


    public synchronized int size() {
        return current_size;
    }

    public synchronized void reset() {
        current_size = 0;
    }


    public synchronized void push(byte b) {
        if (current_size >= array.length) {
            System.err.println("ByteBuffer(" + array.length + ")::push(): length exceeded.");
            return;
        }

        array[current_size] = b;
        current_size++;
    }

    public synchronized void push(byte[] b) {
        if (null == b) {
            return;
        }

        for (byte current : b) this.push(current);
    }

    public synchronized byte[] pop(int length) {
        length = min(length, size());

        byte[] ret = Arrays.copyOf(array, length);

        for (int i = 0; i + length < array.length; i++) {
            array[i] = array[i + length];
        }

        current_size -= length;

        return ret;

    }

}

package com.gui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.OutputStream;

//stream to write data to broadcast messages
public class BroadcastOutputStream extends OutputStream {
    private Context context;
    private String broadcastName;

    public BroadcastOutputStream(Context context, String broadcastName) {
        this.context = context;
        this.broadcastName = broadcastName;
    }

    @Override
    public void write(int b) throws IOException {
        write(new byte[]{(byte) b}, 0, 1);
    }

    @Override
    public void write(byte[] buffer, int offset, int length) throws IOException {
        final String text = new String(buffer, offset, length);

        Intent intent = new Intent(broadcastName);
        intent.putExtra("extra", text);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
package com.bluetooth;

import android.content.Context;

public interface Controller {
    void startControlling();
    void stopControlling();
    String getTypeName();
    void setContext(Context context);
}

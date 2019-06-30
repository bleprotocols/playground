package com.devices.et302r;

import android.content.Context;

import com.rpc.RpcFunction;
import com.rpc.RpcFunctionCaller;

public class ET302RInterface {
    @RpcFunction
    public static  void sendButton(Context context,int button, int duration){
        RpcFunctionCaller.callMethod(context,"etremote_control","sendButton",button,duration);
    }

    public static int BUTTON_MODE_UP = 1;
    public static int BUTTON_MODE_DOWN = 2;
    public static int BUTTON_A_UP = 3;
    public static int BUTTON_A_DOWN = 4;
    public static int BUTTON_B_UP = 5;
    public static int BUTTON_B_DOWN = 6;
    public static int BUTTON_TRAINING_1 = 7;
    public static int BUTTON_TRAINING_2 = 8;
    public static int BUTTON_TRAINING_3 = 9;
}

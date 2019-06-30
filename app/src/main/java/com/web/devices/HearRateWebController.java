package com.web.devices;

import com.rpc.RpcFunction;
import com.rpc.RpcIntentHandler;
import com.session.HTTPController;

public class HearRateWebController extends HTTPController {
    int heartrate = 0;
    private RpcIntentHandler intentHandler = new RpcIntentHandler<>(HearRateWebController.class, this);

    @Override
    public String getTypeName() {
        return "HeartrateSensor";
    }

    @Override
    public String getControlURL() {
        return "get_session.php?session=" + getSessionKey();
    }

    @Override
    public boolean acceptCommand(String command) {
        return true;
    }

    public String getSessionURL() {
        return getBaseURL() + "/heartrate.php?session=" + getSessionKey() + "&heartrate=" + heartrate;
    }


    @Override
    public int getInterval() {
        return 1000;
    }

    @RpcFunction
    public void onHeartrate(int heartrate) {
        this.heartrate = heartrate;
    }

    @Override
    public void startControlling() {
        super.startControlling();
        intentHandler.registerHandler(getContext(), "heartrate");

    }

    @Override
    public void stopControlling() {
        super.stopControlling();
        intentHandler.unregisterHandler(getContext());
    }

}

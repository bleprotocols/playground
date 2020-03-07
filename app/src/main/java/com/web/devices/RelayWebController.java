package com.web.devices;

import com.devices.relay.RelayInterface;
import com.rpc.RpcFunction;
import com.rpc.RpcIntentHandler;
import com.session.HTTPController;

import static com.common.Common.doUntil;
import static com.common.Common.sleep;


public class RelayWebController extends HTTPController {
    long previousButton = 0;
    boolean isConnected = false;
    private RpcIntentHandler intentHandler = null;

    @Override
    public String getTypeName() {
        return "BluetoothRelay";
    }

    @Override
    public String getControlURL() {
        return "relay.php?session=" + getSessionKey();
    }

    @RpcFunction
    public void onConnected() {
        isConnected = true;
    }

    public synchronized void waitConnected() {
        isConnected = false;
        while (!isConnected) {
            RelayInterface.testConnection(getContext());
            sleep(500);
            if(isConnected)break;;
            sleep(5000);
        }
    }

    @Override
    public boolean acceptCommand(String command) {
        String split[] = command.split(",");

        if (split.length != 3) {
            return false;
        }

        if (intentHandler == null) {
            intentHandler = new RpcIntentHandler<>(RelayWebController.class, this);
            intentHandler.registerHandler(getContext(), "relaybox");
        }

        long nextButton = Long.decode(split[2]);

        if (nextButton != previousButton) {
            int button = Integer.parseInt(split[0]);
            int duration = Integer.parseInt(split[1]);

            if (button == 2) {
                duration /= 5;
            }

            if (duration > 5000) {
                waitConnected();
                doUntil(() -> {
                    RelayInterface.setRelay(getContext(), button - 1, true);
                    sleep(5000);
                }, duration);
            } else {
                if(duration ==1){
                    duration=5000;
                }
                waitConnected();
                RelayInterface.setRelay(getContext(), button - 1, true);
                sleep(duration);
            }

            RelayInterface.setRelay(getContext(), button - 1, false);

            previousButton = nextButton;
        }

        return true;
    }


    @Override
    public int getInterval() {
        return 1000;
    }
}

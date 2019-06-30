package com.web.devices;

import com.devices.et302r.ET302RInterface;
import com.session.HTTPController;


public class Et302rWebController extends HTTPController {
    long previousButton = 0;

    @Override
    public String getTypeName() {
        return "ET302R";
    }

    @Override
    public String getControlURL() {
        return "et302r.php?session=" + getSessionKey();
    }


    @Override
    public boolean acceptCommand(String command) {
        String split[] = command.split(",");

        if (split.length != 3) {
            return false;
        }

        long nextButton = Long.decode(split[2]);

        if (nextButton != previousButton) {
            int button = Integer.parseInt(split[0]);
            int duration = Integer.parseInt(split[1]);

            ET302RInterface.sendButton(getContext(), button, duration);
            previousButton = nextButton;
        }

        return true;
    }


    @Override
    public int getInterval() {
        return 1000;
    }
}

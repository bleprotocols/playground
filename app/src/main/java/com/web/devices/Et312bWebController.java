package com.web.devices;

import com.devices.et312b.Et312BInterface;
import com.session.HTTPController;

import static com.common.Common.sleep;

public class Et312bWebController extends HTTPController {
    @Override
    public String getTypeName() {
        return "ET312B";
    }

    @Override
    public String getControlURL( ) {
        return "et312b.php?session=" + getSessionKey();
    }


    @Override
    public boolean acceptCommand(String command) {

        String split[] = command.split(",");

        if (split.length != 6) {
            return false;
        }

        Et312BInterface.setPowerLevel(getContext(), split[0]);
        Et312BInterface.setMode(getContext(), split[1], split[2]);
        Et312BInterface.setPower(getContext(), Integer.decode(split[3]), Integer.decode(split[4]));
        Et312BInterface.setMultiAdjust(getContext(), Integer.decode(split[5]));

        return true;
    }
}

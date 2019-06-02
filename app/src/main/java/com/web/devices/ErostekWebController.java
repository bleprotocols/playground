package com.web.devices;

import com.devices.erostek.ErostekController;
import com.web.WebController;

import static com.common.Common.sleep;

public class ErostekWebController extends ErostekController implements WebController {

    @Override
    public String getTypeName() {
        return "Erostek";
    }

    @Override
    public String getControlURL(String sessionKey) {
        return  "erostek.php?session=" + sessionKey;
    }

    @Override
    public boolean acceptCommand(String command) {

        String split[] = command.split(",");

        if (split.length != 6) {
            return false;
        }

        this.setPowerLevel(split[0]);
        this.setMode(split[1], split[2]);
        this.setPower(Integer.decode(split[3]), Integer.decode(split[4]));
        this.setMultiAdjust(Integer.decode(split[5]));
        sleep(2000);

        return true;
    }
}

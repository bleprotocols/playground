package com.web.devices;

import com.devices.lovesense.LovesenseInterface;
import com.session.HTTPController;

import java.util.Random;

public class LovesenseWebController extends HTTPController {
    private Random random = new Random();

    @Override
    public String getTypeName() {
        return "LoveSense";
    }

    @Override
    public String getControlURL() {
        return "lovesense.php?session=" + getSessionKey();
    }

    @Override
    public boolean acceptCommand(String command) {
        String split[] = command.split(",");

        if (split.length != 2) {
            return false;
        }

        int intensity = Math.max(0, Math.round(Byte.decode(split[0]) / 5.0f));

        if ("Random".equals(split[1])) {
            intensity = random.nextInt(intensity);
        }

        LovesenseInterface.setIntensity(getContext(), intensity);

        return true;
    }
}

package com.web.devices;

import com.devices.lovesense.LovesenseController;
import com.web.WebController;

import java.util.Random;

import static com.common.Common.sleep;

public class LovesenseWebController extends LovesenseController implements WebController {
    private Random random = new Random();

    @Override
    public String getTypeName() {
        return "LoveSense";
    }

    @Override
    public String getControlURL(String sessionKey) {
        return "lovesense.php?session=" + sessionKey;
    }

    @Override
    public boolean acceptCommand(String command) {
        String split[] = command.split(",");

        if (split.length != 2) {
            return false;
        }

        int intensity = Math.round(Byte.decode(split[0]) / 5.0f);

        if ("Random".equals(split[1])) {
            intensity = random.nextInt(intensity);
        }

        // for (int n = 0; n < 8; n++) {
        //   setIntensity(intensity);
        sleep(2000);
        //}

        return true;
    }
}

package com.web.devices;

import com.devices.visionbody.VisionBodyController;
import com.web.WebController;

import static com.common.Common.sleep;
import static com.devices.visionbody.VisionBodyConstants.GET_INTENSITY_INTERVAL;
import static com.devices.visionbody.VisionBodyConstants.POLL_INTERVAL;

public class VisionBodyWebController extends VisionBodyController implements WebController {
    private long sinceLastGetIntensity = System.currentTimeMillis();


    public String getControlURL( String sessionKey) {
        return "visionbody.php?session=" + sessionKey;
    }

    @Override
    public String getTypeName() {
        return "VisionBody";
    }

    @Override
    public boolean acceptCommand(String command) {
        String split[] = command.split(",");

        if (split.length != 12) {
            return false;
        }


        long starttime = System.currentTimeMillis();

        this.loadProgram(split[0], split[1], Byte.decode(split[2]), Byte.decode(split[3]));
        this.setIntensity(Byte.decode(split[4]), Byte.decode(split[5]), Byte.decode(split[6]), Byte.decode(split[7]), Byte.decode(split[8]), Byte.decode(split[9]), Byte.decode(split[10]), Byte.decode(split[11]));

        if ((System.currentTimeMillis() - sinceLastGetIntensity) > GET_INTENSITY_INTERVAL) {
            this.getIntensity();
        }

        sleep(POLL_INTERVAL - (System.currentTimeMillis() - starttime));
        return true;
    }
}

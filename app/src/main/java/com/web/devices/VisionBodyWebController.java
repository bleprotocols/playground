package com.web.devices;

import com.devices.visionbody.VisionBodyInterface;
import com.session.HTTPController;

public class VisionBodyWebController extends HTTPController {
    public String getControlURL() {
        return "visionbody.php?session=" + getSessionKey();
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

        VisionBodyInterface.loadProgram(this.getContext(), split[0], split[1], Byte.decode(split[2]), Byte.decode(split[3]));
        VisionBodyInterface.setIntensity(this.getContext(), Byte.decode(split[4]), Byte.decode(split[5]), Byte.decode(split[6]), Byte.decode(split[7]), Byte.decode(split[8]), Byte.decode(split[9]), Byte.decode(split[10]), Byte.decode(split[11]));

        return true;
    }
}

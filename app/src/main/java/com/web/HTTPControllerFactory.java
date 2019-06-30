package com.web;

import com.session.HTTPController;
import com.web.devices.Et312bWebController;
import com.web.devices.LockWebController;
import com.web.devices.LovesenseWebController;
import com.web.devices.VisionBodyWebController;
import com.web.devices.Et302rWebController;
import com.web.devices.HearRateWebController;

import java.util.Arrays;
import java.util.List;

public class HTTPControllerFactory {
    public static List<HTTPController> all() {
        return Arrays.asList(new LovesenseWebController(), new VisionBodyWebController(), new LockWebController(), new Et312bWebController(), new Et302rWebController(),new HearRateWebController());
    }

    public static HTTPController get(String deviceName) {
        return all().stream().filter(x -> x.getTypeName().equals(deviceName)).findAny().orElse(null);
    }
}


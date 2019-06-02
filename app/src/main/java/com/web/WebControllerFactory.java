package com.web;

import com.web.devices.ErostekWebController;
import com.web.devices.LockWebController;
import com.web.devices.LovesenseWebController;
import com.web.devices.VisionBodyWebController;

import java.util.Arrays;
import java.util.List;

public class WebControllerFactory {
    public static List<WebController> all() {
        return Arrays.asList(new LovesenseWebController(), new VisionBodyWebController(), new LockWebController(), new ErostekWebController());
    }

    public static WebController get(String deviceName) {
        return all().stream().filter(x -> x.getTypeName().equals(deviceName)).findAny().orElse(null);
    }
}


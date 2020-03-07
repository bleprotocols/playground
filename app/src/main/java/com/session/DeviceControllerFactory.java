package com.session;

import com.bluetooth.Controller;
import com.devices.et312b.Et312BController;
import com.devices.heartrate.HeartrateSensorController;
import com.devices.lock.LockController;
import com.devices.lovesense.LovesenseController;
import com.devices.relay.RelayController;
import com.devices.visionbody.VisionBodyController;
import com.devices.et302r.ET302RController;

import java.util.Arrays;
import java.util.List;

public class DeviceControllerFactory {
    public static List<Controller> all() {
        return Arrays.asList(new RelayController(), new LovesenseController(), new VisionBodyController(), new LockController(), new Et312BController(), new HeartrateSensorController(), new ET302RController());
    }


    public static Controller get(String deviceName) {
        return all().stream().filter(x -> x.getTypeName().equals(deviceName)).findAny().orElse(null);
    }
}

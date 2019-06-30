package com.web.devices;

import com.devices.lock.LockInterface;
import com.session.HTTPController;

public class LockWebController extends HTTPController {
    @Override
    public String getTypeName() {
        return "SmartLock";
    }

    @Override
    public String getControlURL( ) {
        return "lock.php?session=" + getSessionKey();
    }


    @Override
    public boolean acceptCommand(String command) {
        //From the moment we recieve an open command - enable the user to open the lock
        LockInterface.setLocked(getContext(),command.startsWith("Locked"));

        return true;
    }
}

package com.web.devices;

import com.devices.lock.LockController;
import com.web.WebController;

import static com.common.Common.sleep;

public class LockWebController extends LockController implements WebController {

    private static final long MAX_TIME_UNLOCK = 60000;
    private long unlockstarttime = System.currentTimeMillis();

    @Override
    public String getTypeName() {
        return "SmartLock";
    }

    @Override
    public String getControlURL(String sessionKey) {
        return "lock.php?session=" + sessionKey;
    }


    @Override
    public boolean acceptCommand(String command) {
        //From the moment we recieve an open command - enable the user to open the lock
        if (!command.startsWith("Locked")) {
            unlockstarttime = System.currentTimeMillis();
        }

        //Always try connecting
        this.connect();

        //updating our status often is not that important
        sleep(6000);
        return true;
    }


    @Override
    protected void onConnect() {
        if ((System.currentTimeMillis() - unlockstarttime) < MAX_TIME_UNLOCK) {

            //you have to authenticate with a password before any of the other messages can be sent.
            if (this.authPassword(0)) {
                this.getLockEnergy();
                this.operateLock();
            }
        }
    }

}

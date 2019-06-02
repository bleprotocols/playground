package com.session;

import com.common.ReturnValue;
import com.web.WebController;

import org.jsoup.Jsoup;

public class DeviceSession implements Runnable {
    private Thread runnerThread;
    private String sessionURL;
    private WebController webController;

    private ReturnValue returnValue = new ReturnValue();
    private boolean closeSession = false;

    public DeviceSession setURL(String URL) {
        this.sessionURL = URL;
        return this;
    }

    public DeviceSession setController(WebController webController) {
        this.webController = webController;
        return this;
    }

    public DeviceSession makeThread() {
        synchronized (this) {
            if (runnerThread != null) {
                return this;
            }

            runnerThread = new Thread(this);
        }

        runnerThread.start();
        return this;
    }

    private String sessionFromUrl(String url) {
        try {
            return Jsoup.connect(url).execute().body();
        } catch (Exception ex) {
            return "";
        }
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                if (closeSession) {
                    returnValue.doReturn(1);
                    return;
                }

                webController.acceptCommand(sessionFromUrl(sessionURL));
            }
        }
    }

    public void close() {
        synchronized (this) {
            closeSession = true;
        }

        returnValue.await(0);
    }
}

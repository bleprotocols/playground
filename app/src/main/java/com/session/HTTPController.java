package com.session;

import android.content.Context;

import com.bluetooth.Controller;
import com.common.ReturnValue;
import com.common.SharedSettings;

import org.jsoup.Jsoup;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.common.Common.wrap;

public abstract class HTTPController implements Controller {
    private String sessionKey;
    private String baseURL;
    private Context context;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();


    private String sessionFromUrl(String url) {
        try {
            return Jsoup.connect(url).execute().body();
        } catch (Exception ex) {
            return "";
        }
    }

    @Override
    public void startControlling() {
        scheduler.scheduleAtFixedRate(wrap(() -> acceptCommand(sessionFromUrl(getSessionURL()))), 0, getInterval(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void stopControlling() {
        scheduler.shutdown();
        try {
            scheduler.awaitTermination(5, TimeUnit.SECONDS);
            scheduler = Executors.newSingleThreadScheduledExecutor();
        } catch (Exception ex) {
        }

    }

    @Override
    public String getTypeName() {
        return null;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
        this.baseURL = SharedSettings.getWebsiteURL(getContext());
    }

    protected Context getContext() {
        return this.context;
    }


    protected String getSessionKey() {
        return sessionKey;
    }

    protected String getBaseURL(){
        return this.baseURL;
    }
    public abstract String getControlURL();


    public abstract boolean acceptCommand(String command);

    public String getSessionURL() {
        return baseURL + "/get_session.php?session=" + getSessionKey();
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public int getInterval(){
        return 2000;
    }
}

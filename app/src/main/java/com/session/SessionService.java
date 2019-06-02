package com.session;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.bluetooth.BluetoothConnection;
import com.gui.BroadcastOutputStream;
import com.common.Common;
import com.web.WebController;
import com.web.WebControllerFactory;
import com.example.visionbodyremote.R;
import com.ble.GattDeviceConnection;
import com.gui.BluetoothDeviceListItem;
import com.gui.MainActivity;
import com.gui.SessionActivity;

import org.jsoup.Jsoup;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class SessionService extends Service {
    private String controllers = "";
    private Intent intent;
    List<DeviceSession> sessionList = new ArrayList<>();

    PrintStream printStream = new PrintStream(new BroadcastOutputStream(this, "devlog"));
    PrintStream sessionStream = new PrintStream(new BroadcastOutputStream(this, "sessions"));

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification updateNotification() {

        NotificationCompat.Builder mBuilder;
        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Running session")
                .setContentText("Running session")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true);

        Intent resultIntent = new Intent(this, SessionActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        resultIntent.putExtra("devices", intent.getSerializableExtra("devices"));
        resultIntent.putExtra("url", intent.getStringExtra("url"));
        resultIntent.putExtra("controllers", controllers);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addParentStack(SessionActivity.class);

        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        Notification notification = mBuilder.build();

        return notification;
    }

    private String[] sessionFromUrl(String url, int splitCount) {
        try {
            String body = Jsoup.connect(url).execute().body();
            String split[] = body.split(",");

            if (split.length != splitCount) {
                throw new RuntimeException();
            }
            return split;
        } catch (Exception ex) {
            printStream.println("Failed to get URL: " + url);
        }

        return new String[]{};
    }

    private void connectToDevice(String serverURL, String deviceAddress, String deviceType) {
        String sessionCode = Common.generateString(6);
        String connect_url = serverURL + "/get_session.php?session=" + sessionCode;
        WebController webController = WebControllerFactory.get(deviceType);

        if (webController instanceof GattDeviceConnection) {
            ((GattDeviceConnection) webController).setaddress(deviceAddress)
                    .setContext(this.getBaseContext())
                    .setLogger(printStream);
        } else {
            ((BluetoothConnection) webController)
                    .setaddress(deviceAddress)
                    .setLogger(printStream);
        }

        controllers += webController.getControlURL(sessionCode) + ",";

        Intent intent = new Intent("sessions");
        intent.putExtra("extra", serverURL + "iframe.php?links=" + controllers);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);


        sessionList.add(new DeviceSession()
                .setController(webController)
                .setURL(connect_url)
                .makeThread());
    }


    @Override
    public void onDestroy() {
        sessionList.forEach(x -> x.close());
        sessionList.clear();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return Service.START_REDELIVER_INTENT;
        }

        Object[] devices = (Object[]) intent.getSerializableExtra("devices");

        if (devices == null || intent.getStringExtra("url") == null) {
            return Service.START_REDELIVER_INTENT;
        }

        String url = intent.getStringExtra("url");
        this.intent = intent;

        printStream = new PrintStream(new BroadcastOutputStream(this, "devlog"));
        sessionStream = new PrintStream(new BroadcastOutputStream(this, "sessions"));

        if (intent.getAction().contains("start")) {
            for (int i = 0; i < devices.length; i++) {
                this.connectToDevice(url,
                        ((BluetoothDeviceListItem) devices[i]).getAddress(),
                        ((BluetoothDeviceListItem) devices[i]).getControllerName());
            }
            startForeground(101, updateNotification());

        } else {
            stopForeground(true);
            stopSelf();
        }

        return Service.START_STICKY;
    }
}

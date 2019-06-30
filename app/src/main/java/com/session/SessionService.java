
package com.session;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.bluetooth.BluetoothDevice;
import com.bluetooth.Controller;
import com.common.SharedSettings;
import com.gui.BluetoothDeviceList;
import com.gui.BroadcastOutputStream;
import com.example.visionbodyremote.R;
import com.gui.SessionActivity;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.common.Common.sleep;

public class SessionService extends Service {
    private String controllers = "";
    private Intent intent;
    List<Controller> controllerList = new ArrayList<>();
    Map<String, List<Controller>> actionTasks = new HashMap<>();

    PrintStream printStream = new PrintStream(new BroadcastOutputStream(this, "devlog"));


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


        //  stackBuilder.addParentStack(MainActivity.class);
        // stackBuilder.addParentStack(SessionActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(resultIntent);
        mBuilder.setContentIntent(stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT));

        return mBuilder.build();
    }


    @Override
    public void onDestroy() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return Service.START_REDELIVER_INTENT;
        }

        //get devices from settings
        //listen for settings changed
        //start all devices here


        this.intent = intent;
        serviceInstance = this;

        if (intent.getAction().contains("start")) {
            startForeground(101, updateNotification());
        } else {
            stopForeground(true);
            stopSelf();
        }

        return Service.START_STICKY;
    }

    //rename this
    public void addActionTask(String action, Controller webController) {
        actionTasks.putIfAbsent(action, new ArrayList<>());
        webController.setContext(this);
        webController.startControlling();
        actionTasks.get(action).add(webController);
    }

    public void removeActionTask(String action) {
        actionTasks.computeIfPresent(action, (x, y) -> {
            y.forEach(Controller::stopControlling);
            return null;
        });
    }

    public void refreshDevices() {
        BluetoothDeviceList bluetoothDeviceList = SharedSettings.getBluetoothDeviceList(this);

        if (bluetoothDeviceList == null) {
            return;
        }

        controllerList.stream().filter(
                x -> bluetoothDeviceList.stream().noneMatch(y -> y.getControllerName().equals(x.getTypeName()))
        ).collect(Collectors.toList())
                .forEach(x -> {
                    x.stopControlling();
                    controllerList.remove(x);
                });

        bluetoothDeviceList.stream()
                .filter(x -> x.getEnabled())
                .forEach(
                        x -> {
                            Controller controller = DeviceControllerFactory.get(x.getControllerName());
                            controller.setContext(this);

                            if (controller instanceof com.bluetooth.BluetoothDevice) {
                                ((BluetoothDevice) controller).setaddress(x.getAddress());
                                ((BluetoothDevice) controller).setLogger(printStream);
                            }

                            controller.startControlling();
                            controllerList.add(controller);
                        }
                );

    }


    private static SessionService serviceInstance = null;
    private static Object lock = new Object();

    //why even bind when you can have a static variable?
    public static SessionService getInstance(Context context) {
        synchronized (lock) {
            if (!isServiceRunning(context)) {
                serviceInstance = null;
                Intent serviceIntent = new Intent(context, SessionService.class);
                serviceIntent.setAction("start");
                serviceIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startService(serviceIntent);
            }
        }

        while (true) {
            synchronized (lock) {
                if (serviceInstance != null) {
                    return serviceInstance;
                }
            }
            sleep(10);
        }
    }

    public static boolean isServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        return manager.getRunningServices(Integer.MAX_VALUE)
                .stream()
                .anyMatch(x -> SessionService.class.getName().equals(x.service.getClassName()));
    }
}
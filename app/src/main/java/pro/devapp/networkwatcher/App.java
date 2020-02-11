package pro.devapp.networkwatcher;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import pro.devapp.networkwatcher.logic.NetworkController;
import pro.devapp.networkwatcher.storage.AppDataBase;

public class App extends Application {

    public static final String SERVICE_NOTIFICATION_CHANNEL_ID = "ScannerNotification";
    public static final String NOTIFICATION_CHANNEL_ID = "NewDeviceNotification";
    private AppDataBase appDataBase;
    private NetworkController networkController;

    @Override
    public void onCreate() {
        super.onCreate();
        appDataBase = AppDataBase.init(getApplicationContext());
        networkController = new NetworkController(getApplicationContext(), appDataBase);
        createNotificationChanel();
    }

    public AppDataBase getAppDataBase() {
        return appDataBase;
    }

    public NetworkController getNetworkController() {
        return networkController;
    }

    private void createNotificationChanel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel foregroundNotificationChannel = new NotificationChannel(
                SERVICE_NOTIFICATION_CHANNEL_ID,
                "Foreground scan service",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            foregroundNotificationChannel.setSound(null, null);
            foregroundNotificationChannel.setShowBadge(false);
            getSystemService(NotificationManager.class).createNotificationChannel(foregroundNotificationChannel);

            NotificationChannel notificationChannel = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "New device detected",
                NotificationManager.IMPORTANCE_HIGH
            );
            notificationChannel.setShowBadge(true);
            getSystemService(NotificationManager.class).createNotificationChannel(notificationChannel);
        }
    }
}

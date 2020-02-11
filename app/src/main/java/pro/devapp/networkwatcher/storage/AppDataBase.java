package pro.devapp.networkwatcher.storage;

import android.app.Application;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.atomic.AtomicBoolean;

import pro.devapp.networkwatcher.storage.dao.NetworkDeviceDao;
import pro.devapp.networkwatcher.storage.dao.NetworkDeviceHistoryDao;
import pro.devapp.networkwatcher.storage.entity.NetworkDeviceEntity;
import pro.devapp.networkwatcher.storage.entity.NetworkDeviceHistoryEntity;

@Database(entities = {NetworkDeviceEntity.class, NetworkDeviceHistoryEntity.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {

    private static final AtomicBoolean initialized = new AtomicBoolean();

    private static AppDataBase appDataBase;

    public static AppDataBase init(Application application) {
        return init((Context) application);
    }

    public static AppDataBase init(Context context) {
        if (!initialized.getAndSet(true)) {
            appDataBase = Room.databaseBuilder(context, AppDataBase.class, "network_monitor")
                .addMigrations()
                .build();
        }
        return appDataBase;
    }

    public abstract NetworkDeviceDao networkDeviceDao();
    public abstract NetworkDeviceHistoryDao networkDeviceHistoryDao();

}

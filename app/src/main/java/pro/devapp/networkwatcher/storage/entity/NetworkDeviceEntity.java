package pro.devapp.networkwatcher.storage.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "NetworkDevice")
public class NetworkDeviceEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private final long id;

    @ColumnInfo(name = "name")
    private final String name;

    @ColumnInfo(name = "mac")
    private final String mac;

    @ColumnInfo(name = "ip")
    private final String ip;

    @ColumnInfo(name = "createdAt")
    private final long createdAt;

    @ColumnInfo(name = "updatedAt")
    private final long updatedAt;

    public NetworkDeviceEntity(long id, String name, String mac, String ip, long createdAt, long updatedAt) {
        this.id = id;
        this.name = name;
        this.mac = mac;
        this.ip = ip;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public String getMac() {
        return mac;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }
}

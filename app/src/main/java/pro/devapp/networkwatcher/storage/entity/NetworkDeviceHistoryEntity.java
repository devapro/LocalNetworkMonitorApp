package pro.devapp.networkwatcher.storage.entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "NetworkDeviceHistory",
    foreignKeys = {
        @ForeignKey(entity = NetworkDeviceEntity.class, parentColumns = "id",
            childColumns = "deviceId", onDelete = ForeignKey.CASCADE)
    }
)
public class NetworkDeviceHistoryEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private final long id;

    @ColumnInfo(name = "deviceId")
    private final long deviceId;

    @ColumnInfo(name = "ip")
    private final String ip;

    @ColumnInfo(name = "state")
    private final String state;

    @ColumnInfo(name = "createdAt")
    private final long createdAt;

    public NetworkDeviceHistoryEntity(long id, long deviceId, String ip, State state, long createdAt) {
        this.id = id;
        this.deviceId = deviceId;
        this.ip = ip;
        this.state = state.toString();
        this.createdAt = createdAt;
    }

    public NetworkDeviceHistoryEntity(long id, long deviceId, String ip, String state, long createdAt) {
        this.id = id;
        this.deviceId = deviceId;
        this.ip = ip;
        this.state = state;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public String getIp() {
        return ip;
    }

    public State getStateType() {
        return State.valueOf(state);
    }

    public String getState() {
        return state;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public enum State{
        ONLINE,
        OFFLINE
    }
}

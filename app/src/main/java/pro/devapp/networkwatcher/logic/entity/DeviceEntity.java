package pro.devapp.networkwatcher.logic.entity;

import androidx.core.util.ObjectsCompat;

import pro.devapp.networkwatcher.storage.entity.NetworkDeviceHistoryEntity;

public class DeviceEntity {
    private final long id;
    private final String name;
    private final String ip;
    private final String mac;
    private final long createdAt;
    private final long updatedAt;
    private final NetworkDeviceHistoryEntity.State lastState;

    public DeviceEntity(long id, String name, String ip, String mac, long createdAt, long updatedAt, NetworkDeviceHistoryEntity.State lastState) {
        this.id = id;
        this.name = name;
        this.ip = ip;
        this.mac = mac;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastState = lastState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceEntity that = (DeviceEntity) o;
        return id == that.id &&
            createdAt == that.createdAt &&
            updatedAt == that.updatedAt &&
            ObjectsCompat.equals(name, that.name) &&
            ObjectsCompat.equals(ip, that.ip) &&
            ObjectsCompat.equals(mac, that.mac) &&
            lastState == that.lastState;
    }

    @Override
    public int hashCode() {
        return ObjectsCompat.hash(id, name, ip, mac, createdAt, updatedAt, lastState);
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

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getMac() {
        return mac;
    }

    public NetworkDeviceHistoryEntity.State getLastState() {
        return lastState;
    }

    public boolean isConnected(){
        return lastState == NetworkDeviceHistoryEntity.State.ONLINE;
    }
}

package pro.devapp.networkwatcher.storage.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import pro.devapp.networkwatcher.storage.entity.NetworkDeviceEntity;
import pro.devapp.networkwatcher.storage.entity.NetworkDeviceHistoryEntity;

@Dao
public interface NetworkDeviceHistoryDao extends BaseDao<NetworkDeviceHistoryEntity> {
    @Query("SELECT * from NetworkDeviceHistory WHERE deviceId = :deviceId ORDER BY createdAt DESC LIMIT 1")
    NetworkDeviceHistoryEntity getLastState(long deviceId);
}

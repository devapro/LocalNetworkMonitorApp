package pro.devapp.networkwatcher.storage.dao;

import androidx.room.Dao;
import androidx.room.Query;
import java.util.List;

import pro.devapp.networkwatcher.storage.entity.NetworkDeviceEntity;

@Dao
public interface NetworkDeviceDao extends BaseDao<NetworkDeviceEntity>{
    @Query("SELECT * from NetworkDevice ORDER BY updatedAt ASC")
    List<NetworkDeviceEntity> devices();

    @Query("SELECT COUNT(id) from NetworkDevice")
    int devicesCount();

    @Query("SELECT * from NetworkDevice WHERE ip = :ip LIMIT 1")
    NetworkDeviceEntity findByIp(String ip);

    @Query("SELECT * from NetworkDevice WHERE mac = :mac LIMIT 1")
    NetworkDeviceEntity findByMac(String mac);

    @Query("SELECT * from NetworkDevice WHERE id = :id LIMIT 1")
    NetworkDeviceEntity findById(long id);
}

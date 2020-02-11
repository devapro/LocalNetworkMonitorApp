package pro.devapp.networkwatcher.storage.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

import java.util.List;

interface BaseDao<T> {

    @Insert
    Long add(T t);

    @Insert
    List<Long> add(List<T> items);

    @Update
    int update(T t);

    @Delete
    void delete(T t);

    @Delete
    void delete(List<T> items);
}

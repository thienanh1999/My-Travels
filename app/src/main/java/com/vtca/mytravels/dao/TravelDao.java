package com.vtca.mytravels.dao;

import com.vtca.mytravels.entity.Travel;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface TravelDao {

    @Query("SELECT * from travel ORDER BY id DESC")
    LiveData<List<Travel>> getAllTravels();

    @Query("SELECT * from travel ORDER BY title")
    LiveData<List<Travel>> getAllTravelsByTitleAsc();

    @Query("SELECT * from travel ORDER BY title DESC")
    LiveData<List<Travel>> getAllTravelsByTitleDesc();

    @Query("SELECT * from travel ORDER BY dateTime")
    LiveData<List<Travel>> getAllTravelsByStartAsc();

    @Query("SELECT * from travel ORDER BY dateTime DESC")
    LiveData<List<Travel>> getAllTravelsByStartDesc();

    @Query("SELECT * FROM travel where (dateTimeLong - :currentTime) < :range and (dateTimeLong - :currentTime) > 0")
    LiveData<List<Travel>> getAllTravelsUpComing(long currentTime, long range);

    @Query("SELECT * FROM travel where (dateTimeLong - :currentTime) < :range and (dateTimeLong - :currentTime) > 0")
    List<Travel> getAllTravelsUpComingWithoutLiveData(long currentTime, long range);

    @Insert
    void insert(Travel travel);

    @Update
    void update(Travel travel);

    @Delete
    void delete(Travel... travels);

    @Query("SELECT * FROM travel where id=:id")
    LiveData<Travel> getTravelById(long id);

}

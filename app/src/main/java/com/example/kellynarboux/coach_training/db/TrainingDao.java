package com.example.kellynarboux.coach_training.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TrainingDao {

    @Insert
    void insert(Training training);

    @Update
    int update(Training training);

    @Query("SELECT * FROM training")
    LiveData<List<Training>> getAll();
}
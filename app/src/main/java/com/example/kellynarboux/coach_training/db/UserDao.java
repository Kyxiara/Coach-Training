package com.example.kellynarboux.coach_training.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void insert(User user);

    @Query("SELECT * FROM user")
    LiveData<List<User>> getAll();

    @Query("DELETE FROM user")
    void deleteAll();
}
package com.example.kellynarboux.coach_training.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.example.kellynarboux.coach_training.model.Exercise;

import java.util.Date;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = User.class,
        parentColumns = "pseudo",
        childColumns = "user_pseudo",
        onDelete = CASCADE),
        primaryKeys = {"user_pseudo","date"},
        indices = {@Index("user_pseudo")})
public class Training {
    @NonNull
    private String user_pseudo;

    @NonNull  @TypeConverters(DateConverter.class)
    private Date date;

    @TypeConverters(ExerciceConverter.class)
    private Exercise exercice;

    public Training(@NonNull String user_pseudo, @NonNull Date date, Exercise exercice){
        this.user_pseudo = user_pseudo;
        this.date = date;
        this.exercice = exercice;
    }

    @NonNull
    public String getUser_pseudo() {
        return user_pseudo;
    }

    public void setUser_pseudo(@NonNull String user_pseudo) {
        this.user_pseudo = user_pseudo;
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }

    public Exercise getExercice() {
        return exercice;
    }

    public void setExercice(Exercise exercice) {
        this.exercice = exercice;
    }
}

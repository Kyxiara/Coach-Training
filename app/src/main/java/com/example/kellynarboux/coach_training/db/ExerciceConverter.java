package com.example.kellynarboux.coach_training.db;

import android.arch.persistence.room.TypeConverter;

import com.example.kellynarboux.coach_training.model.Exercise;

public class ExerciceConverter {
    @TypeConverter
    public static Exercise fromString(String exercise) {
        return Exercise.textToExercise(exercise);
    }

    @TypeConverter
    public static String exerciseToString(Exercise exercise) {
        return exercise.getName();
    }
}
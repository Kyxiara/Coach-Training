package com.example.kellynarboux.coach_training.Model;

public class CountableExercise extends Exercise {

    private int count;

    private CountableExerciseType countableExerciseType;

    public CountableExercise(String name, int count) {
        this.countableExerciseType = CountableExerciseType.valueOf(name);
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String getName() {
        return countableExerciseType.name();
    }
}

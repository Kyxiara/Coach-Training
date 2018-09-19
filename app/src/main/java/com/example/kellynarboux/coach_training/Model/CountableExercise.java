package com.example.kellynarboux.coach_training.Model;

public class CountableExercise extends Exercise {

    private int count;

    private CountableExerciseType countableExerciseType;

    public CountableExercise(CountableExerciseType type, int count) {
        this.countableExerciseType = type;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String getName() {
        return countableExerciseType.name();
    }

    public CountableExerciseType getCountableExerciseType() {
        return countableExerciseType;
    }
}

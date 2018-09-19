package com.example.kellynarboux.coach_training.Model;

public class CountableExercise extends Exercise {

    private int count;

    CountableExercise(String name, int count) {
        super(name);
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}

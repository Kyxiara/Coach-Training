package main.java.com.example.kellynarboux.coach_training.Model;

public abstract class Exercise {

    private String name;

    Exercise(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

package com.example.kellynarboux.coach_training.Model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Exercise {

    // Regex to match vocal exercise
    private static final Pattern p = Pattern.compile("\\D*(\\d+)\\s*(\\S+)");

    public abstract String getName();

    public static Exercise textToExercise(String text) {
        Matcher m = p.matcher(text);
        boolean b = m.matches();
        if (b) {
            if(m.group(2) != null){
                return new CountableExercise(m.group(2), Integer.parseInt(m.group(1)));
            } else {
                return null; // another exercise type
            }
        } else {
            return null; // no match for an exercise
        }
    }
}

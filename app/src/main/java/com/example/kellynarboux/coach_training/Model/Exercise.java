package com.example.kellynarboux.coach_training.Model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Exercise {

    // Regex to match vocal exercise
    private static final Pattern p = Pattern.compile("\\D*(\\d+)\\s*(\\S+)s*");

    public abstract String getName();

    /**
     * Create a new Exercise from a text
     * @param text : a description of a the exercise : ex : do 15 push-up
     * @return the corresponding Excise
     */
    public static Exercise textToExercise(String text) {
        Matcher m = p.matcher(text);
        boolean b = m.matches();
        if (b) {
            if(m.group(2) != null){
                String type = m.group(2);
                String count = m.group(1);
                return new CountableExercise(CountableExerciseType.valueOf(type), Integer.parseInt(count));
            } else {
                return null; // another exercise type
            }
        } else {
            return null; // no match for an exercise
        }
    }
}

package com.example.kellynarboux.coach_training.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class User {
    @PrimaryKey @NonNull
    private String pseudo;

    @ColumnInfo(name = "weight")
    private float weight;

    @ColumnInfo(name = "height")
    private float height;

    @ColumnInfo(name = "age")
    private int age;

    @ColumnInfo(name = "gender")
    private String gender;

    public User(){
        pseudo = "Anonymous";  // FIXME
    }

    public User(@NonNull String name){
        pseudo = name;
    }

    public User(@NonNull String name, float weight, float height, int age, Gender gender){
        pseudo = name;
        this.weight = weight;
        this.height = height;
        this.age = age;
        this.gender = gender.name();
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender.name();
    }

    // Getters and setters are ignored for brevity,
    // but they're required for Room to work.
}

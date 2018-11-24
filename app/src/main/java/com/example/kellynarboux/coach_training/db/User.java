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
    private int height;

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

    public User(@NonNull String name, float weight, int height, int age, Gender gender){
        pseudo = name;
        this.weight = weight;
        this.height = height;
        this.age = age;
        this.gender = gender.name();
    }

    @NonNull
    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(@NonNull String pseudo) {
        this.pseudo = pseudo;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
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
}

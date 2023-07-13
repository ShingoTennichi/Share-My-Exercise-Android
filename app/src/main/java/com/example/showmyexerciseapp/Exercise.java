package com.example.showmyexerciseapp;

public class Exercise {
    private int id;
    private int imageSmall;
    private int imageLarge;
    private String name;
    private String instruction;

    public Exercise(int id, int imageSmall, int imageLarge, String name, String instruction) {
        this.id = id;
        this.imageSmall = imageSmall;
        this.imageLarge = imageLarge;
        this.name = name;
        this.instruction = instruction;
    }

    public int getId() {
        return id;
    }

    public int getImageSmall() {
        return imageSmall;
    }

    public int getImageLarge() {
        return imageLarge;
    }

    public String getName() {
        return name;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImageSmall(int imageSmall) {
        this.imageSmall = imageSmall;
    }

    public void setImageLarge(int imageLarge) {
        this.imageLarge = imageLarge;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
}

package com.example.personaltutoringservice;

public class Tutor {

    private String Name;
    private String Subject;
    private Double Rating;
    private Double Price;
    private String Availability;

    // Required empty constructor for Firebase
    public Tutor() {}

    public String getName() {
        return Name;
    }

    public String getSubject() {
        return Subject;
    }

    public Double getRating() {
        return Rating;
    }

    public Double getPrice() {
        return Price;
    }

    public String getAvailability() {
        return Availability;
    }
}
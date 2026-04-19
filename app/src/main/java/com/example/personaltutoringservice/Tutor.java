package com.example.personaltutoringservice;

import java.util.ArrayList;
import java.util.List;

public class Tutor {
    private String Id;
    private String Name;
    private String Subject;
    private String Price;
    private String Location;
    private double Rating;
    private List<String> Feedback;

    public Tutor() {
        Feedback = new ArrayList<>();
    }

    public Tutor(String name, String subject, String price, String location) {
        this.Name = name;
        this.Subject = subject;
        this.Price = price;
        this.Location = location;
        this.Feedback = new ArrayList<>();
    }

    public Tutor(String id, String name, String subject, String price, String location, double rating, List<String> feedback) {
        this.Id = id;
        this.Name = name;
        this.Subject = subject;
        this.Price = price;
        this.Location = location;
        this.Rating = rating;
        this.Feedback = feedback != null ? feedback : new ArrayList<>();
    }

    public String getId() { return Id; }
    public String getName() { return Name; }
    public String getSubject() { return Subject; }
    public String getPrice() { return Price; }
    public String getLocation() { return Location; }
    public double getRating() { return Rating; }
    public List<String> getFeedback() { return Feedback; }

    public void setId(String id) { this.Id = id; }
    public void setName(String name) { this.Name = name; }
    public void setSubject(String subject) { this.Subject = subject; }
    public void setPrice(String price) { this.Price = price; }
    public void setLocation(String location) { this.Location = location; }
    public void setRating(double rating) { this.Rating = rating; }
    public void setFeedback(List<String> feedback) { this.Feedback = feedback; }
}
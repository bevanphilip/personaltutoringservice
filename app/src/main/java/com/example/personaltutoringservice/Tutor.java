package com.example.personaltutoringservice;

public class Tutor {
    private String Name;
    private Double Price;
    private Double Rating;
    private String Subject;

    // Required empty constructor for Firestore
    public Tutor() {}

    public Tutor(String name, Double price, Double rating,
                 String subject) {
        this.Name = name;
        this.Price = price;
        this.Rating = rating;
        this.Subject = subject;
    }

    public String getName() { return Name; }
    public Double getPrice()   { return Price; }
    public Double getRating() { return Rating; }
    public String getSubject()    { return Subject; }
}
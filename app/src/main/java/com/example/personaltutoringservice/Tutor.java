package com.example.personaltutoringservice;

public class Tutor {
    private String Name;
    private String Subject;
    private String Price;
    private String Location;

    public Tutor() {}

    public Tutor(String name, String subject, String price, String location) {
        this.Name = name;
        this.Subject = subject;
        this.Price = price;
        this.Location = location;
    }

    public String getName() { return Name; }
    public String getSubject()    { return Subject; }
    public String getPrice()   { return Price; }
    public String getLocation() { return Location; }
}
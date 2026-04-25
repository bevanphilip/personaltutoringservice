package com.example.personaltutoringservice;

public class Review {
    private String comment;
    private String studentName;
    private Double rating;

    public Review(String comment, String studentName, Double rating) {
        this.comment = comment;
        this.studentName = studentName;
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public String getStudentName() {
        return studentName;
    }

    public Double getRating() {
        return rating;
    }
}
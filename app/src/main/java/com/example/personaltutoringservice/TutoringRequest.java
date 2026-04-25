package com.example.personaltutoringservice;

public class TutoringRequest {
    private String id;
    private String tutorName;
    private String subject;
    private String availability;
    private String status;
    private Double price;
    private String requestedDate;
    private String requestedTime;
    private String sessionType;
    private String paymentStatus;

    public TutoringRequest(
            String id,
            String tutorName,
            String subject,
            String availability,
            String status,
            Double price,
            String requestedDate,
            String requestedTime,
            String sessionType,
            String paymentStatus
    ) {
        this.id = id;
        this.tutorName = tutorName;
        this.subject = subject;
        this.availability = availability;
        this.status = status;
        this.price = price;
        this.requestedDate = requestedDate;
        this.requestedTime = requestedTime;
        this.sessionType = sessionType;
        this.paymentStatus = paymentStatus;
    }

    public String getId() { return id; }
    public String getTutorName() { return tutorName; }
    public String getSubject() { return subject; }
    public String getAvailability() { return availability; }
    public String getStatus() { return status; }
    public Double getPrice() { return price; }
    public String getRequestedDate() { return requestedDate; }
    public String getRequestedTime() { return requestedTime; }
    public String getSessionType() { return sessionType; }
    public String getPaymentStatus() { return paymentStatus; }
}
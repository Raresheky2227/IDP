package com.example.eventmanager.model;

public class Event {
    private String id;
    private String title;
    private String description;
    private String pdfPath;

    // Constructors
    public Event() {}

    public Event(String id, String title, String description, String pdfPath) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.pdfPath = pdfPath;
    }

    // Getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPdfPath() { return pdfPath; }
    public void setPdfPath(String pdfPath) { this.pdfPath = pdfPath; }
}
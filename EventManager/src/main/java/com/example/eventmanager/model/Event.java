package com.example.eventmanager.model;

import javax.persistence.*;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // <-- THIS IS THE FIX!
    private Long id;

    private String title;
    private String description;
    private String pdfPath;

    public Event() {}

    public Event(Long id, String title, String description, String pdfPath) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.pdfPath = pdfPath;
    }
    public Event(String title, String description, String pdfPath) {
        this.title = title;
        this.description = description;
        this.pdfPath = pdfPath;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPdfPath() { return pdfPath; }
    public void setPdfPath(String pdfPath) { this.pdfPath = pdfPath; }
}

package com.imps.IMPS.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
public class HomeDetails {

    @Id
    private String id; // Unique identifier for the HomeDetails record

    @Column(columnDefinition = "TEXT")
    private String announcements; // To store announcements

    @Column(columnDefinition = "TEXT")
    private String guidelines; // To store guidelines

    @Column(columnDefinition = "TEXT")
    private String process; // To store process information

    @Column(columnDefinition = "TEXT")
    private String locations; // To store location information

    @Column(columnDefinition = "TEXT")
    private String updates; // To store updates

    // Getters and setters for each field
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(String announcements) {
        this.announcements = announcements;
    }

    public String getGuidelines() {
        return guidelines;
    }

    public void setGuidelines(String guidelines) {
        this.guidelines = guidelines;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getLocations() {
        return locations;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }

    public String getUpdates() {
        return updates;
    }

    public void setUpdates(String updates) {
        this.updates = updates;
    }
}

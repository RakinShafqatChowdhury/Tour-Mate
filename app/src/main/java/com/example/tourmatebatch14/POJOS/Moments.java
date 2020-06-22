package com.example.tourmatebatch14.POJOS;

public class Moments {
    private String momentId;
    private String eventId;
    private String momentDownloadUrl;

    public Moments() {
    }

    public Moments(String momentId, String eventId, String momentDownloadUrl) {
        this.momentId = momentId;
        this.eventId = eventId;
        this.momentDownloadUrl = momentDownloadUrl;
    }

    public String getMomentId() {
        return momentId;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getMomentDownloadUrl() {
        return momentDownloadUrl;
    }

    public void setMomentDownloadUrl(String momentDownloadUrl) {
        this.momentDownloadUrl = momentDownloadUrl;
    }
}

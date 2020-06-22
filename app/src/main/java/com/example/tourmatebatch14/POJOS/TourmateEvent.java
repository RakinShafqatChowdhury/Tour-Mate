package com.example.tourmatebatch14.POJOS;

public class TourmateEvent {
    private String eventID;
    private String eventName;
    private String destination;
    private String departure;
    private int initialBudget;
    private String departureDate;

    public TourmateEvent() {
    }

    public TourmateEvent(String eventID, String eventName, String destination, String departure, int initialBudget, String departureDate) {
        this.eventID = eventID;
        this.eventName = eventName;
        this.destination = destination;
        this.departure = departure;
        this.initialBudget = initialBudget;
        this.departureDate = departureDate;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public int getInitialBudget() {
        return initialBudget;
    }

    public void setInitialBudget(int initialBudget) {
        this.initialBudget = initialBudget;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }
}

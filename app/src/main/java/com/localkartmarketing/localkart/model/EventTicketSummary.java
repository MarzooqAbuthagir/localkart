package com.localkartmarketing.localkart.model;

public class EventTicketSummary {
    private String name;
    private String ticketCount;
    private String amount;
    private String available;
    private String booked;
    private String attended;
    private String description;

    public EventTicketSummary(String name, String ticketcount, String amount, String available, String booked, String attended, String description) {
        this.name = name;
        this.ticketCount = ticketcount;
        this.amount = amount;
        this.available = available;
        this.booked = booked;
        this.attended = attended;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTicketCount() {
        return ticketCount;
    }

    public void setTicketCount(String ticketCount) {
        this.ticketCount = ticketCount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public String getBooked() {
        return booked;
    }

    public void setBooked(String booked) {
        this.booked = booked;
    }

    public String getAttended() {
        return attended;
    }

    public void setAttended(String attended) {
        this.attended = attended;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

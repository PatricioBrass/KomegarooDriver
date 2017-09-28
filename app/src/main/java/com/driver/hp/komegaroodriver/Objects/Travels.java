package com.driver.hp.komegaroodriver.Objects;

/**
 * Created by HP on 01/03/2017.
 */

public class Travels {
    String calification;
    String code;
    String comments;
    String customerUid;
    String date;
    String endHour;
    String from;
    String startHour;
    String status;
    String to;
    Integer tripPrice;

    public Travels() {
    }

    public Travels(String calification, String code, String comments, String customerUid, String date, String endHour, String from, String startHour, String status, String to, Integer tripPrice) {
        this.calification = calification;
        this.code = code;
        this.comments = comments;
        this.customerUid = customerUid;
        this.date = date;
        this.endHour = endHour;
        this.from = from;
        this.startHour = startHour;
        this.status = status;
        this.to = to;
        this.tripPrice = tripPrice;
    }

    public String getCalification() {
        return calification;
    }

    public void setCalification(String calification) {
        this.calification = calification;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCustomerUid() {
        return customerUid;
    }

    public void setCustomerUid(String customerUid) {
        this.customerUid = customerUid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEndHour() {
        return endHour;
    }

    public void setEndHour(String endHour) {
        this.endHour = endHour;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getStartHour() {
        return startHour;
    }

    public void setStartHour(String startHour) {
        this.startHour = startHour;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Integer getTripPrice() {
        return tripPrice;
    }

    public void setTripPrice(Integer tripPrice) {
        this.tripPrice = tripPrice;
    }
}

package com.all.credstore.models;

import java.io.Serializable;

public final class Expense implements Serializable {

    private int id;
    private Long cost;
    private String comment;
    private Long time;

    public Expense(Long cost, String comment) {
        this.cost = cost;
        this.comment = comment;
    }

    public Expense(Long cost, String comment, Long time) {
        this.cost = cost;
        this.comment = comment;
        this.time = time;
    }

    public Expense(int id, Long cost, String comment, Long time) {
        this.id = id;
        this.cost = cost;
        this.comment = comment;
        this.time = time;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public Long getCost() {
        return cost;
    }
    public void setCost(Long cost) {
        this.cost = cost;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getTime() {
        return time;
    }
    public void setTime(Long time) {
        this.time = time;
    }
}

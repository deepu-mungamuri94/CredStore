package com.all.credstore.models;

import com.all.credstore.R;

import java.io.Serializable;

public final class Credentials implements Serializable {

    private int id;
    private String tag;
    private String username;
    private String password;
    private String url;
    private String secretKey;
    private String comment;
    private int imageResourceId= R.drawable.lock;

    public Credentials(int id, String tag, String username, String password, String url, String secretKey, String comment) {
        this.id = id;
        this.tag = tag;
        this.username = username;
        this.password = password;
        this.url = url;
        this.secretKey = secretKey;
        this.comment = comment;
    }

    public Credentials(String tag, String username, String password, String url, String comment, String secretKey) {
        this.tag = tag;
        this.username = username;
        this.password = password;
        this.url = url;
        this.comment = comment;
        this.secretKey = secretKey;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getSecretKey() {
        return secretKey;
    }
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }
}

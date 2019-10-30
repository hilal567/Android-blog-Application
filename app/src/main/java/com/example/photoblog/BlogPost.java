package com.example.photoblog;

import com.google.firebase.firestore.ServerTimestamp;


import java.util.Date;

public class BlogPost {

    private String user_id;
    private String image_url;
    private String desc;
    private String image_thumb;
    public Date timestamp;

// An empty constructor for instantiation
    public BlogPost (){}

//the constructor tha has all the fields
    public BlogPost(String user_id, String image_url, String desc, String image_thumb, Date timestamp) {
        this.user_id = user_id;
        this.image_url = image_url;
        this.desc = desc;
        this.image_thumb = image_thumb;
        this.timestamp = timestamp;
    }

    //encapsulate the fields to bbe reused in the retrieving of data

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}

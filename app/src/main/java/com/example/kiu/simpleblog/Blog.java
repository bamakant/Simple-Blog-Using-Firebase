package com.example.kiu.simpleblog;

/**
 * Created by bamakant on 30/12/17.
 */

public class Blog {

    private String title, desc, image,post_uid;

    public Blog() {

    }

    public Blog(String title, String desc, String image,String post_uid) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.post_uid = post_uid;
    }

    public String getPost_uid() {
        return post_uid;
    }

    public void setPost_uid(String post_uid) {
        this.post_uid = post_uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

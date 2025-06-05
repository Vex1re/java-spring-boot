package com.railway.helloworld.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Publications")
public class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String location;
    private String time;
    private String placeName;
    private String description;
    private int rating;
    private Integer commentsCount;
    private String tag;

    public Publication() {}

    public Publication(String name,String tag, String location, String time, String placeName, String description, int rating, int commentsCount) {
        this.name = name;
        this.location = location;
        this.time = time;
        this.placeName = placeName;
        this.description = description;
        this.rating = rating;
        this.commentsCount = commentsCount;
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Integer getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Integer commentsCount) {
        this.commentsCount = commentsCount;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name=" + name + '\'' +
                ", location=" + location + '\'' +
                ", time=" + time + '\'' +
                ", placeName=" + placeName + '\'' +
                ", description=" + description + '\'' +
                ", rating=" + rating + '\'' +
                ", commentsCount=" + commentsCount + '\'' +
                ", tag=" + tag + '\'' +
                '}';
    }

}

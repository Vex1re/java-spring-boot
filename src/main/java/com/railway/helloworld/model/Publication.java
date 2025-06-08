package com.railway.helloworld.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Type;

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
    private String login;

    @Column(columnDefinition = "jsonb")
    private String likes;

    @Column(columnDefinition = "jsonb")
    private String images;

    public Publication() {
        this.commentsCount = 0;
        this.likes = "[]";
    }

    public Publication(String name, String tag, String location, String time, String placeName, String description, int rating, Integer commentsCount, String login) {
        this.name = name;
        this.location = location;
        this.time = time;
        this.placeName = placeName;
        this.description = description;
        this.rating = rating;
        this.commentsCount = commentsCount != null ? commentsCount : 0;
        this.tag = tag;
        this.login = login;
        this.likes = "[]";
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getLikes() {
        return likes != null ? likes : "[]";
    }

    public void setLikes(String likes) {
        this.likes = likes != null ? likes : "[]";
    }

    @Override
    public String toString() {
        return "Publication{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", time='" + time + '\'' +
                ", placeName='" + placeName + '\'' +
                ", description='" + description + '\'' +
                ", rating=" + rating +
                ", commentsCount=" + commentsCount +
                ", tag='" + tag + '\'' +
                ", login='" + login + '\'' +
                ", likes='" + likes + '\'' +
                ", images='" + images + '\'' +
                '}';
    }

}

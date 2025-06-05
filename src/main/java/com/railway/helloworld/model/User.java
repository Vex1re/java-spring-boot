package com.railway.helloworld.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    private String login;
    private String email;
    private String city;
    private String password;

    public User() {}

    public User(String name, String surname, String login, String email, String city, String password) {
        this.name = name;
        this.surname = surname;
        this.login = login;
        this.email = email;
        this.city = city;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name=" + name + '\'' +
                ", surname=" + surname + '\'' +
                ", login=" + login + '\'' +
                ", email=" + email + '\'' +
                ", city=" + city + '\'' +
                ", password=" + password + '\'' +
                '}';
    }

}

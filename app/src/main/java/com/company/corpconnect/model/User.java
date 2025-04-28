package com.company.corpconnect.model;

public class User {
    public String email;
    public String name;
    public String role;

    public User() { }

    public User(String email, String name, String role) {
        this.email = email;
        this.name = name;
        this.role = role;
    }
}

package com.company.corpconnect.model;

public class User {
    public String email;
    public String name;
    public String surname;

    public String role;
    public String position;
    public String department;

    public User() { }

    public User(String email, String name, String surname, String role, String position, String department) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.position = position;
        this.department = department;
    }
}

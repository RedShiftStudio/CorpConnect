package com.company.corpconnect.model;

public class User {
    private String id;
    private String email;
    private String name;
    private String surname;
    private String role;
    private String position;
    private String department;

    public User() {
    }

    public User(String id, String email, String name, String surname, String role, String position, String department) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.position = position;
        this.department = department;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}

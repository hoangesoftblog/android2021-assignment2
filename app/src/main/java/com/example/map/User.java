package com.example.map;


import java.util.ArrayList;
import java.util.List;

public class User {
    public enum Role {
        USER, ADMIN
    };

    public String email;
    public String password;
    public String phoneNumber;
    public String username;
    public List<String> locationsJoined;
    public List<String> locationsOwned;

    public List<String> getLocationsJoined() {
        return locationsJoined;
    }

    public void setLocationsJoined(List<String> locationsJoined) {
        this.locationsJoined = locationsJoined;
    }

    public List<String> getLocationsOwned() {
        return locationsOwned;
    }

    public void setLocationsOwned(List<String> locationsOwned) {
        this.locationsOwned = locationsOwned;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role role;

//    public Role getRole() {
//        return role;
//    }
//
//    public void setRole(Role role) {
//        this.role = role;
//    }
}

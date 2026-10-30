package com.savebite.patterns.structural.adapter;

public class GoogleProfile {
    public String sub;
    public String email;
    public String name;
    public String picture;
    public boolean email_verified;

    public GoogleProfile(String sub, String email, String name) {
        this.sub = sub;
        this.email = email;
        this.name = name;
        this.email_verified = true;
    }
}


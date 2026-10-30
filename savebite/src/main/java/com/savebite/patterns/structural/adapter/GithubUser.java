package com.savebite.patterns.structural.adapter;

public class GithubUser {
    public long id;
    public String login;
    public String userEmail;
    public String displayName;

    public GithubUser(long id, String login, String userEmail, String displayName) {
        this.id = id;
        this.login = login;
        this.userEmail = userEmail;
        this.displayName = displayName;
    }
}


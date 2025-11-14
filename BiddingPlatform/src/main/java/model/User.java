package model;

import java.time.LocalDateTime;

public abstract class User {
    protected Long id;
    protected String username;
    protected String email;
    protected String password;
    protected LocalDateTime registeredAt;

    protected User(Long id, String username, String email,
                   String password, LocalDateTime registeredAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.registeredAt = registeredAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "\nUser ID: " + id + "\nUsername: " + username + "\nEmail: " + email + "\nPassword: " + password
                + "\nRegistered At: " + registeredAt;
    }
}

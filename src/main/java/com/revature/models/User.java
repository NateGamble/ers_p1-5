package com.revature.models;


import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.Objects;

/**
 * Base constructs for users, store only the integer representation of roles in the db for easier role checking
 */
@Entity
@DynamicInsert
@Table(name = "users", schema = "p1_5")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "serial")
    private int userId;

    @Column(name = "username", columnDefinition = "varchar(25) unique not null")
    private String username;

    @Column(name = "password", columnDefinition = "varchar(256) not null")
    private String password;

    @Column(name = "first_name", columnDefinition = "varchar(25) not null")
    private String firstname;

    @Column(name = "last_name", columnDefinition = "varchar(25) not null")
    private String lastname;

    @Column(name = "email", columnDefinition = "varchar(256) unique not null")
    private String email;

    @Column(name = "user_role_id", columnDefinition = "int not null")
    private Integer userRole;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    private boolean isActive = true;

    public User() {
        super();
    }

    public User(String username, String password, String firstname, String lastname, String email) {
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    public User(int userId, String username, String password, String firstname, String lastname, String email, Integer userRole, boolean isActive) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.userRole = userRole;
        this.isActive = isActive;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getUserRole() {
        return userRole;
    }

    public void setUserRole(Integer userRole) {
        this.userRole = userRole;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId
                && isActive == user.isActive
                && username.equals(user.username)
                && password.equals(user.password)
                && firstname.equals(user.firstname)
                && lastname.equals(user.lastname)
                && email.equals(user.email)
                && userRole.equals(user.userRole);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, password, firstname, lastname, email, userRole, isActive);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", password='" + "********" + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", userRole=" + userRole +
                ", isActive=" + isActive +
                '}';
    }
}


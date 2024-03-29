package com.opensymphony.able.model;

import java.util.Date;

/**
 * @author Nicholas Hill <a href="mailto:nick.hill@gmail.com">nick.hill@gmail.com</a>
 * @author <a href="mailto:jhouse@revolition.net">James House</a>
 */
public class User {
    private long id;
    private String username;
    private String email;
    private String name;
    private String passwordHash;
    private Date creationDate;
    private Date updateDate;

    public User() {
    }

    public User(String username, String email, String name, String passwordHash) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.passwordHash = passwordHash;
        this.creationDate = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final User user = (User) o;

        return id == user.id;
    }

    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}

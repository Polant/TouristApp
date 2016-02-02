package com.polant.touristapp.model.database;

/**
 * Created by Антон on 08.01.2016.
 */
public class Mark {

    private int id;
    private String name;
    private String description;
    private int userId;

    public Mark(int id, String name, String description, int userId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.userId = userId;
    }

    public Mark(String name, String description, int userId) {
        this.name = name;
        this.description = description;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public Mark setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Mark setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Mark setDescription(String description) {
        this.description = description;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Mark setUserId(int userId) {
        this.userId = userId;
        return this;
    }
}

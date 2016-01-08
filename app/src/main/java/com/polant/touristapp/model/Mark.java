package com.polant.touristapp.model;

/**
 * Created by Антон on 08.01.2016.
 */
public class Mark {

    private int id;
    private String name;
    private String description;

    public Mark(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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
}

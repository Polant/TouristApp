package com.polant.touristapp.model.database;

import java.io.Serializable;

/**
 * Created by Антон on 08.01.2016.
 */
public class UserMedia implements Serializable {

    private int id;
    private String name;
    private String description;
    private int userId;
    private double latitude;
    private double longitude;
    private String mediaExternalPath;
    private int isInGallery;
    private long createdDate;

    //так как в базе данные о поле isInGallery хранятся как числа, то ввожу две константы.
    public static final int NOT_IN_GALLERY = 0;
    public static final int IN_GALLERY = 1;

    public UserMedia(int id, String name, String description, int userId,
                     double latitude, double longitude,
                     String mediaExternalPath, int isInGallery, long createdDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mediaExternalPath = mediaExternalPath;
        this.isInGallery = isInGallery;
        this.createdDate = createdDate;
    }

    public UserMedia(String name, String description, int userId,
                     double latitude, double longitude,
                     String mediaExternalPath, int isInGallery, long createdDate) {
        this.id = -1;
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mediaExternalPath = mediaExternalPath;
        this.isInGallery = isInGallery;
        this.createdDate = createdDate;
    }

    public int getId() {
        return id;
    }

    public UserMedia setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserMedia setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public UserMedia setDescription(String description) {
        this.description = description;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public UserMedia setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public UserMedia setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public UserMedia setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public String getMediaExternalPath() {
        return mediaExternalPath;
    }

    public UserMedia setMediaExternalPath(String mediaExternalPath) {
        this.mediaExternalPath = mediaExternalPath;
        return this;
    }

    public int isInGallery() {
        return isInGallery;
    }

    public UserMedia setIsInGallery(int isInGallery) {
        this.isInGallery = isInGallery;
        return this;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public UserMedia setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    @Override
    public String toString() {
        return "UserMedia{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", userId=" + userId +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", mediaExternalPath='" + mediaExternalPath + '\'' +
                ", isInGallery=" + isInGallery +
                ", createdDate=" + createdDate +
                '}';
    }
}

package com.polant.touristapp.model;

/**
 * Created by Антон on 08.01.2016.
 */
public class MarkRecord {

    private int id;
    private int mediaId;
    private int markId;

    public MarkRecord(int id, int mediaId, int markId) {
        this.id = id;
        this.mediaId = mediaId;
        this.markId = markId;
    }

    public MarkRecord(int mediaId, int markId) {
        this.mediaId = mediaId;
        this.markId = markId;
    }

    public int getId() {
        return id;
    }

    public MarkRecord setId(int id) {
        this.id = id;
        return this;
    }

    public int getMediaId() {
        return mediaId;
    }

    public MarkRecord setMediaId(int mediaId) {
        this.mediaId = mediaId;
        return this;
    }

    public int getMarkId() {
        return markId;
    }

    public MarkRecord setMarkId(int markId) {
        this.markId = markId;
        return this;
    }
}

package com.polant.touristapp.model.recycler;

import com.polant.touristapp.model.Mark;
import com.polant.touristapp.model.UserMedia;

/**
 * Created by Антон on 29.01.2016.
 */
public class RecyclerItem {

    private int id;
    private Mark mark;
    private UserMedia media;

    public RecyclerItem(int id, Mark mark) {
        this.id = id;
        this.mark = mark;
    }

    public RecyclerItem(int id, UserMedia media) {
        this.id = id;
        this.media = media;
    }

    public boolean isMark(){
        return mark != null;
    }

    public boolean isUserMedia(){
        return media != null;
    }

    public int getId() {
        return id;
    }

    public Mark getMark() {
        return mark;
    }

    public UserMedia getMedia() {
        return media;
    }
}

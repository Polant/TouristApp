package com.polant.touristapp.model.search;

import com.polant.touristapp.model.database.Mark;
import com.polant.touristapp.model.database.UserMedia;

/**
 * Created by Антон on 29.01.2016.
 */
public class SearchComplexItem {

    private int id;
    private Mark mark;
    private UserMedia media;

    public SearchComplexItem(int id, Mark mark) {
        this.id = id;
        this.mark = mark;
    }

    public SearchComplexItem(int id, UserMedia media) {
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

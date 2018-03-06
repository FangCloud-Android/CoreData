package com.wanpg.core.test.model;

import com.coredata.annotation.Entity;
import com.coredata.annotation.PrimaryKey;
import com.coredata.core.PropertyConverter;

/**
 * Created by wangjinpeng on 2017/3/21.
 */
@Entity(tableName = "user")
public class Author {

    @PrimaryKey
    private int id;

    private String name;

    boolean isDirector;

    public Author() {
    }

    public Author(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static class AuthorTagConveter implements PropertyConverter<Tag,String> {

        @Override
        public String convertToProperty(Tag tag) {
            return null;
        }

        @Override
        public Tag convertToValue(String s) {
            return null;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDirector() {
        return isDirector;
    }

    public void setDirector(boolean director) {
        isDirector = director;
    }
}

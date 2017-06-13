package com.wanpg.core.test.model;

import com.coredata.annotation.Entity;
import com.coredata.annotation.PrimaryKey;
import com.coredata.core.PropertyConverter;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by wangjinpeng on 2017/3/21.
 */
@Entity(tableName = "user")
@Setter
@Getter
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
}

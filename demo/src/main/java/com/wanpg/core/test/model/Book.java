package com.wanpg.core.test.model;

import com.coredata.annotation.ColumnInfo;
import com.coredata.annotation.Convert;
import com.coredata.annotation.Embedded;
import com.coredata.annotation.Entity;
import com.coredata.annotation.PrimaryKey;
import com.coredata.annotation.Relation;
import com.coredata.core.PropertyConverter;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by wangjinpeng on 2017/6/1.
 */
@Entity
@Setter
@Getter
public class Book {

    @PrimaryKey
    public long id;

    public String name;

    @Convert(converter = "com.wanpg.core.test.model.Book.TagListConverter", dbType = "java.lang.String")
    public List<Tag> tags;

    @Relation
    @ColumnInfo(name = "author_id")
    public Author author;

    /**
     * 内嵌字段，适用于简单字段
     */
    @Embedded
    public Desc desc;

    public Author getAuthor() {
        return author;
    }

    public static class TagListConverter implements PropertyConverter<List<Tag>, String> {

        @Override
        public String convertToProperty(List<Tag> tags) {
            return "";
        }

        @Override
        public List<Tag> convertToValue(String s) {
            return null;
        }
    }
}

package com.wanpg.core.test.model;

import com.coredata.annotation.ColumnInfo;
import com.coredata.annotation.Convert;
import com.coredata.annotation.Embedded;
import com.coredata.annotation.Entity;
import com.coredata.annotation.PrimaryKey;
import com.coredata.annotation.Relation;
import com.coredata.core.converter.SerializableConverter;
import com.coredata.core.converter.SerializableListConverter;
import com.coredata.core.converter.StringArrayConverter;
import com.coredata.core.converter.StringListConverter;

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

    @Setter
    @Getter
    private String name;

    @Convert(converter = SerializableListConverter.class, dbType = String.class)
    public List<Tag> tags;

    @Convert(converter = StringArrayConverter.class, dbType = String.class)
    public String[] permissions;

    @Relation
    @ColumnInfo(name = "author_id")
    public Author author;

    @Convert(converter = SerializableConverter.class, dbType = String.class)
    public Address address;

    /**
     * 内嵌字段，适用于简单字段
     */
    @Embedded
    public Desc desc;

    @Convert(converter = StringListConverter.class, dbType = String.class)
    public List<String> testList;

    String tag1;
    String tag2;
    String tag3;

    public Author getAuthor() {
        return author;
    }
}

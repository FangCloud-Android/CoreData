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

/**
 * Created by wangjinpeng on 2017/6/1.
 */
@Entity
public class Book {

    @PrimaryKey
    public long id;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Desc getDesc() {
        return desc;
    }

    public void setDesc(Desc desc) {
        this.desc = desc;
    }

    public List<String> getTestList() {
        return testList;
    }

    public void setTestList(List<String> testList) {
        this.testList = testList;
    }

    public String getTag1() {
        return tag1;
    }

    public void setTag1(String tag1) {
        this.tag1 = tag1;
    }

    public String getTag2() {
        return tag2;
    }

    public void setTag2(String tag2) {
        this.tag2 = tag2;
    }

    public String getTag3() {
        return tag3;
    }

    public void setTag3(String tag3) {
        this.tag3 = tag3;
    }
}

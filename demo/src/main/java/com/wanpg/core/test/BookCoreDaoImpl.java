package com.wanpg.core.test;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.coredata.core.CoreDao;
import com.coredata.core.CoreData;
import com.coredata.core.Property;
import com.coredata.core.converter.JSONConverter;
import com.coredata.core.converter.SerializableConverter;
import com.coredata.core.converter.SerializableListConverter;
import com.coredata.core.converter.StringArrayConverter;
import com.wanpg.core.test.model.Address;
import com.wanpg.core.test.model.Address;
import com.wanpg.core.test.model.Author;
import com.wanpg.core.test.model.Book;
import com.wanpg.core.test.model.Desc;
import com.wanpg.core.test.model.Tag;

import java.lang.Integer;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public final class BookCoreDaoImpl extends CoreDao<Book> {
    private final SerializableListConverter<Tag> __tags_SerializableListConverter = new SerializableListConverter<Tag>();

    private final StringArrayConverter __permissions_StringArrayConverter = new StringArrayConverter();

    private final SerializableConverter<Address> __address_SerializableConverter = new SerializableConverter<Address>();

    private final JSONConverter<List<String>> __testList_JSONConverter = new JSONConverter<List<String>>();

    private CoreDao<Author> __AuthorCoreDao;

    protected void onCreate(SQLiteOpenHelper dbHelper) {
        super.onCreate(dbHelper);
        __AuthorCoreDao = CoreData.defaultInstance().dao(Author.class);
    }

    protected String getTableName() {
        return "Book";
    }

    protected String getPrimaryKeyName() {
        return "id";
    }

    protected List<Property> getTableProperties() {
        ArrayList<Property> list = new ArrayList<Property>();
        list.add(new Property("id", long.class, true));
        list.add(new Property("name", String.class, false));
        list.add(new Property("tags", String.class, false));
        list.add(new Property("permissions", String.class, false));
        list.add(new Property("author_id", int.class, false));
        list.add(new Property("address", String.class, false));
        list.add(new Property("desc_content", String.class, false));
        list.add(new Property("desc_email", String.class, false));
        list.add(new Property("testList", String.class, false));
        return list;
    }

    protected String getCreateTableSql() {
        return "CREATE TABLE IF NOT EXISTS 'Book' ('id' BIGINT PRIMARY KEY,'name' TEXT,'tags' TEXT,'permissions' TEXT,'author_id' INT,'address' TEXT,'desc_content' TEXT,'desc_email' TEXT,'testList' TEXT);";
    }

    protected String getInsertSql() {
        return "INSERT OR REPLACE INTO `Book`(`id`,`name`,`tags`,`permissions`,`author_id`,`address`,`desc_content`,`desc_email`,`testList`) VALUES (?,?,?,?,?,?,?,?,?)";
    }

    protected void bindStatement(SQLiteStatement statement, Book entity) {
        statement.bindLong(1, entity.id);
        if(entity != null && entity.name != null){
            statement.bindString(2, entity.name);
        } else {
            statement.bindNull(2);
        }
        String __temp_3 = __tags_SerializableListConverter.convertToProperty(entity.tags);
        if(__temp_3 != null){
            statement.bindString(3, __temp_3);
        } else {
            statement.bindNull(3);
        }
        String __temp_4 = __permissions_StringArrayConverter.convertToProperty(entity.permissions);
        if(__temp_4 != null){
            statement.bindString(4, __temp_4);
        } else {
            statement.bindNull(4);
        }
        int __primaryKey_5 = (entity.author == null ? 0 : entity.author.getId());
        statement.bindLong(5, __primaryKey_5);
        String __temp_6 = __address_SerializableConverter.convertToProperty(entity.address);
        if(__temp_6 != null){
            statement.bindString(6, __temp_6);
        } else {
            statement.bindNull(6);
        }
        if(entity.desc != null && entity.desc.content != null){
            statement.bindString(7, entity.desc.content);
        } else {
            statement.bindNull(7);
        }
        if(entity.desc != null && entity.desc.email != null){
            statement.bindString(8, entity.desc.email);
        } else {
            statement.bindNull(8);
        }
        String __temp_9 = __testList_JSONConverter.convertToProperty(entity.testList);
        if(__temp_9 != null){
            statement.bindString(9, __temp_9);
        } else {
            statement.bindNull(9);
        }
    }

    protected boolean replaceInternal(Collection<Book> collection, SQLiteDatabase db) {
        ArrayList<Book> BookList = new ArrayList<Book>();
        ArrayList<Author> AuthorList = new ArrayList<Author>();
        for (Book item : collection) {
            BookList.add(item);
            if(item.author != null){
                AuthorList.add(item.author);
            }
        }
        executeInsert(BookList, db);
        __AuthorCoreDao.replace(AuthorList, db);
        return true;
    }

    protected List<Book> bindCursor(Cursor cursor) {
        int cursorIndexOfid = cursor.getColumnIndexOrThrow("id");
        int cursorIndexOfname = cursor.getColumnIndexOrThrow("name");
        int cursorIndexOftags = cursor.getColumnIndexOrThrow("tags");
        int cursorIndexOfpermissions = cursor.getColumnIndexOrThrow("permissions");
        int cursorIndexOfauthor_id = cursor.getColumnIndexOrThrow("author_id");
        int cursorIndexOfaddress = cursor.getColumnIndexOrThrow("address");
        int cursorIndexOfdesc_content = cursor.getColumnIndexOrThrow("desc_content");
        int cursorIndexOfdesc_email = cursor.getColumnIndexOrThrow("desc_email");
        int cursorIndexOftestList = cursor.getColumnIndexOrThrow("testList");
        ArrayList<Book> list = new ArrayList<Book>();
        HashMap<Integer, Book> __author_idMap = new HashMap<Integer, Book>();
        while (cursor.moveToNext()) {
            Book entity = new Book();
            entity.id = cursor.getLong(cursorIndexOfid);
            entity.name = cursor.getString(cursorIndexOfname);
            if(!cursor.isNull(cursorIndexOftags)){
                entity.tags = __tags_SerializableListConverter.convertToValue(cursor.getString(cursorIndexOftags));
            }if(!cursor.isNull(cursorIndexOfpermissions)){
                entity.permissions = __permissions_StringArrayConverter.convertToValue(cursor.getString(cursorIndexOfpermissions));
            }__author_idMap.put(cursor.getInt(cursorIndexOfauthor_id), entity);
            if(!cursor.isNull(cursorIndexOfaddress)){
                entity.address = __address_SerializableConverter.convertToValue(cursor.getString(cursorIndexOfaddress));
            }Desc __descTemp = new Desc();
            __descTemp.content = cursor.getString(cursorIndexOfdesc_content);
            __descTemp.email = cursor.getString(cursorIndexOfdesc_email);
            entity.desc = __descTemp;
            if(!cursor.isNull(cursorIndexOftestList)){
                entity.testList = __testList_JSONConverter.convertToValue(cursor.getString(cursorIndexOftestList));
            }list.add(entity);
        }
        List<Author> __AuthorList = __AuthorCoreDao.queryByKeys(__author_idMap.keySet().toArray(new Integer[]{}));
        for(Author item : __AuthorList){
            Book entity = __author_idMap.get(item.getId());
            if(entity != null){
                entity.author = item;
            }
        }
        return list;
    }
}
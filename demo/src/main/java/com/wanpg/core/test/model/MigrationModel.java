package com.wanpg.core.test.model;

import com.coredata.annotation.ColumnInfo;
import com.coredata.annotation.Entity;
import com.coredata.annotation.PrimaryKey;

//// 14
//@Entity(tableName = "migration_model")
//public class MigrationModel {
//
//    @PrimaryKey
//    public int id;
//    public String name;
//    public String address;
//
//    public MigrationModel() {
//    }
//
//    public MigrationModel(int id, String name, String address) {
//        this.id = id;
//        this.name = name;
//        this.address = address;
//    }
//
//    public static MigrationModel create(int id, String name, String address){
//        return new MigrationModel(id, name, address);
//    }
//}

//// 15
//@Entity
//public class MigrationModel {
//
//    @PrimaryKey
//    public int id;
//    public String name;
//    public String address;
//
//    public MigrationModel() {
//    }
//
//    public MigrationModel(int id, String name, String address) {
//        this.id = id;
//        this.name = name;
//        this.address = address;
//    }
//
//    public static MigrationModel create(int id, String name, String address) {
//        return new MigrationModel(id, name, address);
//    }
//}

//// 16
//@Entity
//public class MigrationModel {
//
//    @PrimaryKey
//    public String id;
//    @ColumnInfo(name = "new_name")
//    public String name;
//    public String address;
//
//    public MigrationModel() {
//    }
//
//    public MigrationModel(String id, String name, String address) {
//        this.id = id;
//        this.name = name;
//        this.address = address;
//    }
//
//    public static MigrationModel create(int id, String name, String address) {
//        return new MigrationModel(String.valueOf(id), name, address);
//    }
//}

// 17
@Entity
public class MigrationModel {

    @PrimaryKey
    public String id;
    @ColumnInfo(name = "new_name")
    public String name;
    //    public String address;
    public String phone;

    public MigrationModel() {
    }

    public MigrationModel(String id, String name, String address) {
        this.id = id;
        this.name = name;
//        this.address = address;
        this.phone = address;
    }

    public static MigrationModel create(int id, String name, String address) {
        return new MigrationModel(String.valueOf(id), name, address);
    }
}

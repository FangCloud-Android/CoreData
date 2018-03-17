package com.coredata.compiler;

import com.coredata.annotation.Convert;
import com.coredata.annotation.Embedded;
import com.coredata.annotation.Entity;
import com.coredata.compiler.utils.TextUtils;
import com.coredata.compiler.utils.Utils;
import com.coredata.db.Property;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * Created by wangjinpeng on 2018/3/16.
 */

public class EntityDetail {

    public static EntityDetail parse(ProcessingEnvironment env, TypeElement element) {
        EntityDetail entityDetail = new EntityDetail();
        entityDetail.entityElement = element;
        // 解析主键
        entityDetail.parsePrimaryKeyAndElements(env);
        return entityDetail;
    }


    private TypeElement entityElement;
    private Element primaryKey;
    private List<Element> dbElements;

    public TypeElement getEntityElement() {
        return entityElement;
    }

    /**
     * 获取实体名称
     *
     * @return
     */
    public String getEntityName() {
        return entityElement.getSimpleName().toString();
    }

    /**
     * 获取实体所在的包名
     *
     * @param env
     * @return
     */
    public String getEntityPackageName(ProcessingEnvironment env) {
        return env.getElementUtils().getPackageOf(entityElement).getQualifiedName().toString();
    }

    /**
     * 获取数据库表名
     *
     * @return
     */
    public String getTableName() {
        Entity entity = entityElement.getAnnotation(Entity.class);
        String tableName = entity.tableName();
        if (TextUtils.isEmpty(tableName)) {
            tableName = getEntityName();
        }
        return tableName;
    }

    public Element getPrimaryKey() {
        return primaryKey;
    }


    public List<Element> getDbElements() {
        return dbElements;
    }

    public List<Element> getRelationElements() {
        return Utils.getRelationElements(getDbElements());
    }

    public List<Element> getConvertElements(ProcessingEnvironment env) {
        return getConvertElements(env, getDbElements());
    }

    public List<Property> getProperties(ProcessingEnvironment env) {
        return Utils.getProperties(env, getDbElements(), getPrimaryKey());
    }

    private List<Element> getConvertElements(ProcessingEnvironment env, List<Element> elements) {
        List<Element> elementList = new ArrayList<>();
        for (Element element : elements) {
            if (element.getAnnotation(Convert.class) != null) {
                elementList.add(element);
            } else {
                if (element.getAnnotation(Embedded.class) != null) {
                    Element elementEmbedded = env.getTypeUtils().asElement(element.asType());
                    List<Element> elementEmbeddedList = new ArrayList<>();
                    Utils.fillElementsForDbAndReturnPrimaryKey(env, elementEmbeddedList, (TypeElement) elementEmbedded);
                    elementList.addAll(getConvertElements(env, elementEmbeddedList));
                }
            }
        }
        return elementList;
    }

    private void parsePrimaryKeyAndElements(ProcessingEnvironment env) {
        dbElements = new ArrayList<>();
        primaryKey = Utils.fillElementsForDbAndReturnPrimaryKey(env, dbElements, entityElement);
    }
}

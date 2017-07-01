package com.coredata.compiler.utils;

import com.coredata.annotation.ColumnInfo;
import com.coredata.annotation.Convert;
import com.coredata.annotation.Embedded;
import com.coredata.annotation.Entity;
import com.coredata.annotation.Ignore;
import com.coredata.annotation.PrimaryKey;
import com.coredata.annotation.Relation;
import com.coredata.db.Property;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created by wangjinpeng on 2017/6/7.
 */

public class Utils {

    public static String getInsertSql(String tableName, List<Property> propertyList) {
        String insertFormat = "INSERT OR REPLACE INTO `%s`(%s) VALUES (%s)";
        StringBuilder fieldBuilder = new StringBuilder();
        StringBuilder askBuilder = new StringBuilder();

        boolean isFirst = true;
        for (Property property : propertyList) {
            if (!isFirst) {
                fieldBuilder.append(",");
                askBuilder.append(",");
            }
            isFirst = false;
            fieldBuilder.append("`")
                    .append(property.name)
                    .append("`");
            askBuilder.append("?");
        }
        return String.format(insertFormat, tableName, fieldBuilder.toString(), askBuilder.toString());
    }

    private static List<Class<?>> baseTypeList = new ArrayList<>();

    static {

        baseTypeList.add(byte.class);
        baseTypeList.add(Byte.class);

        baseTypeList.add(short.class);
        baseTypeList.add(Short.class);

        baseTypeList.add(int.class);
        baseTypeList.add(Integer.class);

        baseTypeList.add(long.class);
        baseTypeList.add(Long.class);

        baseTypeList.add(double.class);
        baseTypeList.add(Double.class);

        baseTypeList.add(float.class);
        baseTypeList.add(Float.class);

        baseTypeList.add(boolean.class);
        baseTypeList.add(Boolean.class);

        baseTypeList.add(char.class);
        baseTypeList.add(Character.class);

        baseTypeList.add(String.class);
    }

    /**
     * 判断是否是基本类型
     *
     * @param type
     * @return
     */
    public static boolean isBaseType(Class<?> type) {
        return baseTypeList.contains(type);
    }

    public static boolean isBaseType(TypeName typeName) {
        if (typeName.isPrimitive() || typeName.isBoxedPrimitive()) {
            return true;
        }
        String typeClassName = typeName.toString();
        for (Class<?> aClass : baseTypeList) {
            if (aClass.getName().equals(typeClassName)) {
                return true;
            }
        }
        return false;
    }

    public static TypeName getTypeNameByType(Class aClass) {
        if (aClass == boolean.class) {
            return TypeName.BOOLEAN;
        } else if (aClass == byte.class) {
            return TypeName.BYTE;
        } else if (aClass == short.class) {
            return TypeName.SHORT;
        } else if (aClass == int.class) {
            return TypeName.INT;
        } else if (aClass == long.class) {
            return TypeName.LONG;
        } else if (aClass == char.class) {
            return TypeName.CHAR;
        } else if (aClass == float.class) {
            return TypeName.FLOAT;
        } else if (aClass == double.class) {
            return TypeName.DOUBLE;
        }
        return ClassName.get(aClass);
    }

    public static Class getTypeByTypeName(TypeName typeName) {
        try {
            TypeName unbox = typeName.unbox();
            if (unbox == TypeName.BOOLEAN) {
                return boolean.class;
            } else if (unbox == TypeName.BYTE) {
                return byte.class;
            } else if (unbox == TypeName.SHORT) {
                return short.class;
            } else if (unbox == TypeName.INT) {
                return int.class;
            } else if (unbox == TypeName.LONG) {
                return long.class;
            } else if (unbox == TypeName.CHAR) {
                return char.class;
            } else if (unbox == TypeName.FLOAT) {
                return float.class;
            } else if (unbox == TypeName.DOUBLE) {
                return double.class;
            }
        } catch (Exception ignored) {
        }
        return String.class;
    }

    /**
     * @param elements
     * @param element
     * @return
     */
    public static List<Element> getElementsForDb(Elements elements, TypeElement element) {
        List<Element> listCreatePro = new ArrayList<>();
        List<? extends Element> enclosedElements = element.getEnclosedElements();
        boolean hasPrimaryKey = false;
        for (Element member : enclosedElements) {
            if (member.getKind().isField()) {
                if (isDbElement(member.getModifiers())) {
                    if (member.getAnnotation(Ignore.class) == null) {
                        if (member.getAnnotation(PrimaryKey.class) != null) {
                            if (hasPrimaryKey) {
                                throw new RuntimeException(element.getSimpleName() + "同时拥有两个主键，目前不支持多主键");
                            }
                            hasPrimaryKey = true;
                        }
                        listCreatePro.add(member);
                        System.out.println(member.getSimpleName() + "----" + member.getKind());
                    }
                }
            }
        }
        if (!element.getSuperclass().toString().equals(Object.class.getCanonicalName())) {
            listCreatePro.addAll(getElementsForDb(elements, elements.getTypeElement(element.getSuperclass().toString())));
        }
        return listCreatePro;
    }

    /**
     * 静态参数，final参数和transient参数都不是数据库类型
     *
     * @param modifiers
     * @return
     */
    private static boolean isDbElement(Set<Modifier> modifiers) {
        for (Modifier modifier : modifiers) {
            if (Modifier.STATIC.equals(modifier)) {
                return false;
            }
            if (Modifier.FINAL.equals(modifier)) {
                return false;
            }
            if (Modifier.TRANSIENT.equals(modifier)) {
                return false;
            }
        }
        return true;
    }

    public static String converterName(Element element, ClassName classConverter) {
        return "__" + element.getSimpleName() + "_" + classConverter.simpleName();
    }

    public static String relationDaoName(ClassName classRelation) {
        return "__" + classRelation.simpleName() + "CoreDao";
    }

    public static Element primaryKeyElement(List<Element> elements) {
        for (Element element : elements) {
            if (element.getAnnotation(PrimaryKey.class) != null) {
                return element;
            }
        }
        return null;
    }

    public static List<Element> getRelationElements(List<Element> elements) {
        List<Element> elementList = new ArrayList<>();
        for (Element element : elements) {
            if (element.getAnnotation(Relation.class) != null) {
                elementList.add(element);
            }
            // FIXME: 2017/6/7 考虑是否支持内嵌类型的关联
        }
        return elementList;
    }

    public static String methodGet(Element element, String prefix) {
        TypeName typeName = ClassName.get(element.asType());
        String elementName = element.getSimpleName().toString();
        Set<Modifier> modifiers = element.getModifiers();
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(prefix)) {
            sb.append(prefix);
            sb.append(".");
        }
        if (modifiers.contains(Modifier.PUBLIC)) {
            sb.append(elementName);
        } else {
            if (typeName == TypeName.BOOLEAN) {
                if (elementName.startsWith("is")) {
                    return sb.append(element).append("()").toString();
                } else {
                    sb.append("is");
                }
            } else {
                sb.append("get");
            }
            sb.append(elementName.substring(0, 1).toUpperCase())
                    .append(elementName.substring(1))
                    .append("()");
        }
        return sb.toString();
    }

    public static String methodSetFormat(Element element, String prefix) {
        TypeName typeName = ClassName.get(element.asType());
        String elementName = element.getSimpleName().toString();
        Set<Modifier> modifiers = element.getModifiers();
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(prefix)) {
            sb.append(prefix)
                    .append(".");
        }
        if (modifiers.contains(Modifier.PUBLIC)) {
            sb.append(elementName)
                    .append(" = $N");
        } else {
            sb.append("set");
            if (typeName == TypeName.BOOLEAN && elementName.startsWith("is")) {
                sb.append(elementName.substring(2));
            } else {
                sb.append(elementName.substring(0, 1).toUpperCase())
                        .append(elementName.substring(1));
            }
            sb.append("($N)");
        }
        return sb.toString();
    }

    public static List<Property> getProperties(Elements elementUtils, Types typeUtils, List<Element> elements) {
        List<Property> propertyList = new ArrayList<>();
        boolean hasPrimaryKey = false;
        for (Element element : elements) {
            boolean isPrimaryKey = false;
            if (!hasPrimaryKey) {
                PrimaryKey primaryKey = element.getAnnotation(PrimaryKey.class);
                if (primaryKey != null) {
                    hasPrimaryKey = true;
                    isPrimaryKey = true;
                }
            }

            String name = null;
            ColumnInfo columnInfo = element.getAnnotation(ColumnInfo.class);
            if (columnInfo != null) {
                name = columnInfo.name();
            }
            if (TextUtils.isEmpty(name)) {
                name = element.getSimpleName().toString();
            }

            TypeName dbBaseType;
            TypeMirror typeMirror = element.asType();
            TypeName typeNameElement = ClassName.get(typeMirror);
            if (Utils.isBaseType(typeNameElement)) {
                dbBaseType = typeNameElement;
            } else {
                TypeElement elementFieldType = (TypeElement) typeUtils.asElement(typeMirror);
                Relation annotationRelation = element.getAnnotation(Relation.class);
                if (annotationRelation != null) {
                    // 关联数据
                    Entity relationEntity = elementFieldType.getAnnotation(Entity.class);
                    if (relationEntity == null) {
                        throw new IllegalStateException("@Relation 添加的属性必须是 @Entity 的类");
                    }
                    Element primaryKeyElement = Utils.primaryKeyElement(Utils.getElementsForDb(elementUtils, elementFieldType));
                    if (primaryKeyElement == null) {
                        throw new IllegalStateException("@Relation 属性必须有主键");
                    }
                    dbBaseType = ClassName.get(primaryKeyElement.asType());
                } else {
                    Embedded embedded = element.getAnnotation(Embedded.class);
                    if (embedded != null) {
                        // 内嵌数据, 循环解析内嵌结构的字段
                        propertyList.addAll(getProperties(elementUtils, typeUtils, Utils.getElementsForDb(elementUtils, (TypeElement) typeUtils.asElement(typeMirror))));
                        continue;
                    } else {
                        Convert convert = element.getAnnotation(Convert.class);
                        if (convert != null) {
                            TypeName classConvertDb = Utils.getConvertDbType(convert);
                            if (Utils.isBaseType(classConvertDb)) {
                                dbBaseType = classConvertDb;
                            } else {
                                throw new IllegalStateException(element.getSimpleName()
                                        + "converter dbtype is not a base type");
                            }
                        } else {
                            throw new IllegalStateException(element.getSimpleName()
                                    + " is a complex structure field，" +
                                    "must indicate it as @Relation or @Embedded, " +
                                    "or supply a @Convert for it");
                        }
                    }
                }
            }
            propertyList.add(new Property(name, Utils.getTypeByTypeName(dbBaseType), isPrimaryKey));
        }
        return propertyList;
    }

    public static String getColumnName(Element element) {
        ColumnInfo columnInfo = element.getAnnotation(ColumnInfo.class);
        if (columnInfo != null) {
            String name = columnInfo.name();
            if (!TextUtils.isEmpty(name)) {
                return name;
            }
        }
        return element.getSimpleName().toString();
    }

    public static TypeName getDbType(Element element) {
        Convert convert = element.getAnnotation(Convert.class);
        if (convert != null) {
            return getConvertDbType(convert);
        }
        return ClassName.get(element.asType());
    }

    public static TypeName getConvertDbType(Convert convert) {
        try {
            return ClassName.get(convert.dbType());
        } catch (MirroredTypeException e) {
            return ClassName.get(e.getTypeMirror());
        }
    }

    public static TypeName getConverterType(Convert convert) {
        try {
            return ClassName.get(convert.converter());
        } catch (MirroredTypeException e) {
            return ClassName.get(e.getTypeMirror());
        }
    }
}

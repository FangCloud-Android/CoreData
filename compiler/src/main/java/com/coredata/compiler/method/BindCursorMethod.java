package com.coredata.compiler.method;

import com.coredata.annotation.Convert;
import com.coredata.annotation.Embedded;
import com.coredata.annotation.Relation;
import com.coredata.compiler.utils.Utils;
import com.coredata.db.Property;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import static com.coredata.compiler.EntityProcessor.classCursor;

/**
 * Created by wangjinpeng on 2017/6/7.
 */

public class BindCursorMethod extends BaseMethod {
    public BindCursorMethod(ProcessingEnvironment processingEnv, TypeElement typeElement) {
        super(processingEnv, typeElement);
    }

    @Override
    public MethodSpec build() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("bindCursor")
                .addModifiers(Modifier.PROTECTED)
                .returns(ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(typeElement.asType())))
                .addParameter(classCursor, "cursor");
        bind(builder);
        return builder.build();
    }

    private void bind(MethodSpec.Builder builder) {
//        final int cursorIndexOfId = cursor.getColumnIndexOrThrow("id");
//        final int cursorIndexOfName = cursor.getColumnIndexOrThrow("name");
//        final int cursorIndexOfTags = cursor.getColumnIndexOrThrow("tags");
//        final int cursorIndexOfAuthor = cursor.getColumnIndexOrThrow("author_id");
//        final int cursorIndexOfContent = cursor.getColumnIndexOrThrow("desc_content");
//        final int cursorIndexOfEmail = cursor.getColumnIndexOrThrow("desc_email");
//        List<Book> bookList = new ArrayList<>();
//        Map<Integer, Book> authorIdWithBookMap = new HashMap<>();
//        while (cursor.moveToNext()) {
//            Book book = new Book();
//            book.id = cursor.getLong(cursorIndexOfId);
//            book.name = cursor.getString(cursorIndexOfName);
//            book.tags = __TagListConverter.convertToValue(cursor.getString(cursorIndexOfTags));
//            int authorId = cursor.getInt(cursorIndexOfAuthor);
//            book.desc = new Desc();
//            book.desc.content = cursor.getString(cursorIndexOfContent);
//            book.desc.email = cursor.getString(cursorIndexOfEmail);
//            bookList.add(book);
//            authorIdWithBookMap.put(authorId, book);
//        }
//        List<Author> authorList = __authorCoreDao.queryByKeys(authorIdWithBookMap.keySet().toArray(new Integer[]{}));
//        for (Author author : authorList) {
//            Book book = authorIdWithBookMap.get(author.getId());
//            if (book != null) {
//                book.author = author;
//            }
//        }
//        return bookList;
        TypeName typeNameEntity = ClassName.get(typeElement.asType());
        Element typeEntity = processingEnv.getTypeUtils().asElement(typeElement.asType());
        ParameterizedTypeName typeListEntity = ParameterizedTypeName.get(ClassName.get(ArrayList.class), typeNameEntity);
        List<Element> elementsForDb = Utils.getElementsForDb(processingEnv.getElementUtils(), typeElement);
        List<Property> properties = Utils.getProperties(processingEnv.getElementUtils(), processingEnv.getTypeUtils(), elementsForDb);
        List<Element> relationElements = Utils.getRelationElements(elementsForDb);
        for (Property property : properties) {
            builder.addStatement("int $N = cursor.getColumnIndexOrThrow($S)", "cursorIndexOf" + property.name, property.name);
        }
        // 创建自己的列表
        builder.addStatement("$T list = new $T()", typeListEntity, typeListEntity);

        // 创建多个关联数据的hashMap，主键类型为key
        for (Element relationElement : relationElements) {
            TypeElement typeRelation = (TypeElement) processingEnv.getTypeUtils().asElement(relationElement.asType());
            Element primaryKeyElement = Utils.primaryKeyElement(Utils.getElementsForDb(processingEnv.getElementUtils(), typeRelation));
            if (primaryKeyElement != null) {
                TypeMirror typeMirror = primaryKeyElement.asType();
                ParameterizedTypeName hashMapType = ParameterizedTypeName.get(ClassName.get(HashMap.class), ClassName.get(typeMirror).box(), typeNameEntity);
                builder.addStatement("$T __$NMap = new $T()", hashMapType, Utils.getColumnName(relationElement), hashMapType);
            }
        }

        builder.addCode("while (cursor.moveToNext()) {\n  ");
        // 创建一个对象
        String itemName = "entity";
        builder.addStatement("$T $N = new $T()", typeNameEntity, itemName, typeNameEntity);
        for (Element element : elementsForDb) {
            bindCursorToField(builder, element, itemName);
        }
        builder.addStatement("list.add(entity)");
        //将对象放入相应关联对象对应的map

        builder.addCode("}\n");

        // 循环关联对象，赋值给主对象
        for (Element relationElement : relationElements) {
            ClassName classNameRelation = ClassName.bestGuess(relationElement.asType().toString());
            TypeElement typeRelationElement = (TypeElement) processingEnv.getTypeUtils().asElement(relationElement.asType());
            Element primaryKeyElement = Utils.primaryKeyElement(Utils.getElementsForDb(processingEnv.getElementUtils(), typeRelationElement));
            if (primaryKeyElement != null) {
                //            List<Author> authorList = __authorCoreDao.queryByKeys(authorIdWithBookMap.keySet().toArray(new Integer[]{}));
                TypeName primaryTypeName = ClassName.get(primaryKeyElement.asType());
                ParameterizedTypeName listRelationType = ParameterizedTypeName.get(ClassName.get(List.class), classNameRelation);
                String listName = String.format("__%sList", typeRelationElement.getSimpleName());
                String mapName = String.format("__%sMap", Utils.getColumnName(relationElement));
                builder.addStatement("$T $N = $N.queryByKeys($N.keySet().toArray(new $T[]{}))",
                        listRelationType,
                        listName,
                        Utils.relationDaoName(classNameRelation),
                        mapName,
                        primaryTypeName.box());
                builder.addCode("for($T item : $N){\n", classNameRelation, listName);
                builder.addStatement("  $T entity = $N.get($N)", typeNameEntity, mapName, Utils.methodGet(primaryKeyElement, "item"));
                builder.addCode("  if(entity != null){\n");
                builder.addStatement(Utils.methodSetFormat(relationElement, "entity"), "item");
                builder.addCode("  }\n");
                builder.addCode("}\n");
            }
        }
        builder.addStatement("return list");
    }

    private void bindCursorToField(MethodSpec.Builder builder, Element element, String prefix) {
        // 基本类型
        // String
        // 内嵌类型
        // 关联类型
        // 转换类型
        ClassName stringClassName = ClassName.get(String.class);
        TypeName typeName = ClassName.get(element.asType());
        Element typeElement = processingEnv.getTypeUtils().asElement(element.asType());
        if (typeName.isPrimitive() || typeName.isBoxedPrimitive() || typeName.equals(stringClassName)) {
            // 基础类型 // 封装基础类型 // 字符串类型
            builder.addStatement(Utils.methodSetFormat(element, prefix), cursorGetMethod(element, Utils.getDbType(element)));
        } else {
            Embedded embedded = element.getAnnotation(Embedded.class);
            if (embedded != null) {
                // 内嵌类型循环添加
                // 创建临时变量
                String embeddedTempName = "__" + element.getSimpleName() + "Temp";
                builder.addStatement("$T $N = new $T()", typeName, embeddedTempName, typeName);
                // 赋值
                List<Element> elementsForDb = Utils.getElementsForDb(processingEnv.getElementUtils(), (TypeElement) typeElement);
                for (Element eleEmbeddedChild : elementsForDb) {
                    bindCursorToField(builder, eleEmbeddedChild, embeddedTempName);
                }
                builder.addStatement(Utils.methodSetFormat(element, prefix), embeddedTempName);
                return;
            }
            Convert convert = element.getAnnotation(Convert.class);
            if (convert != null) {
                ClassName classConverter = ClassName.bestGuess(Utils.getConverterType(convert).toString());
                TypeName convertDbType = Utils.getConvertDbType(convert);
                if (!convertDbType.isPrimitive()) {
                    builder.addCode("if(!cursor.isNull(cursorIndexOf$N)){\n  ", Utils.getColumnName(element));
                }
                builder.addStatement(Utils.methodSetFormat(element, prefix), Utils.converterName(element, classConverter) + ".convertToValue(" + cursorGetMethod(element, Utils.getDbType(element)) + ")");
                if (!convertDbType.isPrimitive()) {
                    builder.addCode("}");
                }
                return;
            }
            Relation relation = element.getAnnotation(Relation.class);
            if (relation != null) {
                TypeElement typeRelation = (TypeElement) processingEnv.getTypeUtils().asElement(element.asType());
                Element primaryKeyElement = Utils.primaryKeyElement(Utils.getElementsForDb(processingEnv.getElementUtils(), typeRelation));
                if (primaryKeyElement != null) {
                    TypeName typePrimary = ClassName.get(primaryKeyElement.asType());
                    String columnName = Utils.getColumnName(element);
                    if (!typePrimary.isPrimitive()) {
                        builder.addCode("if(!cursor.isNull(cursorIndexOf$N)){\n  ", columnName);
                    }
                    builder.addStatement("__$NMap.put($N, entity)", columnName, cursorGetMethod(element, Utils.getDbType(primaryKeyElement)));
                    if (!typePrimary.isPrimitive()) {
                        builder.addCode("}");
                    }
                }
                return;
            }
        }
    }

    private String cursorGetMethod(Element element, TypeName dbType) {
//        TypeName typeName = Utils.getDbType(element);
        TypeName unbox = null;
        try {
            unbox = dbType.unbox();
        } catch (Exception ignored) {
        }
        String getMethod = null;
        if (unbox != null) {
            if (unbox == TypeName.BYTE) {
                getMethod = "getInt";
            } else if (unbox == TypeName.SHORT) {
                getMethod = "getShort";
            } else if (unbox == TypeName.INT) {
                getMethod = "getInt";
            } else if (unbox == TypeName.LONG) {
                getMethod = "getLong";
            } else if (unbox == TypeName.LONG) {
                getMethod = "getLong";
            } else if (unbox == TypeName.CHAR) {
                getMethod = "getInt";
            } else if (unbox == TypeName.FLOAT) {
                getMethod = "getFloat";
            } else if (unbox == TypeName.FLOAT) {
                getMethod = "getDouble";
            } else if (unbox == TypeName.BOOLEAN) {
                return String.format("cursor.getInt(cursorIndexOf%s) != 0 ? true : false", Utils.getColumnName(element));
            }
        } else {
            getMethod = "getString";
        }

        return String.format("cursor.%s(cursorIndexOf%s)", getMethod, Utils.getColumnName(element));
    }
}

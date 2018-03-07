package com.coredata.compiler.method;

import com.coredata.annotation.Convert;
import com.coredata.compiler.utils.Utils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

import static com.coredata.compiler.EntityProcessor.classCoreData;

/**
 * Created by wangjinpeng on 2017/6/26.
 */

public class CreateConvertStatement {

    private static final ClassName SERIALIZABLE_CLASS_NAME
            = ClassName.bestGuess("com.coredata.core.converter.SerializableConverter");
    private static final ClassName SERIALIZABLE_LIST_CLASS_NAME
            = ClassName.bestGuess("com.coredata.core.converter.SerializableListConverter");
    public static final ClassName stringClassName = ClassName.get(String.class);

    public static CodeBlock buildConvertStatic(List<Element> convertElements) {
        CodeBlock.Builder builder = CodeBlock.builder();
        if (convertElements == null) {
            return null;
        }
        for (Element convertElement : convertElements) {
            Convert convert = convertElement.getAnnotation(Convert.class);
            ClassName classConverter = ClassName.bestGuess(Utils.getConverterType(convert).toString());
            String packageName = classConverter.packageName();
            String name = classConverter.simpleName();
            System.out.println("BindConvertStatement----packageName:" + packageName);
            System.out.println("BindConvertStatement----name:" + name);

            if (SERIALIZABLE_CLASS_NAME.equals(classConverter)
                    || SERIALIZABLE_LIST_CLASS_NAME.equals(classConverter)) {
                TypeName typeName1 = ClassName.get(convertElement.asType());

                System.out.println("BindConvertStatement----typeName:" + typeName1.toString());
                TypeName typeName = findSerializableType(typeName1);

                if (typeName != null) {
                    builder.addStatement("$T.registerSerializableClass($T.class)", classCoreData, typeName);
                }
            }
        }
        return builder.build();
    }

    private static TypeName findSerializableType(TypeName typeName) {
        if (typeName instanceof ParameterizedTypeName) {
            List<TypeName> typeArguments = ((ParameterizedTypeName) typeName).typeArguments;
            return findSerializableType(typeArguments.get(0));
        }
        if (!typeName.isPrimitive() && !typeName.isBoxedPrimitive()
                && !stringClassName.equals(typeName)
                && !typeName.toString().equals("java.lang.String[]")) {
            return typeName;
        }
        return null;
    }

    public static List<FieldSpec> bindComvertFields(List<Element> convertElements) {
        if (convertElements == null) {
            return null;
        }
        List<FieldSpec> list = new ArrayList<>();
        for (Element convertElement : convertElements) {
            Convert convert = convertElement.getAnnotation(Convert.class);
            ClassName classConverter = ClassName.bestGuess(Utils.getConverterType(convert).toString());
            String packageName = classConverter.packageName();
            String name = classConverter.simpleName();
            System.out.println("BindConvertStatement----packageName:" + packageName);
            System.out.println("BindConvertStatement----name:" + name);
            FieldSpec fieldSpec;
            if (SERIALIZABLE_CLASS_NAME.equals(classConverter)) {
                fieldSpec = createSerializableField(convertElement);
            } else if (SERIALIZABLE_LIST_CLASS_NAME.equals(classConverter)) {
                fieldSpec = createSerializableListField(convertElement);
            } else {
                fieldSpec = createCommonField(convertElement, classConverter);
            }
            list.add(fieldSpec);
        }
        return list;
    }

    private static FieldSpec createCommonField(Element convertElement, ClassName classConverter) {
        return FieldSpec.builder(
                classConverter,
                Utils.converterName(convertElement, classConverter),
                Modifier.PRIVATE, Modifier.FINAL)
                .initializer("new $T()", classConverter)
                .build();
    }

    private static FieldSpec createSerializableField(Element convertElement) {
        ParameterizedTypeName parameterizedTypeName
                = ParameterizedTypeName.get(SERIALIZABLE_CLASS_NAME, ClassName.get(convertElement.asType()));
        return FieldSpec.builder(
                parameterizedTypeName,
                Utils.converterName(convertElement, SERIALIZABLE_CLASS_NAME),
                Modifier.PRIVATE, Modifier.FINAL)
                .initializer("new $T()", parameterizedTypeName)
                .build();
    }

    private static FieldSpec createSerializableListField(Element convertElement) {
        TypeName typeName = ClassName.get(convertElement.asType());
        if (typeName instanceof ParameterizedTypeName) {
            List<TypeName> typeArguments = ((ParameterizedTypeName) typeName).typeArguments;
            typeName = typeArguments.get(0);
        }
        ParameterizedTypeName parameterizedTypeName
                = ParameterizedTypeName.get(SERIALIZABLE_LIST_CLASS_NAME, typeName);
        return FieldSpec.builder(
                parameterizedTypeName,
                Utils.converterName(convertElement, SERIALIZABLE_LIST_CLASS_NAME),
                Modifier.PRIVATE, Modifier.FINAL)
                .initializer("new $T()", parameterizedTypeName)
                .build();
    }
}

package com.coredata.compiler.method;

import com.coredata.annotation.Convert;
import com.coredata.annotation.Embedded;
import com.coredata.annotation.Relation;
import com.coredata.compiler.EntityDetail;
import com.coredata.compiler.EntityProcessor;
import com.coredata.compiler.utils.TextUtils;
import com.coredata.compiler.utils.Utils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class BindStatementMethod extends BaseMethod {

    public BindStatementMethod(ProcessingEnvironment processingEnv, EntityDetail entityDetail) {
        super(processingEnv, entityDetail);
    }

    @Override
    public MethodSpec build() {
        return bindStatementMethod(ClassName.bestGuess(entityDetail.getEntityElement().asType().toString()), entityDetail.getDbElements()).build();
    }

    private MethodSpec.Builder bindStatementMethod(ClassName classEntity, List<Element> elements) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("bindStatement")
                .addModifiers(Modifier.PROTECTED)
                .returns(void.class)
                .addParameter(EntityProcessor.classSQLiteStatement, "statement")
                .addParameter(classEntity, "entity");
        bindStatementMethodInternal(builder, elements, 1, "entity", false);
        return builder;
    }

    private int bindStatementMethodInternal(MethodSpec.Builder builder, List<Element> elements, int index, String prefix, boolean carePrefix) {
        for (Element element : elements) {
            TypeName typeName = ClassName.get(element.asType());

            String fieldGetMethod = Utils.methodGet(element, prefix);
            if (typeName.isPrimitive() || typeName.isBoxedPrimitive()) {
                bindTo(builder, typeName, index, fieldGetMethod, prefix);
            } else {
                // String
                // convert
                // Relation
                // throw exception
                if (typeName.equals(ClassName.get(String.class))) {
                    bindTo(builder, typeName, index, fieldGetMethod, prefix);
                } else {
                    Embedded embedded = element.getAnnotation(Embedded.class);
                    if (embedded != null) {
                        Element elementEmbedded = processingEnv.getTypeUtils().asElement(element.asType());
                        List<Element> elementsForDbEmbedded = new ArrayList<>();
                        Utils.fillElementsForDbAndReturnPrimaryKey(processingEnv, elementsForDbEmbedded, (TypeElement) elementEmbedded);
                        index = bindStatementMethodInternal(builder, elementsForDbEmbedded, index, fieldGetMethod, true);
                        continue;
                    }
                    Convert convert = element.getAnnotation(Convert.class);
                    if (convert != null) {
                        // 转换
                        ClassName classConverter = ClassName.bestGuess(Utils.getConverterType(convert).toString());
                        ClassName dbClassName = ClassName.bestGuess(Utils.getConvertDbType(convert).toString());
                        if (!carePrefix || TextUtils.isEmpty(prefix)) {
                            builder.addStatement("$T __temp_$N = $N", dbClassName, String.valueOf(index), formatBindMethod(fieldGetMethod, Utils.converterName(element, classConverter)));
                        } else {
                            builder.addStatement("$T __temp_$N = $N == null ? $N : $N", dbClassName, String.valueOf(index), prefix, getDefaultValue(dbClassName), formatBindMethod(fieldGetMethod, Utils.converterName(element, classConverter)));
                        }
                        bindTo(builder, dbClassName, index, "__temp_" + index, null);
                    } else {
                        Relation relation = element.getAnnotation(Relation.class);
                        if (relation != null) {
                            TypeElement relationType = (TypeElement) processingEnv.getTypeUtils().asElement(element.asType());
                            EntityDetail relationEntityDetail = EntityDetail.parse(processingEnv, relationType);
                            Element relationEntityDetailPrimaryKey = relationEntityDetail.getPrimaryKey();
                            if (relationEntityDetailPrimaryKey == null) {
                                throw new RuntimeException(element.getSimpleName() + "#" + relationType.getSimpleName() + "must add has primaryKey");
                            }
                            TypeName dbClassName = ClassName.get(relationEntityDetailPrimaryKey.asType());
                            builder.addCode("$T __primaryKey_$N = ", dbClassName, String.valueOf(index));
                            if (carePrefix && !TextUtils.isEmpty(prefix)) {
                                builder.addCode("$N == null ? $N : ", prefix, getDefaultValue(dbClassName));
                            }
                            // 拼接当前数据判断为空
                            builder.addCode("($N == null ? $N : ", fieldGetMethod, getDefaultValue(dbClassName));
                            builder.addStatement("$N)", Utils.methodGet(relationEntityDetailPrimaryKey, fieldGetMethod));
                            bindTo(builder, dbClassName, index, "__primaryKey_" + index, null);
                        } else {
                            throw new RuntimeException(element.getSimpleName() + "must add @Relation or @Convert");
                        }
                    }
                }
            }
            index++;
        }
        return index;
    }

    private void bindTo(MethodSpec.Builder builder, TypeName typeName, int index, String methodGet, String prefixMethod) {
        if (typeName.isPrimitive()) {
            String bindStr = getBindStr(typeName);
            if (typeName == TypeName.BOOLEAN) {
                builder.addStatement("$N.$N($N, $N ? 1 : 0)", "statement", bindStr, String.valueOf(index), methodGet);
            } else {
                builder.addStatement("$N.$N($N, $N)", "statement", bindStr, String.valueOf(index), methodGet);
            }
        } else {
            builder.addCode("if(");
            if (!TextUtils.isEmpty(prefixMethod)) {
                builder.addCode("$N != null", prefixMethod);
                builder.addCode(" && ");
            }
            builder.addCode("$N != null", methodGet);
            builder.addCode("){\n  ");
            String bindStr = getBindStr(typeName);
            if (typeName.equals(ClassName.get(Boolean.class))) {
                builder.addStatement("$N.$N($N, $N ? 1 : 0)", "statement", bindStr, String.valueOf(index), methodGet);
            } else {
                builder.addStatement("$N.$N($N, $N)", "statement", bindStr, String.valueOf(index), methodGet);
            }
            builder.addCode("} else {\n  ");
            builder.addStatement("$N.bindNull($N)", "statement", String.valueOf(index));
            builder.addCode("}\n");
        }
    }

    private String formatBindMethod(String methodGet, String converField) {
        if (TextUtils.isEmpty(converField)) {
            return methodGet;
        }
        return converField + ".convertToProperty(" + methodGet + ")";
    }

    private static String getBindStr(TypeName typeName) {
        TypeName unbox = null;
        try {
            unbox = typeName.unbox();
        } catch (Exception ignored) {
        }
        if (unbox != null) {
            typeName = unbox;
        }
        if (typeName == TypeName.BYTE
                || typeName == TypeName.SHORT
                || typeName == TypeName.INT
                || typeName == TypeName.LONG
                || typeName == TypeName.CHAR) {
            return "bindLong";
        } else if (typeName == TypeName.FLOAT
                || typeName == TypeName.DOUBLE) {
            return "bindDouble";
        } else if (typeName == TypeName.BOOLEAN) {
            return "bindLong";
        }
        return "bindString";
    }

    private static Object getDefaultValue(TypeName typeName) {
        if (typeName == TypeName.BYTE
                || typeName == TypeName.SHORT
                || typeName == TypeName.INT
                || typeName == TypeName.LONG
                || typeName == TypeName.CHAR) {
            return "0";
        } else if (typeName == TypeName.FLOAT
                || typeName == TypeName.DOUBLE) {
            return "0";
        } else if (typeName == TypeName.BOOLEAN) {
            return "false";
        } else {
            return "null";
        }
    }
}

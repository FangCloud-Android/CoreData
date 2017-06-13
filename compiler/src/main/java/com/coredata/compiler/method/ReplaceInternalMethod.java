package com.coredata.compiler.method;

import com.coredata.compiler.utils.Utils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;

import static com.coredata.compiler.EntityProcessor.classSQLiteDatabase;

/**
 * Created by wangjinpeng on 2017/6/7.
 */

public class ReplaceInternalMethod extends BaseMethod {


    public ReplaceInternalMethod(ProcessingEnvironment processingEnv, TypeElement typeElement) {
        super(processingEnv, typeElement);
    }

    @Override
    public MethodSpec build() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("replaceInternal")
                .addModifiers(Modifier.PROTECTED)
                .returns(boolean.class)
                .addParameter(
                        ParameterizedTypeName.get(ClassName.get(Collection.class), ClassName.get(typeElement.asType())),
                        "collection")
                .addParameter(classSQLiteDatabase, "db");
        bind(builder);
        builder.addStatement("return true");
        return builder.build();
    }

    private void bind(MethodSpec.Builder builder) {
        Types typeUtils = processingEnv.getTypeUtils();
        TypeName typeEntity = ClassName.get(typeElement.asType());
        ParameterizedTypeName typeListEntity = ParameterizedTypeName.get(ClassName.get(ArrayList.class), typeEntity);
        builder.addStatement("$T $N = new $T()", typeListEntity, typeElement.getSimpleName() + "List", typeListEntity);
        List<Element> relationElements = Utils.getRelationElements(Utils.getElementsForDb(processingEnv.getElementUtils(), typeElement));
        for (Element relationElement : relationElements) {
            Element typeRelationElement = typeUtils.asElement(relationElement.asType());
            TypeName typeRelation = ClassName.get(relationElement.asType());
            ParameterizedTypeName typeListRelation = ParameterizedTypeName.get(ClassName.get(ArrayList.class), typeRelation);
            builder.addStatement("$T $N = new $T()", typeListRelation, typeRelationElement.getSimpleName() + "List", typeListRelation);
        }
        builder.addCode("for ($T item : collection) {\n  ", typeEntity);
        builder.addStatement("$N.add($N)", typeElement.getSimpleName() + "List", "item");
        for (Element relationElement : relationElements) {
            Element typeRelationElement = typeUtils.asElement(relationElement.asType());
            TypeName typeRelation = ClassName.get(relationElement.asType());
            relationElement.getSimpleName();
            String methodGet = Utils.methodGet(relationElement, "item");
            builder.addCode("if($N != null){\n  ", methodGet);
            builder.addStatement("$N.add($N)", typeRelationElement.getSimpleName() + "List", methodGet);
            builder.addCode("}\n");
        }
        builder.addCode("}\n");
        builder.addStatement("executeInsert($N, db)", typeElement.getSimpleName() + "List");
        for (Element relationElement : relationElements) {
            Element typeRelationElement = typeUtils.asElement(relationElement.asType());
            ClassName classNameRelation = ClassName.bestGuess(relationElement.asType().toString());
            String relationDaoName = Utils.relationDaoName(classNameRelation);
            builder.addStatement("$N.replace($N, db)", relationDaoName, typeRelationElement.getSimpleName() + "List");
        }
    }
}

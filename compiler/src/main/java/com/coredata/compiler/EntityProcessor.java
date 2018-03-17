package com.coredata.compiler;

import com.coredata.annotation.Entity;
import com.coredata.compiler.method.BindCursorMethod;
import com.coredata.compiler.method.BindStatementMethod;
import com.coredata.compiler.method.CreateConvertStatement;
import com.coredata.compiler.method.ReplaceInternalMethod;
import com.coredata.compiler.utils.SqlBuilder;
import com.coredata.compiler.utils.Utils;
import com.coredata.db.Property;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@SupportedAnnotationTypes("com.coredata.annotation.Entity")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public final class EntityProcessor extends AbstractProcessor {

    public static final ClassName classCoreDao = ClassName.bestGuess("com.coredata.core.CoreDao");
    public static final ClassName classCoreData = ClassName.bestGuess("com.coredata.core.CoreData");
    public static final ClassName classCoreProperty = ClassName.get(Property.class);
    public static final ClassName classSQLiteOpenHelper = ClassName.bestGuess("com.coredata.core.CoreDatabaseManager");
    public static final ClassName classSQLiteStatement = ClassName.bestGuess("com.coredata.core.db.CoreStatement");
    public static final ClassName classSQLiteDatabase = ClassName.bestGuess("com.coredata.core.db.CoreDatabase");
    public static final ClassName classCursor = ClassName.bestGuess("android.database.Cursor");

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(Entity.class);
        for (Element element : elementsAnnotatedWith) {
            if (element instanceof TypeElement) {
                try {
                    createEntityDao((TypeElement) element);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public void createEntityDao(TypeElement element) throws IOException {

        EntityDetail entityDetail = EntityDetail.parse(processingEnv, element);


        // 实体类的class
        ClassName classEntity = ClassName.bestGuess(element.asType().toString());


        if (entityDetail.getPrimaryKey() == null) {
            throw new RuntimeException(classEntity.reflectionName() + " 没有主键");
        }

        List<Property> propertyList = entityDetail.getProperties(processingEnv);

        // 1、找出tableName，PrimaryKeyName ok
        // 2、找出所有的PropertyConverter，并生成局部变量，类似 __TagListConverter ok
        // 3、找出所有关联对象 @Relation，并生成对应的dao 类似 __AuthorCoreDao ok
        // 4、onCreate方法，初始化 关联对象对应的 dao ok
        // 5、getInsertSql， 返回插入的sql语句 ok
        // 6、getCreateTableSql，返回建表语句
        // 7、getTableProperties，返回所有的表结构
        // 8、绑定数据

        // dao的java名字
        String daoName = String.format("%sCoreDaoImpl", entityDetail.getEntityName());
        TypeSpec.Builder daoTypeBuilder = TypeSpec.classBuilder(daoName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(ParameterizedTypeName.get(classCoreDao, classEntity));

        List<Element> convertElements = entityDetail.getConvertElements(processingEnv);

        // static 代码块
        CodeBlock convertStaticBlock = CreateConvertStatement.buildConvertStatic(convertElements);
        if (convertStaticBlock != null) {
            daoTypeBuilder.addStaticBlock(convertStaticBlock);
        }

        // 创建convert
        List<FieldSpec> convertFieldSpecs = CreateConvertStatement.bindComvertFields(convertElements);
        if (convertFieldSpecs != null) {
            for (FieldSpec fieldSpec : convertFieldSpecs) {
                daoTypeBuilder.addField(fieldSpec);
            }
        }

        // onCreate方法
        // 创建关联的dao
        List<Element> relationElements = entityDetail.getRelationElements();
        MethodSpec.Builder onCreateMethodBuilder = MethodSpec.methodBuilder("onCreate")
                .addModifiers(Modifier.PROTECTED)
                .returns(void.class)
                .addParameter(classCoreData, "coreData")
                .addStatement("super.onCreate($N)", "coreData");
        for (Element relationElement : relationElements) {
            TypeMirror typeMirror = relationElement.asType();
            ClassName classRelation =
                    ClassName.bestGuess(typeMirror.toString());
            String daoFieldName = Utils.relationDaoName(classRelation);
            FieldSpec fieldSpec = FieldSpec.builder(
                    ParameterizedTypeName.get(classCoreDao, classRelation),
                    daoFieldName,
                    Modifier.PRIVATE)
                    .build();
            daoTypeBuilder.addField(fieldSpec);

            onCreateMethodBuilder
                    .addStatement(
                            "$N = coreData.dao($T.class)",
                            daoFieldName,
                            classRelation
                    );
        }

        MethodSpec onCreateMethod = onCreateMethodBuilder.build();

        // getCreateTableSql 方法，用来获取建表语句
        MethodSpec getCreateTableSqlMethod = MethodSpec.methodBuilder("getCreateTableSql")
                .addModifiers(Modifier.PROTECTED)
                .returns(String.class)
                .addStatement("return $S", SqlBuilder.buildCreateSql(entityDetail.getTableName(), propertyList, true))
                .build();

        // getInsertSql 方法，用来获取插入语句
        MethodSpec getInsertSqlMethod = MethodSpec.methodBuilder("getInsertSql")
                .addModifiers(Modifier.PROTECTED)
                .returns(String.class)
                .addStatement("return $S", Utils.getInsertSql(entityDetail.getTableName(), propertyList))
                .build();

        // bindStatement 用来绑定数据
        MethodSpec bindStatementMethod = new BindStatementMethod(processingEnv, entityDetail).build();

        // replaceInternal 方法，用来处理关系型数据
        MethodSpec replaceInternalMethod = new ReplaceInternalMethod(processingEnv, entityDetail).build();

        // 创建 getTableName 方法，返回tableName
        MethodSpec getTableNameMethod = MethodSpec.methodBuilder("getTableName")
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class)
                .addStatement("return $S", entityDetail.getTableName())
                .build();

        // 创建 getPrimaryKeyName 方法，返回 主键的名字
        MethodSpec getPrimaryKeyNameMethod = MethodSpec.methodBuilder("getPrimaryKeyName")
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class)
                .addStatement("return $S", Utils.getColumnName(entityDetail.getPrimaryKey()))
                .build();

        // 创建 bindCursor 方法，绑定游标数据到模型
        MethodSpec bindCursorMethod = new BindCursorMethod(processingEnv, entityDetail).build();

        // 创建 getTableProperties 方法，返回所有字段相关的 Property
        ParameterizedTypeName listPropertyType = ParameterizedTypeName.get(ClassName.get(ArrayList.class), classCoreProperty);
        MethodSpec.Builder getTablePropertiesBuilder =
                MethodSpec.methodBuilder("getTableProperties")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ParameterizedTypeName.get(ClassName.get(List.class), classCoreProperty));
        getTablePropertiesBuilder.addStatement("$T list = new $T()", listPropertyType, listPropertyType);
        for (Property property : propertyList) {
            getTablePropertiesBuilder.addStatement("list.add(new $T($S, $T.class, $N))", classCoreProperty, property.name, Utils.getTypeNameByType(property.type), String.valueOf(property.primaryKey));
        }
        getTablePropertiesBuilder.addStatement("return list");

        daoTypeBuilder
                .addMethod(onCreateMethod)
                .addMethod(getTableNameMethod)
                .addMethod(getPrimaryKeyNameMethod)
                .addMethod(getTablePropertiesBuilder.build())
                .addMethod(getCreateTableSqlMethod)
                .addMethod(getInsertSqlMethod)
                .addMethod(bindStatementMethod)
                .addMethod(replaceInternalMethod)
                .addMethod(bindCursorMethod)
        ;
        JavaFile javaFile = JavaFile.builder(entityDetail.getEntityPackageName(processingEnv), daoTypeBuilder.build()).build();
        javaFile.writeTo(processingEnv.getFiler());
        System.out.println(element.getSimpleName());
        System.out.println(processingEnv.getElementUtils().getPackageOf(element).getQualifiedName());
    }
}
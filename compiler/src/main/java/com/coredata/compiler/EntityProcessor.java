package com.coredata.compiler;

import com.coredata.annotation.Convert;
import com.coredata.annotation.Embedded;
import com.coredata.annotation.Entity;
import com.coredata.compiler.db.Property;
import com.coredata.compiler.method.BindCursorMethod;
import com.coredata.compiler.method.BindStatementMethod;
import com.coredata.compiler.method.ReplaceInternalMethod;
import com.coredata.compiler.utils.SqlUtils;
import com.coredata.compiler.utils.TextUtils;
import com.coredata.compiler.utils.Utils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
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
    public static final ClassName classCoreProperty = ClassName.bestGuess("com.coredata.core.Property");
    public static final ClassName classSQLiteOpenHelper = ClassName.bestGuess("android.database.sqlite.SQLiteOpenHelper");
    public static final ClassName classSQLiteStatement = ClassName.bestGuess("android.database.sqlite.SQLiteStatement");
    public static final ClassName classSQLiteDatabase = ClassName.bestGuess("android.database.sqlite.SQLiteDatabase");
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
        Elements elementUtils = processingEnv.getElementUtils();
        Types typeUtils = processingEnv.getTypeUtils();
        // 实体的类名
        String entityName = element.getSimpleName().toString();
        // package
        String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
        String daoNameFormat = "%sCoreDaoImpl";
        // 表名
        String tableName;

        Entity entity = element.getAnnotation(Entity.class);
        tableName = entity.tableName();
        if (TextUtils.isEmpty(tableName)) {
            tableName = entityName;
        }
        ClassName classEntity = ClassName.bestGuess(element.asType().toString());

        List<Element> elementsForDb = Utils.getElementsForDb(elementUtils, element);

        Element primaryKeyElement = Utils.primaryKeyElement(elementsForDb);
        if (primaryKeyElement == null) {
            throw new RuntimeException(classEntity.reflectionName() + " 没有主键");
        }

        List<Element> relationElements = Utils.getRelationElements(elementsForDb);

        List<Element> convertElements = getConvertElements(processingEnv, elementsForDb);

        List<Property> propertyList = Utils.getProperties(elementUtils, typeUtils, elementsForDb);

        // 1、找出tableName，PrimaryKeyName ok
        // 2、找出所有的PropertyConverter，并生成局部变量，类似 __TagListConverter ok
        // 3、找出所有关联对象 @Relation，并生成对应的dao 类似 __AuthorCoreDao ok
        // 4、onCreate方法，初始化 关联对象对应的 dao ok
        // 5、getInsertSql， 返回插入的sql语句 ok
        // 6、
        // 7、getTableProperties，返回所有的表结构

        // dao的java名字
        String daoName = String.format(daoNameFormat, entityName);
        TypeSpec.Builder daoTypeBuilder = TypeSpec.classBuilder(daoName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(ParameterizedTypeName.get(classCoreDao, classEntity));

        // 创建convert
        for (Element convertElement : convertElements) {
            Convert convert = convertElement.getAnnotation(Convert.class);
            ClassName classConverter = ClassName.bestGuess(Utils.getConverterType(convert).toString());
            FieldSpec fieldSpec = FieldSpec.builder(
                    classConverter,
                    Utils.converterName(classConverter),
                    Modifier.PRIVATE, Modifier.FINAL)
                    .initializer("new $T()", classConverter)
                    .build();
            daoTypeBuilder.addField(fieldSpec);
        }

        // onCreate方法
        // 创建关联的dao
        MethodSpec.Builder onCreateMethodBuilder = MethodSpec.methodBuilder("onCreate")
                .addModifiers(Modifier.PROTECTED)
                .returns(void.class)
                .addParameter(classSQLiteOpenHelper, "dbHelper")
                .addStatement("super.onCreate($N)", "dbHelper");
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
                            "$N = $T.defaultInstance().dao($T.class)",
                            daoFieldName,
                            classCoreData,
                            classRelation
                    );
        }

        MethodSpec onCreateMethod = onCreateMethodBuilder.build();

        // getInsertSql 方法，用来获取插入语句
        MethodSpec getCreateTableSqlMethod = MethodSpec.methodBuilder("getCreateTableSql")
                .addModifiers(Modifier.PROTECTED)
                .returns(String.class)
                .addStatement("return $S", SqlUtils.buildCreateSql(tableName, propertyList, true))
                .build();

        // getInsertSql 方法，用来获取插入语句
        MethodSpec getInsertSqlMethod = MethodSpec.methodBuilder("getInsertSql")
                .addModifiers(Modifier.PROTECTED)
                .returns(String.class)
                .addStatement("return $S", Utils.getInsertSql(tableName, propertyList))
                .build();

        // bindStatement 用来绑定数据
        MethodSpec bindStatementMethod = new BindStatementMethod(processingEnv, element).build();

        MethodSpec replaceInternalMethod = new ReplaceInternalMethod(processingEnv, element).build();

        MethodSpec getTableNameMethod = MethodSpec.methodBuilder("getTableName")
                .addModifiers(Modifier.PROTECTED)
                .returns(String.class)
                .addStatement("return $S", tableName)
                .build();

        MethodSpec getPrimaryKeyNameMethod = MethodSpec.methodBuilder("getPrimaryKeyName")
                .addModifiers(Modifier.PROTECTED)
                .returns(String.class)
                .addStatement("return $S", Utils.getColumnName(primaryKeyElement))
                .build();

        MethodSpec bindCursorMethod = new BindCursorMethod(processingEnv, element).build();

        ParameterizedTypeName listPropertyType = ParameterizedTypeName.get(ClassName.get(ArrayList.class), classCoreProperty);
        MethodSpec.Builder getTablePropertiesBuilder =
                MethodSpec.methodBuilder("getTableProperties")
                        .addModifiers(Modifier.PROTECTED)
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
        JavaFile javaFile = JavaFile.builder(packageName, daoTypeBuilder.build()).build();
        javaFile.writeTo(processingEnv.getFiler());
        System.out.println(element.getSimpleName());
        System.out.println(elementUtils.getPackageOf(element).getQualifiedName());
    }

    private static List<Element> getConvertElements(ProcessingEnvironment processingEnv, List<Element> elements) {
        List<Element> elementList = new ArrayList<>();
        for (Element element : elements) {
            if (element.getAnnotation(Convert.class) != null) {
                elementList.add(element);
            } else {
                if (element.getAnnotation(Embedded.class) != null) {
                    Element elementEmbedded = processingEnv.getTypeUtils().asElement(element.asType());
                    elementList.addAll(getConvertElements(processingEnv, Utils.getElementsForDb(processingEnv.getElementUtils(), (TypeElement) elementEmbedded)));
                }
            }
        }
        return elementList;
    }
}
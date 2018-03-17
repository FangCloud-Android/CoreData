package com.coredata.compiler.method;

import com.coredata.compiler.EntityDetail;
import com.squareup.javapoet.MethodSpec;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

/**
 * Created by wangjinpeng on 2017/6/7.
 */

public abstract class BaseMethod {

    protected ProcessingEnvironment processingEnv;
    protected EntityDetail entityDetail;

    public BaseMethod(ProcessingEnvironment processingEnv, EntityDetail entityDetail) {
        this.processingEnv = processingEnv;
        this.entityDetail = entityDetail;
    }

    public abstract MethodSpec build();
}

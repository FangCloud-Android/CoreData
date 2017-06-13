package com.coredata.compiler.method;

import com.squareup.javapoet.MethodSpec;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

/**
 * Created by wangjinpeng on 2017/6/7.
 */

public abstract class BaseMethod {

    protected ProcessingEnvironment processingEnv;
    protected TypeElement typeElement;

    public BaseMethod(ProcessingEnvironment processingEnv, TypeElement typeElement) {
        this.processingEnv = processingEnv;
        this.typeElement = typeElement;
    }

    public abstract MethodSpec build();
}

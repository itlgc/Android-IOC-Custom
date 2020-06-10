package com.it.ioc_library.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 事件注解的父类
 * Created by lgc on 2020-02-16.
 */
//作用在注解之上
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventBase {

    //事件3个重要的成员封装
    // 1、setXXXClickListener
    String listenerSetter();

    // 2、new xxxClickListener()对象
    Class<?> listenerType();

    // 3、回调方法 onxxx()
    String callbackListener();
}

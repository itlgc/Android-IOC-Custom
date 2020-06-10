package com.it.ioc_library.annotation;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by lgc on 2020-02-16.
 *
 * @description
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@EventBase(listenerSetter="setOnLongClickListener", listenerType = View.OnLongClickListener.class,
        callbackListener = "onLongClick")
public @interface OnLongClick {
    int[] value();

}

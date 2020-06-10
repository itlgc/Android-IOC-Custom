package com.it.ioc_library;

import android.app.Activity;
import android.view.View;

import com.it.ioc_library.annotation.ContentView;
import com.it.ioc_library.annotation.EventBase;
import com.it.ioc_library.annotation.InjectView;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;


/**
 * 注入管理类
 * Created by lgc on 2020-02-16.
 */
public class InjectManagerSimple {

    public static void inject(Activity activity) {
        //布局的注入
        try {
            injectLayout(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //控件的注入
        try {
            injectViews(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //事件的注入
        try {
            injectEvents(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 布局的注入
     */
    private static void injectLayout(Activity activity) throws Exception {
        Class<? extends Activity> clazz = activity.getClass();
        //获取类之上的注解
        ContentView contentView = clazz.getAnnotation(ContentView.class);
        if (contentView != null) {
            //获取注解的值,也就是布局id
            int layoutId = contentView.value();

            //执行setContentView方法
            Method setContentViewMethod = clazz.getMethod("setContentView", int.class);
            setContentViewMethod.invoke(activity, layoutId);
        }
    }

    /**
     * 控件的注入
     */
    private static void injectViews(Activity activity) throws Exception {
        Class<? extends Activity> clazz = activity.getClass();
        //获取该类所有属性
        Field[] fields = clazz.getDeclaredFields();
        //循环 获取属性上的注解
        for (Field field : fields) {
            InjectView injectView = field.getAnnotation(InjectView.class);
            if (injectView != null) {
                int viewId = injectView.value();

                Method findViewByIdMethod = clazz.getMethod("findViewById", int.class);
                Object view = findViewByIdMethod.invoke(activity, viewId);

                //赋值 属性的值赋值给控件 在当前的Activity
                field.setAccessible(true);
                field.set(activity, view);
            }
        }
    }

    /**
     * 事件的注入
     */
    private static void injectEvents(Activity activity) throws Exception {
        Class<? extends Activity> clazz = activity.getClass();
        //获取类的所有方法
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            //循环 获取方法上的所有注解
            Annotation[] annotations = method.getAnnotations();

            for (Annotation annotation : annotations) {
                //遍历 获取其中onClick注解上的注解，即获取父类EventBase注解上的类型
                // 进而从方法诸多注解中筛选出Onclick注解
                Class<? extends Annotation> annotationType = annotation.annotationType();
                if (annotationType != null) {
                    //获取EventBase注解 和事件3个重要的成员封装
                    EventBase eventBase = annotationType.getAnnotation(EventBase.class);
                    if (eventBase != null) {
                        String listenerSetter = eventBase.listenerSetter();
                        Class<?> listenerType = eventBase.listenerType();
                        //这就是需要拦截的方法
                        String callbackListener = eventBase.callbackListener();

                        //获取onClick注解中的value方法
                        Method valueMethod = annotationType.getDeclaredMethod("value");
                        valueMethod.setAccessible(true);
                        //获取注解上的值 （数组）
                        int[] ViewsId = (int[]) valueMethod.invoke(annotation);

                        //动态代理
                        ListenerInvocationHandler invocationHandler =
                                new ListenerInvocationHandler(activity);
                        invocationHandler.addMethods(callbackListener, method);

                        Object listener = Proxy.newProxyInstance(listenerType.getClassLoader(),
                                new Class[]{listenerType}, invocationHandler);

                        for (int viewId : ViewsId) {
                            //获取当前activity中View控件 并赋值
                            View view = activity.findViewById(viewId);

                            //这样才能兼容 setxxxClicklister  setxxxLongClickLister等
                            Method setter = view.getClass().getMethod(listenerSetter, listenerType);
                            setter.invoke(view, listener);

                        }
                    }
                }
            }
        }
    }


    static class ListenerInvocationHandler implements InvocationHandler {
        private Object target;//要拦截的对象  eg.Activity  Fragment
        private Map<String, Method> methodMap = new HashMap<>();

        public ListenerInvocationHandler(Object object) {
            target = object;
        }

        /**
         * 添加需要拦截的方法
         *
         * @param methodName 要拦截的方法 eg. onClick 、 onLongClick
         * @param method     定义的事件注解的方法
         */
        public void addMethods(String methodName, Method method) {
            methodMap.put(methodName, method);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //获取到系统回调的方法名 要拦截的方法 eg. onClick
            String methodName = method.getName();

            //替换操作
            Method method_ = methodMap.get(methodName);
            if (method_ != null) {
                //无参数的点击事件处理
                if (method_.getParameterTypes().length == 0) { //方法没有参数
                    if (boolean.class == method.getReturnType()) {
                        method_.invoke(target);
                        return true;
                    }
                    return method_.invoke(target);
                }

                if (boolean.class == method.getReturnType()) {
                    method_.invoke(target, args);
                    return true;
                }
                return method_.invoke(target, args);
            }
            return null;
        }
    }

}



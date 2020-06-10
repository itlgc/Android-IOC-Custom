

### Ioc注入框架的手写实现

目的：自己手写是为了进一步加深Android种IOC注入实现的理解，例如Android Annotations，ButterKnife，Dagger等开源库都有体现。



#### loC的核心是解耦

在Spring中IoC更多的是依靠xml的配置
而Android上的IoC框架可以不使用xml配置



#### 布局注入思路

获得类>布局注解>注解的值>获取指定方法>执行方法

```java
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
```



#### 控件注入思路

获得类>获得所有属性>遍历属性>每个属性的注解>每个注解的值>获得指定方法>执行方法>设置访问私有>赋值

```java
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
```



#### 事件注入思路

事件有点击事件、长按事件、触摸事件等，需要对它们进行兼容处理

```java
        /**
         * 代码特点：
         * 1、setXXXClickListener
         * 2、xxxClickListener
         * 3、回调方法 onxxx()
         */
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //真正写代码的地方
            }
        });

        btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
```

根据事件代码特点将其3个重要的成员进行封装

```java
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
```

针对点击事件的注解

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@EventBase(listenerSetter = "setOnClickListener",listenerType = View.OnClickListener.class,
        callbackListener = "onClick")
public @interface OnClick {
    int[] value();
}
```



具体操作思路见如下注释

```java
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
```
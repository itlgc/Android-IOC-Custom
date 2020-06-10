package com.it.android_ioc_custom;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.it.ioc_library.annotation.ContentView;
import com.it.ioc_library.annotation.InjectView;
import com.it.ioc_library.annotation.OnClick;
import com.it.ioc_library.annotation.OnLongClick;

/**
 * 自定义实现 IOC 注入框架设计
 */
//1、布局注入
@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    //2、控件注入
    @InjectView(R.id.tv)
    private TextView tv;

    @InjectView(R.id.btn)
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        Log.e("TAG:", "布局注入生效了");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //btn = findViewById(R.id.btn);
        Log.e("TAG:", "控件注入生效了 btn:" + btn.getText().toString());

    }

    //3、事件注入
    @OnClick({R.id.btn,R.id.tv})
    public void clickEvent1(View view){
        Toast.makeText(getApplicationContext(),"事件注入生效了", Toast.LENGTH_SHORT).show();
        Log.e("TAG:", "事件注入生效了 viewId:" + view.getId());
    }

    //无参数的方法也可以注入监听事件
//    @OnClick({R.id.btn,R.id.tv})
//    public void clickEvent2(){
//        Toast.makeText(getApplicationContext(),"事件注入生效了2", Toast.LENGTH_SHORT).show();
//    }

    @OnLongClick({R.id.btn,R.id.tv})
    public void longClickEvent() {
        Toast.makeText(getApplicationContext(),"长按事件注入生效了", Toast.LENGTH_SHORT).show();
    }


}

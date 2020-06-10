package com.it.android_ioc_custom;

import android.app.Activity;
import android.os.Bundle;
import com.it.ioc_library.InjectManager;
import com.it.ioc_library.InjectManagerSimple;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by lgc on 2020-02-16.
 *
 * @description
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //一般会在父类 帮助子类进行 布局、控件、事件的注入

//        InjectManagerSimple.inject(this);
        InjectManager.inject(this);
    }
}

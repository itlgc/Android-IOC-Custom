package com.it.android_ioc_custom;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.it.ioc_library.InjectManager;
import com.it.ioc_library.annotation.InjectView;
import com.it.ioc_library.annotation.OnClick;
import com.it.ioc_library.annotation.OnLongClick;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by lgc on 2020-06-10.
 */
public class TestFragment extends Fragment {

    @InjectView(R.id.tv_fragment)
    private TextView tv;

    @InjectView(R.id.btn_fragment)
    private Button btn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
       return inflater.inflate(R.layout.fragment_test, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        InjectManager.inject(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e("TAG:Fragment", "控件注入生效了 btn:" + btn.getText().toString());
    }

    //3、事件注入
    @OnClick({R.id.btn_fragment,R.id.tv_fragment})
    public void clickEventFragment(View view){
        Toast.makeText(getContext(),"事件注入生效了", Toast.LENGTH_SHORT).show();
        Log.e("TAG:Fragment", "事件注入生效了 viewId:" + view.getId());
    }


}

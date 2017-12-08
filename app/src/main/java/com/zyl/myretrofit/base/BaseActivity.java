package com.zyl.myretrofit.base;

import android.os.Bundle;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;


/**
 * Created by zyl.
 * 一个拥有DataBinding框架的基Activity
 * 这里根据项目业务可以换成你自己熟悉的BaseActivity, 但是需要继承RxAppCompatActivity,方便LifecycleProvider管理生命周期
 */

public abstract class BaseActivity extends RxAppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

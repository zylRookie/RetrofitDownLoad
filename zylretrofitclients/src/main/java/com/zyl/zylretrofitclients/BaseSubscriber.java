package com.zyl.zylretrofitclients;

import android.content.Context;
import android.widget.Toast;


import rx.Subscriber;

/**
 * Created by zyl.
 * 该类仅供参考，实际业务Code, 根据需求来定义，
 */
public abstract class BaseSubscriber<T> extends Subscriber<T> {
    public abstract void onResult(T t);

    private Context context;
    private boolean isNeedCahe;

    public BaseSubscriber(Context context) {
        this.context = context;
    }

    @Override
    public void onError(Throwable e) {
        //KLog.e(e.getMessage());
        // todo error somthing

        if (e instanceof ExceptionHandle.ResponeThrowable) {
            onError((ExceptionHandle.ResponeThrowable) e);
        } else {
            onError(new ExceptionHandle.ResponeThrowable(e, ExceptionHandle.ERROR.UNKNOWN));
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        Toast.makeText(context, "http is start", Toast.LENGTH_SHORT).show();
        // todo some common as show loadding  and check netWork is NetworkAvailable
        // if  NetworkAvailable no !   must to call onCompleted
        if (!NetworkUtil.isNetworkAvailable(context)) {
            Toast.makeText(context, "无网络，读取缓存数据", Toast.LENGTH_SHORT).show();
            onCompleted();
        }

    }

    @Override
    public void onCompleted() {

        Toast.makeText(context, "http is Complete", Toast.LENGTH_SHORT).show();
        // todo some common as  dismiss loadding
    }


    public abstract void onError(ExceptionHandle.ResponeThrowable e);

    @Override
    public void onNext(Object o) {
        BaseResponse baseResponse = (BaseResponse) o;
        if (baseResponse.getCode() == 200) {
            onResult((T) baseResponse.getResult());
        } else if (baseResponse.getCode() == 330) {
            //ToastUtils.showShort(baseResponse.getMessage());
        } else if (baseResponse.getCode() == 503) {
            //KLog.e(baseResponse.getMessage());
        } else {
            //ToastUtils.showShort("操作失败！错误代码:" + baseResponse.getCode());
        }
    }
}

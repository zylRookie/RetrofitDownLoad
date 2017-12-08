package com.zyl.zylretrofitclients;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by zyl.
 * 有关Rx的工具类
 */
public class RxUtils {

    /**
     * 线程调度器
     */
    public static Observable.Transformer schedulersTransformer() {
        return new Observable.Transformer() {
            @Override
            public Object call(Object observable) {
                return ((Observable) observable).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
           /* @Override
            public Observable call(Observable observable) {
                return observable.subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }*/
        };
    }

    public static <T> Observable.Transformer<BaseResponse<T>, T> exceptionTransformer() {

        return new Observable.Transformer() {

            @Override
            public Object call(Object observable) {
                return ((Observable) observable).map(new HandleFuc<T>()).onErrorResumeNext(new HttpResponseFunc<T>());
            }
        };
    }

    private static class HttpResponseFunc<T> implements Func1<Throwable, Observable<T>> {
        @Override
        public Observable<T> call(Throwable t) {
            return Observable.error(ExceptionHandle.handleException(t));
        }
    }

    private static class HandleFuc<T> implements Func1<BaseResponse<T>, T> {
        @Override
        public T call(BaseResponse<T> response) {
            if (!response.isOk())
                throw new RuntimeException(response.getCode() + "" + response.getMessage() != null ? response.getMessage() : "");
            return response.getResult();
        }
    }

}

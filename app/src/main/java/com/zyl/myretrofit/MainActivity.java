package com.zyl.myretrofit;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.zyl.myretrofit.base.BaseActivity;
import com.zyl.myretrofit.utils.ToastUtils;
import com.zyl.zylretrofitclients.BaseResponse;
import com.zyl.zylretrofitclients.DownLoadManager;
import com.zyl.zylretrofitclients.RxUtils;
import com.zyl.zylretrofitclients.download.ProgressCallBack;

import java.io.File;

import kr.co.namee.permissiongen.PermissionGen;
import okhttp3.ResponseBody;
import rx.functions.Action0;
import rx.functions.Action1;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionGen.with(this)
                .addRequestCode(100)
                .permissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .request();
    }

    public void retrofit(View view) {
        RetrofitClient.getInstance().create(DemoApiService.class)
                .demoGet()
                //.compose(RxUtils.bindToLifecycle(context)) //请求与View周期同步//这里是使用rxLifecycle进行生命周期管理的绑定
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RxUtils.exceptionTransformer()) // 请求code异常处理, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        // showDialog();
                    }
                })
                .subscribe(new Action1<BaseResponse<DemoEntity>>() {
                    @Override
                    public void call(BaseResponse<DemoEntity> response) {
                        //dismissDialog();
                        //请求成功
                        if (response.getCode() == 1) {

                        } else {
                            ToastUtils.showShort("数据错误");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {//自己定义的异常返回
                        //dismissDialog();
                        Log.e("AAA", "call: " + throwable);
                        throwable.printStackTrace();
                    }
                });
    }

    public void retrofitDownLoad(View view) {
        downloadFile();
    }

    /**
     * 下载
     */
    public void downloadFile() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("正在下载...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String loadUrl = "http://a.gdown.baidu.com/data/wisegame/2828a29ba864e167/neihanduanzi_660.apk?from=a1101";
        final String destFileDir = Environment.getExternalStorageDirectory().getPath() + "/tcDown/";  //文件存放的路径
        final String destFileName = "neihanduanzi.apk";//文件存放的名称
        DownLoadManager.getInstance().load(loadUrl, new ProgressCallBack<ResponseBody>(destFileDir, destFileName) {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onCompleted() {
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(ResponseBody responseBody) {
                ToastUtils.showShort("文件下载完成！");
                //通过隐式意图调用系统安装程序安装APK
                Intent intent = new Intent(Intent.ACTION_VIEW);
                // 由于没有在Activity环境下启动Activity,设置下面的标签
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上 自己到网上找provider方法
//                    //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
//                    Uri apkUri = FileProvider.getUriForFile(OnStartActivity.this, "com.tcwlkj.uuCustomer.fileprovider", result);
//                    //添加这一句表示对目标应用临时授权该Uri所代表的文件
//                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(Uri.fromFile(new File(destFileDir, destFileName)), "application/vnd.android.package-archive");
                }
                startActivity(intent);
            }

            @Override
            public void progress(final long progress, final long total) {
                progressDialog.setMax((int) total);
                progressDialog.setProgress((int) progress);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                ToastUtils.showShort("文件下载失败！");
            }
        });
    }
}

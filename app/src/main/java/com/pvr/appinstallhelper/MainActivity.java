package com.pvr.appinstallhelper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.IPackageInstallObserver;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener, PackageUtils.InstallListener {
    Button mBtnClickInstall;
    TextView mTVInstallResult;
    RadioGroup mRGApkSource;
    ScrollView sll_handle;
    public static final String TAG = "installhelper";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initFunction();
    }

    private void initView() {
        mBtnClickInstall = findViewById(R.id.btn_click_install);
        mTVInstallResult = findViewById(R.id.tv_install_result);
        mRGApkSource = findViewById(R.id.rg_apk_source);
        sll_handle = findViewById(R.id.sv_content);
    }

    private void initData() {
    }

    private void initFunction() {
        mBtnClickInstall.setOnClickListener(this);
        mRGApkSource.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_internal) {
                    SdCardUtils.GET_APK_PATH = SdCardUtils.GET_APK_FROM_SDCARD;
                } else {
                    SdCardUtils.GET_APK_PATH = SdCardUtils.GET_APK_FROM_EXTERNAL_STORAGE;
                }
            }
        });
        mRGApkSource.check(R.id.rb_internal);

        sll_handle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                sll_handle.post(new Runnable() {
                    public void run() {
                        sll_handle.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        });
    }

    List<String> apksPath;

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_click_install) {
            apksPath = SdCardUtils.getApksPath(this);
            if (apksPath.size() == 0) {
                mTVInstallResult.setText("未扫描到apk，请确认apk放置路径正确");
                return;
            }
            mTVInstallResult.setText("扫描完毕，共发现" + apksPath.size() + "个可安装应用，开始安装...\n安装过程中请勿执行其他操作\n\n");
            for (String apkPath : apksPath) {
                PackageUtils.getInstance(this).installApp(new File(apkPath), this, this);
            }
        }
    }

    @Override
    public void oneAppInstalling(String appPath) {
        //mTVInstallResult.append(appPath + "安装中。。。\n");
    }

    @Override
    public void oneAppInstallFinish(final String apkName, final String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                installedApkAmount++;
                if (result.equals(PackageUtils.installStatusToString(1))) {
                    installedApkSuccessAmount++;
                } else {
                    installedApkFailureAmount++;
                }
                mTVInstallResult.append(apkName + result + "\n");
                if (installedApkAmount == apksPath.size()) {
                    allAppInstallFinish();
                }
            }
        });

    }

    int installedApkAmount = 0;
    int installedApkSuccessAmount = 0;
    int installedApkFailureAmount = 0;

    @Override
    public void allAppInstallFinish() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTVInstallResult.append("\n全部安装完毕,共尝试安装应用" + installedApkAmount + "个，安装成功" + installedApkSuccessAmount + "个，安装失败" + installedApkFailureAmount + "个");
                installedApkAmount = 0;
                installedApkSuccessAmount = 0;
                installedApkFailureAmount = 0;
            }
        });

    }

}


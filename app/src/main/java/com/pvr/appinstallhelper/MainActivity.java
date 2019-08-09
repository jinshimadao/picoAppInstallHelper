package com.pvr.appinstallhelper;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener, PackageUtils.InstallListener {
    Button mBtnClickInstall;
    //TextView mTVInstallDes;
    TextView mTVNoneAPKfound;
    TextView mTVInstallSummary;
    TextView mTVInstallProgress;
    CheckBox mCBDeleteAPK;
    RadioGroup mRGApkSource;
    ScrollView sll_handle;
    Boolean deleteApkWhenInstallSuccess;
    RecyclerView mRVAppInstallation;
    MyRecyleViewAdapter mMyRecyleViewAdapter;
    LinearLayout mLLInstallHint;
    ArrayList<APKInfo> apkInfoList;
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
        mTVNoneAPKfound = findViewById(R.id.tv_none_apk_found);
        mRGApkSource = findViewById(R.id.rg_apk_source);
        sll_handle = findViewById(R.id.sv_content);
        mCBDeleteAPK = findViewById(R.id.cb_deleteapk);
        mLLInstallHint = findViewById(R.id.ll_installing_hint);
        mTVInstallProgress = findViewById(R.id.tv_install_progress);
        mTVInstallSummary = findViewById(R.id.tv_install_summary);
        mRVAppInstallation = findViewById(R.id.rv_app_installation);
        mRVAppInstallation.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initData() {
        deleteApkWhenInstallSuccess = mCBDeleteAPK.isChecked();
        mLLInstallHint.setVisibility(View.INVISIBLE);
        apkInfoList = new ArrayList<>();
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

//        sll_handle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                sll_handle.post(new Runnable() {
//                    public void run() {
//                        sll_handle.fullScroll(View.FOCUS_DOWN);
//                    }
//                });
//            }
//        });
        mCBDeleteAPK.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    deleteApkWhenInstallSuccess = true;
                } else {
                    deleteApkWhenInstallSuccess = false;
                }
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
                mTVNoneAPKfound.setText(R.string.apk_not_found);
                mTVNoneAPKfound.setVisibility(View.VISIBLE);
                mRVAppInstallation.setVisibility(View.INVISIBLE);
                mTVInstallSummary.setVisibility(View.INVISIBLE);
                return;
            }
            mTVNoneAPKfound.setVisibility(View.INVISIBLE);
            String apkDir = SdCardUtils.getApkDir(this);
            long folderSize = Utils.getFolderSize(new File(apkDir));
            String apksSize = Utils.getUnit(folderSize);
            String dialogInfo = getString(R.string.scan_dialog_info);
            String formatDialogInfo = String.format(dialogInfo, apksPath.size() + "", apksSize, Utils.getRomAvailableSize());
            DialogUtil.init(this).setInfo("", formatDialogInfo).setButton(getString(R.string.install), getString(R.string.cancel), new DialogUtil.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int flag) {
                    if (flag == 1) {
                        setMenuWidgetsClickable(false);
                        initInstallationRecycleView();
                        mLLInstallHint.setAlpha(1);
                        mLLInstallHint.setVisibility(View.VISIBLE);
                        mTVInstallSummary.setVisibility(View.INVISIBLE);
                        mRVAppInstallation.setVisibility(View.VISIBLE);
                        String strInstalledApkAmount = installedApkAmount < 10 ? " " + installedApkAmount : "" + installedApkAmount;
                        mTVInstallProgress.setText(getString(R.string.installation_progress) + strInstalledApkAmount + "/" + apksPath.size());
                        for (String apkPath : apksPath) {
                            PackageUtils.getInstance(MainActivity.this).installApp(new File(apkPath), MainActivity.this, MainActivity.this);
                        }
                    } else {
                        dialogInterface.dismiss();
                    }

                }
            }).show();
        }
    }

    private void initInstallationRecycleView() {
        Log.i(TAG, "DO initInstallationRecycleView");
        apkInfoList.clear();
        if (null != mRVAppInstallation.getAdapter()) {
            mRVAppInstallation.getAdapter().notifyDataSetChanged();
        }
        for (String filepath : apksPath) {
            PackageManager pm = getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(filepath, PackageManager.GET_ACTIVITIES);
            if (info != null) {
                ApplicationInfo appInfo = info.applicationInfo;
                appInfo.sourceDir = filepath;
                appInfo.publicSourceDir = filepath;
                String mAPKName = new File(filepath).getName();
                Drawable icon = appInfo.loadIcon(pm);//得到图标信息
                APKInfo mAPKInfo = new APKInfo(icon, mAPKName);
                apkInfoList.add(mAPKInfo);
            }
        }
        Log.i(TAG, "apkInfoList.size = " + apkInfoList.size());
        mMyRecyleViewAdapter = new MyRecyleViewAdapter(this, apkInfoList);
        mRVAppInstallation.setAdapter(mMyRecyleViewAdapter);
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
                String strInstalledApkAmount = installedApkAmount < 10 ? " " + installedApkAmount : "" + installedApkAmount;
                mTVInstallProgress.setText(getString(R.string.installation_progress) + strInstalledApkAmount + "/" + apksPath.size());

                int position = 0;
                for (APKInfo mAPKInfo : apkInfoList) {
                    Log.i(TAG, "mAPKInfo.getAPKName() = " + mAPKInfo.getAPKName() + ",apkName = " + apkName);
                    if (mAPKInfo.getAPKName().equals(apkName)) {
                        Log.i(TAG, "匹配的position = " + position);
                        mAPKInfo.setmInstalling(false);
                        if (deleteApkWhenInstallSuccess) {
                            File file = new File(SdCardUtils.getApkPath(MainActivity.this, apkName));
                            if (file.delete()) {
                                mAPKInfo.setmInstallResult(result + "，" + getString(R.string.apk_delete_success));
                                //mTVInstallDes.append(formatAPKName + " " + result + "，" + getString(R.string.apk_delete_success) + "\n");
                            } else {
                                mAPKInfo.setmInstallResult(result + "，" + getString(R.string.apk_delete_failed));
                                //mTVInstallDes.append(formatAPKName + " " + result + "，" + getString(R.string.apk_delete_failed) + "\n");
                            }
                        } else {
                            mAPKInfo.setmInstallResult(result);
                        }
                        mRVAppInstallation.getAdapter().notifyItemChanged(position);
                        position = 0;
                    } else {
                        position++;
                    }
                }


                if (result.equals(PackageUtils.getInstance(MainActivity.this).installStatusToString(1))) {
                    installedApkSuccessAmount++;
                } else {
                    installedApkFailureAmount++;
                }
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
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        mLLInstallHint.setVisibility(View.INVISIBLE);
                        mTVInstallSummary.setVisibility(View.VISIBLE);
                        String formatInstallResultFormat = String.format(getString(R.string.install_result_summary), installedApkAmount, installedApkSuccessAmount, installedApkFailureAmount);
                        mTVInstallSummary.setText(formatInstallResultFormat);
                        installedApkAmount = 0;
                        installedApkSuccessAmount = 0;
                        installedApkFailureAmount = 0;
                        setMenuWidgetsClickable(true);
                    }
                }, 2 * 1000);
            }
        });

    }

    public void setMenuWidgetsClickable(Boolean clickable) {
        mBtnClickInstall.setClickable(clickable);
        mCBDeleteAPK.setClickable(clickable);
        SetRadioGroupClickable(mRGApkSource, clickable);
        if (!clickable) {
            mBtnClickInstall.setTextColor(Color.GRAY);
            mCBDeleteAPK.setTextColor(Color.GRAY);
        } else {
            mBtnClickInstall.setTextColor(Color.BLACK);
            mCBDeleteAPK.setTextColor(Color.BLACK);
        }
    }

    public void SetRadioGroupClickable(RadioGroup testRadioGroup, Boolean clickable) {
        for (int i = 0; i < testRadioGroup.getChildCount(); i++) {
            testRadioGroup.getChildAt(i).setEnabled(clickable);
        }
    }


}


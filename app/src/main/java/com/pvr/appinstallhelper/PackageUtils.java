package com.pvr.appinstallhelper;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.opengl.GLES20;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PackageUtils {
    private static final String TAG = "PackageUtils";
    private static ExecutorService mInstaller = Executors.newFixedThreadPool(1);

    private PackageManager mPackageManager;

    private PackageUtils(Context context) {
        mPackageManager = context.getPackageManager();
    }

    private static PackageUtils instance = null;

    public static PackageUtils getInstance(Context context) {
        synchronized (PackageUtils.class) {
            if (instance == null) {
                instance = new PackageUtils(context);
            }
        }
        return instance;
    }

    /**
     * 获取apk文件的一些信息
     *
     * @param
     * @param appPath
     * @return packageName+","+versionName+","+versionCode
     */
    public static String getPackageInfo(String appPath) {
        String info = "";
        try {
            File file = new File(appPath);
            if (!file.exists()) {
                return "";
            }
            Object pkg;// PackageParser.Package
            Class<?> clzPackageParser = Class
                    .forName("android.content.pm.PackageParser");
            Method method = clzPackageParser.getDeclaredMethod(
                    "parseMonolithicPackage", File.class, int.class);
            pkg = method.invoke(clzPackageParser.newInstance(), file, 0);
            Class<?> clzPackage = pkg.getClass();
            Class<?> clzPackageUserState = Class
                    .forName("android.content.pm.PackageUserState");
            Method generatePackageInfoMethod = clzPackageParser.getMethod(
                    "generatePackageInfo", clzPackage, int[].class, int.class,
                    long.class, long.class, Set.class, clzPackageUserState);
            PackageInfo packageInfo = (PackageInfo) generatePackageInfoMethod
                    .invoke(clzPackageParser, pkg, null,
                            PackageManager.GET_PERMISSIONS, 0, 0, null,
                            clzPackageUserState.newInstance());
            String packageName = packageInfo.packageName;
            String versionName = packageInfo.versionName;
            int versionCode = packageInfo.versionCode;
//			String appName = packageInfo.applicationInfo.loadLabel(
//					activity.getPackageManager()).toString();
            info = packageName + "," + versionName + "," + versionCode;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return info;
    }


    /**
     * app静默安装
     *
     * @param appFile
     * @param context
     */
    public void installApp(File appFile, Context context, InstallListener mInstallListener) {
        mInstallListener.oneAppInstalling(appFile.getName());
        try {
            Class<?>[] types = new Class[]{Uri.class, IPackageInstallObserver.class, int.class, String.class};
            Method method = context.getPackageManager().getClass().getMethod("installPackage", types);
            method.invoke(context.getPackageManager(), Uri.fromFile(appFile),
                    new MyAppInstallObserver(appFile, context, mInstallListener), 0x00000002, "com.pvr.appinstallhelper");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class MyAppInstallObserver extends IPackageInstallObserver.Stub {
        private File mFile;
        private Context mContext;
        private InstallListener mInstallListener;

        public MyAppInstallObserver(File file, Context context, InstallListener installListener) {
            mFile = file;
            mContext = context;
            mInstallListener = installListener;
        }

        @Override
        public void packageInstalled(String packageName, int returnCode) throws RemoteException {
            Log.i(TAG, "app升级安装完成：" + packageName + ", " + returnCode + ", " + mFile.getAbsolutePath());
            //  不用反射的方法获取installStatusToString了，直接自己将returnCode转换成想要的字符串
//              Class<PackageManager> pkm = PackageManager.class;//首先还是必须得到这个对象的Class。
//              Method mInstallStatusToString = pkm.getDeclaredMethod("installStatusToString", int.class);//得到执行的method
//              mInstallStatusToString.setAccessible(true);//设置访问权限
//              String result = (String) mInstallStatusToString.invoke(mPackageManager, returnCode);
            String result = installStatusToString(returnCode);
            mInstallListener.oneAppInstallFinish(mFile.getName(), result);
        }
    }


    public interface InstallListener {
        public void oneAppInstalling(String appPath);

        public void oneAppInstallFinish(String apkName, String result);

        public void allAppInstallFinish();
    }

    public static String installStatusToString(int status) {
        switch (status) {
            case INSTALL_SUCCEEDED:
                return "安装成功";
            case INSTALL_FAILED_ALREADY_EXISTS:
                return "安装失败，应用已存在";
            case INSTALL_FAILED_INVALID_APK:
                return "INSTALL_FAILED_INVALID_APK";
            case INSTALL_FAILED_INVALID_URI:
                return "INSTALL_FAILED_INVALID_URI";
            case INSTALL_FAILED_INSUFFICIENT_STORAGE:
                return "安装失败，设备空间不足";
            case INSTALL_FAILED_DUPLICATE_PACKAGE:
                return "INSTALL_FAILED_DUPLICATE_PACKAGE";
            case INSTALL_FAILED_NO_SHARED_USER:
                return "INSTALL_FAILED_NO_SHARED_USER";
            case INSTALL_FAILED_UPDATE_INCOMPATIBLE:
                return "安装失败，应用签名不一致";
            case INSTALL_FAILED_SHARED_USER_INCOMPATIBLE:
                return "INSTALL_FAILED_SHARED_USER_INCOMPATIBLE";
            case INSTALL_FAILED_MISSING_SHARED_LIBRARY:
                return "INSTALL_FAILED_MISSING_SHARED_LIBRARY";
            case INSTALL_FAILED_REPLACE_COULDNT_DELETE:
                return "INSTALL_FAILED_REPLACE_COULDNT_DELETE";
            case INSTALL_FAILED_DEXOPT:
                return "INSTALL_FAILED_DEXOPT";
            case INSTALL_FAILED_OLDER_SDK:
                return "INSTALL_FAILED_OLDER_SDK";
            case INSTALL_FAILED_CONFLICTING_PROVIDER:
                return "INSTALL_FAILED_CONFLICTING_PROVIDER";
            case INSTALL_FAILED_NEWER_SDK:
                return "INSTALL_FAILED_NEWER_SDK";
            case INSTALL_FAILED_TEST_ONLY:
                return "INSTALL_FAILED_TEST_ONLY";
            case INSTALL_FAILED_CPU_ABI_INCOMPATIBLE:
                return "INSTALL_FAILED_CPU_ABI_INCOMPATIBLE";
            case INSTALL_FAILED_MISSING_FEATURE:
                return "INSTALL_FAILED_MISSING_FEATURE";
            case INSTALL_FAILED_CONTAINER_ERROR:
                return "INSTALL_FAILED_CONTAINER_ERROR";
            case INSTALL_FAILED_INVALID_INSTALL_LOCATION:
                return "INSTALL_FAILED_INVALID_INSTALL_LOCATION";
            case INSTALL_FAILED_MEDIA_UNAVAILABLE:
                return "INSTALL_FAILED_MEDIA_UNAVAILABLE";
            case INSTALL_FAILED_VERIFICATION_TIMEOUT:
                return "INSTALL_FAILED_VERIFICATION_TIMEOUT";
            case INSTALL_FAILED_VERIFICATION_FAILURE:
                return "INSTALL_FAILED_VERIFICATION_FAILURE";
            case INSTALL_FAILED_PACKAGE_CHANGED:
                return "INSTALL_FAILED_PACKAGE_CHANGED";
            case INSTALL_FAILED_UID_CHANGED:
                return "安装失败，应用UID发生了改变";
            case INSTALL_FAILED_VERSION_DOWNGRADE:
                return "安装失败，当前设备已经安装了更高版本的应用";
            case INSTALL_PARSE_FAILED_NOT_APK:
                return "INSTALL_PARSE_FAILED_NOT_APK";
            case INSTALL_PARSE_FAILED_BAD_MANIFEST:
                return "INSTALL_PARSE_FAILED_BAD_MANIFEST";
            case INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION:
                return "INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION";
            case INSTALL_PARSE_FAILED_NO_CERTIFICATES:
                return "INSTALL_PARSE_FAILED_NO_CERTIFICATES";
            case INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES:
                return "INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES";
            case INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING:
                return "INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING";
            case INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME:
                return "INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME";
            case INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID:
                return "INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID";
            case INSTALL_PARSE_FAILED_MANIFEST_MALFORMED:
                return "INSTALL_PARSE_FAILED_MANIFEST_MALFORMED";
            case INSTALL_PARSE_FAILED_MANIFEST_EMPTY:
                return "INSTALL_PARSE_FAILED_MANIFEST_EMPTY";
            case INSTALL_FAILED_INTERNAL_ERROR:
                return "INSTALL_FAILED_INTERNAL_ERROR";
            case INSTALL_FAILED_USER_RESTRICTED:
                return "INSTALL_FAILED_USER_RESTRICTED";
            case INSTALL_FAILED_DUPLICATE_PERMISSION:
                return "INSTALL_FAILED_DUPLICATE_PERMISSION";
            case INSTALL_FAILED_NO_MATCHING_ABIS:
                return "INSTALL_FAILED_NO_MATCHING_ABIS";
            case INSTALL_FAILED_ABORTED:
                return "INSTALL_FAILED_ABORTED";
            default:
                return Integer.toString(status);
        }

    }

    public static final int INSTALL_SUCCEEDED = 1;
    public static final int INSTALL_FAILED_ALREADY_EXISTS = -1;
    public static final int INSTALL_FAILED_INVALID_APK = -2;
    public static final int INSTALL_FAILED_INVALID_URI = -3;
    public static final int INSTALL_FAILED_INSUFFICIENT_STORAGE = -4;
    public static final int INSTALL_FAILED_DUPLICATE_PACKAGE = -5;
    public static final int INSTALL_FAILED_NO_SHARED_USER = -6;
    public static final int INSTALL_FAILED_UPDATE_INCOMPATIBLE = -7;
    public static final int INSTALL_FAILED_SHARED_USER_INCOMPATIBLE = -8;
    public static final int INSTALL_FAILED_MISSING_SHARED_LIBRARY = -9;
    public static final int INSTALL_FAILED_REPLACE_COULDNT_DELETE = -10;
    public static final int INSTALL_FAILED_DEXOPT = -11;
    public static final int INSTALL_FAILED_OLDER_SDK = -12;
    public static final int INSTALL_FAILED_CONFLICTING_PROVIDER = -13;
    public static final int INSTALL_FAILED_NEWER_SDK = -14;
    public static final int INSTALL_FAILED_TEST_ONLY = -15;
    public static final int INSTALL_FAILED_CPU_ABI_INCOMPATIBLE = -16;
    public static final int INSTALL_FAILED_MISSING_FEATURE = -17;
    public static final int INSTALL_FAILED_CONTAINER_ERROR = -18;
    public static final int INSTALL_FAILED_INVALID_INSTALL_LOCATION = -19;
    public static final int INSTALL_FAILED_MEDIA_UNAVAILABLE = -20;
    public static final int INSTALL_FAILED_VERIFICATION_TIMEOUT = -21;
    public static final int INSTALL_FAILED_VERIFICATION_FAILURE = -22;
    public static final int INSTALL_FAILED_PACKAGE_CHANGED = -23;
    public static final int INSTALL_FAILED_UID_CHANGED = -24;
    public static final int INSTALL_FAILED_VERSION_DOWNGRADE = -25;
    public static final int INSTALL_PARSE_FAILED_NOT_APK = -100;
    public static final int INSTALL_PARSE_FAILED_BAD_MANIFEST = -101;
    public static final int INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION = -102;
    public static final int INSTALL_PARSE_FAILED_NO_CERTIFICATES = -103;
    public static final int INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES = -104;
    public static final int INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING = -105;
    public static final int INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME = -106;
    public static final int INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID = -107;
    public static final int INSTALL_PARSE_FAILED_MANIFEST_MALFORMED = -108;
    public static final int INSTALL_PARSE_FAILED_MANIFEST_EMPTY = -109;
    public static final int INSTALL_FAILED_INTERNAL_ERROR = -110;
    public static final int INSTALL_FAILED_USER_RESTRICTED = -111;
    public static final int INSTALL_FAILED_DUPLICATE_PERMISSION = -112;
    public static final int INSTALL_FAILED_NO_MATCHING_ABIS = -113;
    public static final int INSTALL_FAILED_ABORTED = -115;
}

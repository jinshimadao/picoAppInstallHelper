package com.pvr.appinstallhelper;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SdCardUtils {
    private static final String TAG = "SdCardUtils";
    private static final String APPS_PATH = "/apk";
    public static final int GET_APK_FROM_SDCARD = 0;
    public static final int GET_APK_FROM_EXTERNAL_STORAGE = 1;
    public static final int GET_APK_FROM_BOTH_PATH = 2;
    public static int GET_APK_PATH = GET_APK_FROM_SDCARD;

    /**
     * sd卡是否可读
     *
     * @return
     */
    public static boolean ifSdcardReadable(String cardPath) {
        if (TextUtils.isEmpty(cardPath)) {
            cardPath = Environment.getExternalStorageDirectory().getPath();
        }
        String state = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            state = Environment.getExternalStorageState(new File(cardPath));
        }
        if (Environment.MEDIA_MOUNTED.equals(state)
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * sd卡是否可写
     *
     * @param cardPath
     * @return
     */
    public static boolean isSdcardWritable(String cardPath) {
        if (TextUtils.isEmpty(cardPath)) {
            cardPath = Environment.getExternalStorageDirectory().getPath();
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            String state = Environment
                    .getExternalStorageState(new File(cardPath));
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取外置SD卡路径
     *
     * @param context
     * @return
     */
    public static String getExternalSdCardPath(Context context) {
        String[] paths = getSdcardPaths(context);
        String path = "";
        if (paths.length < 2) {
            return "";
        } else {
            path = paths[1];
        }
        Log.i(TAG, "getExternalSdCardPath----->" + path);
        return path;
    }

    public static String[] getSdcardPaths(Context context) {
        WeakReference<Context> weakContextRef = new WeakReference<Context>(
                context);
        Context ctx = weakContextRef.get();
        String[] paths = null;
        if (ctx != null) {
            StorageManager stManager = (StorageManager) ctx
                    .getSystemService(Context.STORAGE_SERVICE);
            try {
                Method mtdGetVolumePath = stManager.getClass()
                        .getDeclaredMethod("getVolumePaths");
                paths = (String[]) mtdGetVolumePath.invoke(stManager);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return paths;
    }

    /**
     * 是否有外置SD卡
     *
     * @param context
     * @return
     */
    public static boolean hasExternalSdcard(Context context) {
        String[] paths = getSdcardPaths(context);
        if (paths == null) {
            return false;
        } else {
            return paths.length >= 2;
        }
    }

    /**
     * 获取内置sd卡路径
     *
     * @return
     */
    public static String getSdCardPath() {
        String path = Environment.getExternalStorageDirectory().getPath();
        Log.i(TAG, "getSdCardPath----->" + path);
        return path;
    }

    public static String getApkPath(Context context, String apkName) {
        switch (GET_APK_PATH) {
            case GET_APK_FROM_EXTERNAL_STORAGE:
                Log.i(TAG, "apk 地址为：" + getExternalSdCardPath(context) + APPS_PATH + "/" + apkName);
                return getExternalSdCardPath(context) + APPS_PATH + "/" + apkName;
            case GET_APK_FROM_SDCARD:
                return getSdCardPath() + APPS_PATH + "/" + apkName;
            default:
                return getSdCardPath() + APPS_PATH + "/" + apkName;
        }
    }
    public static String getApkDir(Context context) {
        switch (GET_APK_PATH) {
            case GET_APK_FROM_EXTERNAL_STORAGE:
                Log.i(TAG, "apk 地址为：" + getExternalSdCardPath(context) + APPS_PATH);
                return getExternalSdCardPath(context) + APPS_PATH ;
            case GET_APK_FROM_SDCARD:
                return getSdCardPath() + APPS_PATH;
            default:
                return getSdCardPath() + APPS_PATH;
        }
    }

    public static List<String> getApksPath(Context context) {
        List<String> paths = new ArrayList<String>();
        Log.i(TAG, "GET_APK_PATH----->" + GET_APK_PATH);
        switch (GET_APK_PATH) {
            case GET_APK_FROM_BOTH_PATH:
                addFileToList(paths, getSdCardPath() + APPS_PATH);
                addFileToList(paths, getExternalSdCardPath(context) + APPS_PATH);
                break;
            case GET_APK_FROM_EXTERNAL_STORAGE:
                // addFileToList(paths, getSdCardPath()+APPS_PATH);
                addFileToList(paths, getExternalSdCardPath(context) + APPS_PATH);
                break;
            case GET_APK_FROM_SDCARD:
                addFileToList(paths, getSdCardPath() + APPS_PATH);
                // addFileToList(paths, getExternalSdCardPath(context)+APPS_PATH);
                break;
            default:
                addFileToList(paths, getSdCardPath() + APPS_PATH);
                addFileToList(paths, getExternalSdCardPath(context) + APPS_PATH);
                break;
        }
        Log.i(TAG, "调用getApksPath，List大小为----->" + paths.size());
        return paths;
    }

    private static void addFileToList(List<String> paths, String filePath) {
        if (paths == null) {
            return;
        }
        try {
            File dFile = new File(filePath);
            if (dFile.exists() && dFile.isDirectory()) {
                File[] files = dFile.listFiles();
                for (File file : files) {
                    if (file.getName().endsWith(".apk")) {
                        String path = file.toString();
                        Log.i(TAG, "apk file path is : " + path);
                        paths.add(file.toString());
                    }
                }
            } else {
                boolean mkdirsResult = dFile.mkdirs();
                Log.i(TAG, filePath + "文件夹在设备上不存在，创建，创建结果为***" + mkdirsResult);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

}

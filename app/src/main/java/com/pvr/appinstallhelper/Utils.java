package com.pvr.appinstallhelper;

import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Created by goodman.ye on 2019/8/2.
 */
public class Utils {
    public static String readSystemAvalableSize() {
        File root = Environment.getRootDirectory();
        StatFs sf = new StatFs(root.getPath());
        long availBytes = sf.getAvailableBytes();
        long freeBytes = sf.getFreeBytes();
        Log.i("SizeUtil", "readSystemTotalSize availBytes = " + availBytes + "***freeBytes = " + freeBytes);
        return +availBytes / (1024 * 1024 * 1024) + "GB--" + freeBytes / (1024 * 1024 * 1024) + "GB";
    }

    public static String readSystemTotalSize() {
        File root = Environment.getRootDirectory();
        StatFs sf = new StatFs(root.getPath());
        long availBytes = sf.getTotalBytes();
        Log.i("SizeUtil", "readSystemTotalSize availBytes = " + availBytes);
        return +availBytes / (1024 * 1024 * 1024) + "GB";
    }

    public static String readSDCard() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long availBytes = sf.getAvailableBytes();
            return "可用大小:" + availBytes / (1024 * 1024) + "MB";
        }
        return "";
    }

    public static String getRomTotalSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        //return Formatter.formatFileSize(MainActivity.this, blockSize * totalBlocks);
        float mTotalSize = (float) (blockSize * totalBlocks) / (1024 * 1024 * 1024);
        DecimalFormat df = new DecimalFormat("0.0");
        return df.format(mTotalSize) + "GB";
    }

    /**
     * 获得机身可用内存
     *
     * @return
     */
    public static String getRomAvailableSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        //return Formatter.formatFileSize(MainActivity.this, blockSize * availableBlocks);
//        float mAvailableSize = (float)(blockSize * availableBlocks) / (1024 * 1024 * 1024);
//        DecimalFormat df = new DecimalFormat("0.0");
        //return df.format(mAvailableSize) + "GB";
        return getUnit(blockSize * availableBlocks);
    }


    public static long getFolderSize(java.io.File file) {

        long size = 0;
        try {
            java.io.File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);

                } else {
                    size = size + fileList[i].length();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    public static String[] units = {"B", "KB", "MB", "GB", "TB"};

    public static String getUnit(float size) {
        int index = 0;
        while (size > 1024 && index < 4) {
            size = size / 1024;
            index++;
        }
        return String.format(Locale.getDefault(), " %.2f %s", size, units[index]);
    }
}

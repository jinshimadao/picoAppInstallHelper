package com.pvr.appinstallhelper;

import android.graphics.drawable.Drawable;

/**
 * Created by goodman.ye on 2019/8/8.
 */
public class APKInfo {
    public Drawable APKIcon;
    public String APKName;
    public boolean mInstalling;
    public String mInstallResult;

    public APKInfo(Drawable APKIcon, String APKName) {
        this.APKIcon = APKIcon;
        this.APKName = APKName;
        mInstalling = true;
        mInstallResult = "";
    }

    public APKInfo(Drawable APKIcon, String APKName, Boolean installing) {
        this.APKIcon = APKIcon;
        this.APKName = APKName;
        mInstalling = installing;
        mInstallResult = "";
    }

    public Drawable getAPKIcon() {
        return APKIcon;
    }

    public void setAPKIcon(Drawable APKIcon) {
        this.APKIcon = APKIcon;
    }

    public String getAPKName() {
        return APKName;
    }

    public void setAPKName(String APKName) {
        this.APKName = APKName;
    }

    public boolean ismInstalling() {
        return mInstalling;
    }

    public void setmInstalling(boolean mInstalling) {
        this.mInstalling = mInstalling;
    }

    public String getmInstallResult() {
        return mInstallResult;
    }

    public void setmInstallResult(String mInstallResult) {
        this.mInstallResult = mInstallResult;
    }
}

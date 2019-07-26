package com.pvr.appinstallhelper;

public class AppInfo {
	
	private String packageName;
	private String activityName;
	private String appName;
	private int versionCode;
	private String versionName;
	private long installTime;
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String mPackageName) {
		this.packageName = mPackageName;
	}
	public String getActivityName() {
		return activityName;
	}
	public void setActivityName(String mActivityName) {
		this.activityName = mActivityName;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String mAppName) {
		this.appName = mAppName;
	}
	public long getInstallTime() {
		return installTime;
	}
	public void setInstallTime(long mInstallTime) {
		this.installTime = mInstallTime;
	}
	public int getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	@Override
	public String toString() {
		return "AppInfo [packageName=" + packageName + ", activityName="
				+ activityName + ", appName=" + appName + ", versionCode="
				+ versionCode + ", versionName=" + versionName
				+ ", installTime=" + installTime + "]";
	}

}

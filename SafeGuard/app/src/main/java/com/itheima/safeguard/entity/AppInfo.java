package com.itheima.safeguard.entity;

import android.graphics.drawable.Drawable;

public class AppInfo {

	private String name;
	private String packageName;
	private Drawable icon;
	private boolean isInstalledOnRom;
	private boolean isInstalledByUser;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public boolean isInstalledOnRom() {
		return isInstalledOnRom;
	}

	public void setInstalledOnRom(boolean isInstalledOnRom) {
		this.isInstalledOnRom = isInstalledOnRom;
	}

	public boolean isInstalledByUser() {
		return isInstalledByUser;
	}

	public void setInstalledByUser(boolean isInstalledByUser) {
		this.isInstalledByUser = isInstalledByUser;
	}
}

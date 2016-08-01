package com.itheima.safeguard.entity;

import android.graphics.drawable.Drawable;

public class TaskInfo {

	private String name;
	private Drawable icon;
	private String packageName;

	private long usedMemory;
	private boolean isUserTask;
	private boolean isChecked;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public long getUsedMemory() {
		return usedMemory;
	}

	public void setUsedMemory(long usedMemory) {
		this.usedMemory = usedMemory;
	}

	public boolean isUserTask() {
		return isUserTask;
	}

	public void setUserTask(boolean isUserTask) {
		this.isUserTask = isUserTask;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

}

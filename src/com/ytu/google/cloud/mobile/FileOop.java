package com.ytu.google.cloud.mobile;

import android.graphics.drawable.Drawable;

@SuppressWarnings("rawtypes")
public class FileOop implements Comparable{
	
	private String text = "";
	private Drawable icon = null;

	private boolean mSelectable = true;

	public FileOop(String text, Drawable icon) {
		this.text = text;
		this.icon = icon;
	}

	public boolean isSelectable() {
		return mSelectable;
	}

	public void setSelectable(boolean sec) {
		this.mSelectable = sec;
	}

	public String getFileName() {
		return this.text;
	}

	public void setFileName(String name) {
		this.text = name;
	}

	public Drawable getIcon() {
		return this.icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public int compareTo(FileOop file) {
		// TODO Auto-generated method stub
		if (this.text != null) {
			return this.text.compareTo(file.getFileName());
		} else {
			throw new IllegalArgumentException();
		}
   }

	@Override
	public int compareTo(Object another) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}

package com.ytu.google.cloud.mobile;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class ListLayout extends LinearLayout{

	private TextView mText = null;
	private ImageView mIcon = null;

	/* 文件名和图标 */
	public ListLayout(Context context, FileOop file) {
		super(context);
		// TODO Auto-generated constructor stub
		this.setOrientation(HORIZONTAL);

		mIcon = new ImageView(context);
		mIcon.setImageDrawable(file.getIcon());
		mIcon.setPadding(8, 12, 6, 12);
		addView(mIcon, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		mText = new TextView(context);
		mText.setText(file.getFileName());
		mText.setPadding(8, 12, 6, 12);
		mText.setTextSize(18);

		addView(mText, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));

	}
	
	 public void setText (String s){
		 mText.setText(s);
	 }

	 public void setIcon (Drawable d){
		 mIcon.setImageDrawable(d);
	 }
	
}

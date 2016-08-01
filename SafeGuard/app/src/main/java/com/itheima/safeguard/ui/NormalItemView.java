package com.itheima.safeguard.ui;

import com.itheima.safeguard.R;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class NormalItemView extends LinearLayout {

	private TextView tv_title;
	private TextView tv_desc;
	private ImageView iv_icon;
	private String title;
	private String desc;
	private Drawable icon;

	public NormalItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context, attrs);
	}

	public NormalItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context, attrs);
	}

	public NormalItemView(Context context) {
		super(context);
		initView(context, null);
	}

	private void initView(Context context, AttributeSet attrs) {
		View view = View.inflate(context, R.layout.normal_item, this);
		this.tv_title = (TextView) view.findViewById(R.id.tv_title);
		this.tv_desc = (TextView) view.findViewById(R.id.tv_desc);
		this.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.NormalItemView);
		this.title = ta.getString(R.styleable.NormalItemView_title);
		this.desc = ta.getString(R.styleable.NormalItemView_desc);
		this.icon = ta.getDrawable(R.styleable.NormalItemView_icon);
		this.tv_title.setText(this.title);
		this.tv_desc.setText(this.desc);
		this.iv_icon.setImageDrawable(icon);
		ta.recycle();
	}

	public void setTitle(String title) {
		this.tv_title.setText(title);
	}

	public void setDesc(String desc) {
		this.tv_desc.setText(desc);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		tv_title.setEnabled(enabled);
		tv_desc.setEnabled(enabled);
		iv_icon.setEnabled(enabled);
	}

}

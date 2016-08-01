package com.itheima.safeguard.ui;

import com.itheima.safeguard.R;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class CheckBoxItemView extends LinearLayout {

	private TextView tv_title;
	private TextView tv_desc;
	private CheckBox checkbox;
	private String title;
	private String desc;

	public CheckBoxItemView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context, attrs);
	}

	public CheckBoxItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context, attrs);
	}

	public CheckBoxItemView(Context context) {
		super(context);
		initView(context, null);
	}

	private void initView(Context context, AttributeSet attrs) {
		View view = View.inflate(context, R.layout.checkbox_item, this);
		this.tv_title = (TextView) view.findViewById(R.id.tv_title);
		this.tv_desc = (TextView) view.findViewById(R.id.tv_desc);
		this.checkbox = (CheckBox) view.findViewById(R.id.checkbox);
		this.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CheckBoxItemView.this.checkbox
						.setChecked(!CheckBoxItemView.this.checkbox.isChecked());
			}

		});
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.CheckBoxItemView);
		this.title = ta.getString(R.styleable.CheckBoxItemView_title);
		this.desc = ta.getString(R.styleable.CheckBoxItemView_desc);
		this.tv_title.setText(this.title);
		this.tv_desc.setText(this.desc);
		ta.recycle();

	}

	public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
		this.checkbox.setOnCheckedChangeListener(listener);
	}
	
	public void setChecked(boolean isChecked) {
		this.checkbox.setChecked(isChecked);
	}
}

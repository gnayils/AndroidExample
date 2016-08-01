package com.itheima.safeguard;

import com.itheima.safeguard.data.C;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

public class MobileSecuritySetupBaseActivity extends Activity {

	private GestureDetector detector;
	protected SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = this.getSharedPreferences(C.prefs.NAME, MODE_PRIVATE);
		detector = new GestureDetector(this, new OnGestureListener(){

			@Override
			public boolean onDown(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				if(e1.getX() < e2.getX()) {
					MobileSecuritySetupBaseActivity.this.prev();
					return true;
				} else if(e1.getX() > e2.getX()) {
					MobileSecuritySetupBaseActivity.this.next();
					return true;
				}
				return false;
			}
			
		});
	}
	
	protected void next() {
		
	}
	
	protected void prev() {
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
		
	}
}

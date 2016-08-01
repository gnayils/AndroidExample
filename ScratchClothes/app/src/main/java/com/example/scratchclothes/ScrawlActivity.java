package com.example.scratchclothes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Administrator on 2016/3/20.
 */
public class ScrawlActivity extends Activity {

    private int SCREEN_W;
    private int SCREEN_H;
    private int imagePosition;

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        String bgPath = null;
        String fgPath = null;
        try {
            bgPath = intent.getStringExtra("bgPath");
            fgPath = intent.getStringExtra("fgPath");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bgPath != null && fgPath != null) {
            gameView = new GameView(this, bgPath, fgPath);
        } else {
            imagePosition = intent.getIntExtra("imagePosition", 0);
            gameView = new GameView(this);
        }
        setContentView(gameView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameView.recycleBitmap();
    }

    private class GameView extends View {

        private Bitmap fgBitmap;
        private Bitmap bgBitmap;
        private Canvas canvas;
        private Paint paint;
        private Path path;
        private float x, y;
        private static final float TOUCH_TOLERANCE = 4;

        public GameView(Context context) {
            super(context);
            setFocusable(true);
            setScreenWH();
            setBackGround();
            int drawableId = 0;
            try {
                drawableId = R.mipmap.class.getDeclaredField("pre" + imagePosition).getInt(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            bgBitmap = scaleBitmapFillScreen(BitmapFactory.decodeResource(getResources(), drawableId));
            setFrontgroundBitmap(bgBitmap);
        }

        public GameView(Context context, String bgPath, String fgPath) {
            super(context);
            setFocusable(true);
            setScreenWH();
            setBackGround(context, bgPath);
            bgBitmap = scaleBitmapFillScreen(readBitmap(fgPath));
            setFrontgroundBitmap(bgBitmap);
        }

        private void setScreenWH() {
            DisplayMetrics dm = new DisplayMetrics();
            dm = getResources().getDisplayMetrics();
            SCREEN_W = dm.widthPixels;
            SCREEN_H = dm.heightPixels;
        }

        private Bitmap scaleBitmapFillScreen(Bitmap bm) {
            return Bitmap.createScaledBitmap(bm, SCREEN_W, SCREEN_H, true);
        }

        private void setBackGround() {
            int drawableId = 0;
            try {
                drawableId = R.mipmap.class.getDeclaredField("after" + imagePosition).getInt(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            setBackgroundResource(drawableId);
        }

        private void setBackGround(Context context, String path) {
            Bitmap bm = readBitmap(path);
            BitmapDrawable bd = new BitmapDrawable(context.getResources(), bm);
            setBackgroundDrawable(bd);
        }

        private Bitmap readBitmap(String path) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inSampleSize = 1;
            return BitmapFactory.decodeFile(path, options);
        }

        private void setFrontgroundBitmap(Bitmap bitmap) {
            paint = new Paint();
            paint.setAlpha(0);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            paint.setAntiAlias(true);
            paint.setDither(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeWidth(40);
            path = new Path();
            fgBitmap = Bitmap.createBitmap(SCREEN_W, SCREEN_H, Bitmap.Config.ARGB_8888);
            canvas = new Canvas();
            canvas.setBitmap(fgBitmap);
            canvas.drawBitmap(bitmap, 0, 0, null);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(fgBitmap, 0, 0, null);
            this.canvas.drawPath(path, paint);
            super.onDraw(canvas);
        }

        private void touchStart(float x, float y) {
            path.reset();
            path.moveTo(x, y);
            this.x = x;
            this.y = y;
        }

        private void touchMove(float x, float y) {
            float dx = Math.abs(x- this.x);
            float dy = Math.abs(y - this.y);
            if(dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                path.quadTo(this.x, this.y, (x + this.x) / 2, (y + this.y) / 2);
                this.x = x;
                this.y = y;
            }
        }

        private void touchUp() {
            path.lineTo(this.x, this.y);
            canvas.drawPath(path, paint);
            path.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchStart(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    touchMove(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    touchUp();
                    break;
            }
            invalidate();
            return true;
        }

        public void recycleBitmap() {
            fgBitmap.recycle();
            bgBitmap.recycle();
        }
    }
}

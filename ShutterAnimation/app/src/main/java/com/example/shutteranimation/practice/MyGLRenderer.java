package com.example.shutteranimation.practice;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Administrator on 2015/11/22.
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    private Context mContext;

    private Triangle mTriangle;
    private Square mSquare;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    private final float[] mMTRMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];
    private final float[] mTranslationMatrix = new float[16];

    private final float[] mTempMatrix = new float[16];

    public MyGLRenderer(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0f, 0f, 0f, 1f);

        mTriangle = new Triangle();
        mSquare = new Square(mContext);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;

        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0, 0, 0, 0f, 1f, 0f);

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
    }
    int mMaxBladeDegree = 60;
    int mMinBladeDegree = 0;
    int mCurBladeDegree = 0;
    int mDegreeIncrement = 1;

    @Override
    public void onDrawFrame(GL10 gl) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


//        long time  = SystemClock.uptimeMillis() % 4000L;
//        float angle = 0.09f * ((int) time);
//        Matrix.setRotateM(mRotationMatrix, 0, angle, 0f, 0f, 1f);
//        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
//        mSquare.draw(scratch);

//        Matrix.setRotateM(mRotationMatrix, 0, 360f - angle, 0f, 0f, 1f);
//        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
//        mTriangle.draw(scratch);


//        Matrix.setIdentityM(mRotationMatrix, 0);
//        Matrix.rotateM(mRotationMatrix, 0, 60, 0f, 0f, 1f);
//
//        Matrix.setIdentityM(mTranslationMatrix, 0);
//        Matrix.translateM(mTranslationMatrix, 0, 0f, 0.5f, 0f);
//
//        Matrix.multiplyMM(mMTRMatrix, 0, mTranslationMatrix, 0, mRotationMatrix, 0);
//
//        Matrix.multiplyMM(mTempMatrix, 0, mMVPMatrix, 0, mMTRMatrix, 0);
//        mSquare.draw(mTempMatrix);

        if(mCurBladeDegree > mMaxBladeDegree) {
            mDegreeIncrement = 0 - mDegreeIncrement;
        } else if(mCurBladeDegree < mMinBladeDegree) {
            mDegreeIncrement = 0 - mDegreeIncrement;
        }
        for (int i = 0; i < 10; i++) {
            Matrix.setIdentityM(mRotationMatrix, 0);
            Matrix.rotateM(mRotationMatrix, 0, -(36 * i), 0f, 0f, 1f);
            Matrix.setIdentityM(mTranslationMatrix, 0);
            Matrix.translateM(mTranslationMatrix, 0, 0.01f, 0.5f, 0f);

            float[] tempMTRMatrix = new float[16];
            Matrix.multiplyMM(tempMTRMatrix, 0, mRotationMatrix, 0, mTranslationMatrix, 0);

            Matrix.setIdentityM(mRotationMatrix, 0);
            Matrix.rotateM(mRotationMatrix, 0, mCurBladeDegree, 0f, 0f, 1f);
            Matrix.multiplyMM(mMTRMatrix, 0,tempMTRMatrix , 0,mRotationMatrix , 0);

            Matrix.multiplyMM(mTempMatrix, 0, mMVPMatrix, 0, mMTRMatrix, 0);
            mSquare.draw(mTempMatrix);

        }
        mCurBladeDegree += mDegreeIncrement;

    }

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}

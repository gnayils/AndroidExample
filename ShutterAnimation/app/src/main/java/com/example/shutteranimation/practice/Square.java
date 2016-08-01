package com.example.shutteranimation.practice;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.example.shutteranimation.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Administrator on 2015/11/22.
 */
public class Square {

    private final String mVertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 aStructureVertex;" +
                    "attribute vec2 aTextureVertex;" +
                    "varying   vec2 vTextureVertex;" +
                    "void main() {" +
                    "   gl_Position = uMVPMatrix * aStructureVertex;" +
                    "   vTextureVertex = aTextureVertex;" +
                    "}";
    private final String mFragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4      uColor;" +
                    "varying vec2      vTextureVertex;" +
                    "uniform sampler2D uTexture;" +
                    "void main() {" +
                    //"   gl_FragColor = uColor;" +
                    "   gl_FragColor = texture2D(uTexture, vTextureVertex);" +
                    "}";

    private final float mSquareCoords[] = {
            -0.4f, 0.5f, 0.0f,
            0f, 1f,
            -0.4f, -0.5f, 0.0f,
            0f, 0f,
            0.4f, -0.5f, 0.0f,
            1f, 0f,
            0.4f, 0.5f, 0.0f,
            1f, 1f
    };
    private final int mCoordsPerVertexOfStructure = 3;
    private final int mCoordsPerVertexOfTexture = 2;
    private final int mVertexStride = (mCoordsPerVertexOfStructure + mCoordsPerVertexOfTexture) * 4;


    private final FloatBuffer mVertexBuffer;
    private final short[] mDrawOrder = {0, 1, 2, 0, 2, 3};
    private final ShortBuffer mDrawOrderBuffer;

    private final float[] mColor = {0.2f, 0.709803922f, 0.898039216f, 1.0f};

    private final int mProgram;

    private int mMVPMatrixHandle;
    private int mVertexPositionHandle;
    private int mTexturePositionHandle;

    private int mColorHandle;
    private int mTextureHandle;

    private int[] mTextureId = new int[1];

    public Square(Context context) {
        mVertexBuffer = ByteBuffer.allocateDirect(mSquareCoords.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(mSquareCoords);
        mVertexBuffer.position(0);
        mDrawOrderBuffer = ByteBuffer.allocateDirect(mDrawOrder.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer().put(mDrawOrder);
        mDrawOrderBuffer.position(0);

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, mVertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, mFragmentShaderCode);
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);

        Bitmap textureBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.iris_shutter);
        GLES20.glGenTextures(1, mTextureId, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, textureBitmap, 0);
        textureBitmap.recycle();
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(mProgram);

        //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId[0]);

        mVertexPositionHandle = GLES20.glGetAttribLocation(mProgram, "aStructureVertex");
        GLES20.glEnableVertexAttribArray(mVertexPositionHandle);
        mVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(mVertexPositionHandle, mCoordsPerVertexOfStructure, GLES20.GL_FLOAT, false, mVertexStride, mVertexBuffer);

        mTexturePositionHandle = GLES20.glGetAttribLocation(mProgram, "aTextureVertex");
        GLES20.glEnableVertexAttribArray(mTexturePositionHandle);
        mVertexBuffer.position(3);
        GLES20.glVertexAttribPointer(mTexturePositionHandle, mCoordsPerVertexOfTexture, GLES20.GL_FLOAT, false, mVertexStride, mVertexBuffer);


        mTextureHandle = GLES20.glGetUniformLocation(mProgram, "uTexture");
        GLES20.glUniform1i(mTextureHandle, 0);

//        mColorHandle = GLES20.glGetUniformLocation(mProgram, "uColor");
//        GLES20.glUniform4fv(mColorHandle, 1, mColor, 0);


        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mDrawOrder.length, GLES20.GL_UNSIGNED_SHORT, mDrawOrderBuffer);

        GLES20.glDisableVertexAttribArray(mVertexPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexturePositionHandle);
    }
}

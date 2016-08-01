package com.gnayils.example;

import android.content.Context;
import android.opengl.GLES20;


import com.gnayils.example.R;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class RawImage {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 1000;
    private static final ByteBuffer IMAGE_DATA_BUFFER = ByteBuffer.allocate(WIDTH * HEIGHT);

    private static final String VERTEX_SHADER_CODE =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 aStructureVertex;" +
                    "attribute vec2 aTextureVertex;" +
                    "varying   vec2 vTextureVertex;" +
                    "void main() {" +
                    "   gl_Position = uMVPMatrix * aStructureVertex;" +
                    "   vTextureVertex = aTextureVertex;" +
                    "}";
    private static final String FRAGMENT_SHADER_CODE =
            "precision mediump float;" +
                    "varying vec2      vTextureVertex;" +
                    "uniform sampler2D uTexture;" +
                    "void main() {" +
                    "   gl_FragColor = texture2D(uTexture, vTextureVertex);" +
                    "}";

    private static final float COORDINATES[] = {
            -1f, 1f, 0.0f,
            0f, 1f,
            -1f, -1f, 0.0f,
            0f, 0f,
            1f, -1f, 0.0f,
            1f, 0f,
            1f, 1f, 0.0f,
            1f, 1f
    };
    private static final int COORDINATES_PER_VERTEX_OF_STRUCTURE = 3;
    private static final int COORDINATES_PER_VERTEX_OF_TEXTURE = 2;
    private static final int VERTEX_STRIDE = (COORDINATES_PER_VERTEX_OF_STRUCTURE + COORDINATES_PER_VERTEX_OF_TEXTURE) * 4;

    private final FloatBuffer mVertexBuffer;
    private final short[] mDrawOrder = {0, 1, 2, 0, 2, 3};
    private final ShortBuffer mDrawOrderBuffer;

    private final int mProgram;
    private int mMVPMatrixHandle;
    private int mVertexPositionHandle;
    private int mTexturePositionHandle;
    private int mTextureHandle;

    private int[] mTextureId = new int[1];

    public RawImage(Context context) {
        mVertexBuffer = ByteBuffer.allocateDirect(COORDINATES.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(COORDINATES);
        mVertexBuffer.position(0);
        mDrawOrderBuffer = ByteBuffer.allocateDirect(mDrawOrder.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer().put(mDrawOrder);
        mDrawOrderBuffer.position(0);

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_CODE);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE);
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);

        readImageData(context);
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(mProgram);

        mVertexPositionHandle = GLES20.glGetAttribLocation(mProgram, "aStructureVertex");
        GLES20.glEnableVertexAttribArray(mVertexPositionHandle);
        mVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(mVertexPositionHandle, COORDINATES_PER_VERTEX_OF_STRUCTURE, GLES20.GL_FLOAT, false, VERTEX_STRIDE, mVertexBuffer);

        mTexturePositionHandle = GLES20.glGetAttribLocation(mProgram, "aTextureVertex");
        GLES20.glEnableVertexAttribArray(mTexturePositionHandle);
        mVertexBuffer.position(3);
        GLES20.glVertexAttribPointer(mTexturePositionHandle, COORDINATES_PER_VERTEX_OF_TEXTURE, GLES20.GL_FLOAT, false, VERTEX_STRIDE, mVertexBuffer);

        mTextureHandle = GLES20.glGetUniformLocation(mProgram, "uTexture");
        GLES20.glUniform1i(mTextureHandle, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, WIDTH, HEIGHT, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, IMAGE_DATA_BUFFER);

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mDrawOrder.length, GLES20.GL_UNSIGNED_SHORT, mDrawOrderBuffer);

        GLES20.glDisableVertexAttribArray(mVertexPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexturePositionHandle);
    }

    private void readImageData(Context context) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.image);
        byte[] buffer = new byte[1024];
        int readLength = 0;
        try {
            while((readLength = inputStream.read(buffer)) > 0) {
                IMAGE_DATA_BUFFER.put(buffer, 0, readLength);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        IMAGE_DATA_BUFFER.position(0);
    }
}

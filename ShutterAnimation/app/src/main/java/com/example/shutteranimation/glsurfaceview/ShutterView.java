package com.example.shutteranimation.glsurfaceview;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Administrator on 2015/11/21.
 */
public class ShutterView extends GLSurfaceView {

    private final Renderer mRenderer;

    public ShutterView(Context context) {
        super(context);
        setFocusableInTouchMode(true);
        setEGLContextClientVersion(2);
        mRenderer = new ShutterRenderer(context);
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);// Create the shader object
        if (shader == 0) {
            throw new RuntimeException("Error create shader.");
        }
        int[] compiled = new int[1];
        GLES20.glShaderSource(shader, shaderCode);// Load the shader source
        GLES20.glCompileShader(shader);// Compile the shader
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);// Check the compile status
        if (compiled[0] == 0) {
            GLES20.glDeleteShader(shader);
            throw new RuntimeException("Error compile shader: " + GLES20.glGetShaderInfoLog(shader));
        }
        return shader;
    }
}

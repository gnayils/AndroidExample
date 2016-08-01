package com.example.shutteranimation.glsurfaceview;

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
 * Created by Administrator on 2015/11/21.
 */
public class Blade {

    private final int mProgram;
    private FloatBuffer mVertex;
    private ShortBuffer mIndex;
    private float[] mQuadVertex = new float[] {
            -0.5f, 0.5f, 0.0f, // Position 0
            0, 1.0f, // TexCoord 0
            -0.5f, -0.5f, 0.0f, // Position 1
            0, 0, // TexCoord 1
            0.5f , -0.5f, 0.0f, // Position 2
            1.0f, 0, // TexCoord 2
            0.5f, 0.5f, 0.0f, // Position 3
            1.0f, 1.0f, // TexCoord 3
    };
    private short[] mQuadIndex = new short[] {
            0, // Position 0
            1, // Position 1
            2, // Position 2
            0, // Position 2
            2, // Position 3
            3, // Position 0
    };
    private final String mVertexShaderCode =
            "uniform mat4 u_MVPMatrix;" +
                    "attribute vec4 a_position;" +
                    "attribute vec2 a_texCoord;" +
                    "varying vec2 v_texCoord;" +
                    "void main()" +
                    "{" +
                    "gl_Position = a_position;" +
                    "v_texCoord = a_texCoord;" +
                    "}";
    private final String mFragmentShaderCode =
            "precision lowp float;" +
                    "varying vec2 v_texCoord;" +
                    "uniform sampler2D u_samplerTexture;" +
                    "void main()" +
                    "{" +
                    "gl_FragColor = texture2D(u_samplerTexture, v_texCoord);" +
                    "}";
    private final int mAttribPosition;
    private final int mAttribTexCoord;;
    private final int mUniformTexture;
    private final int[] mTextureAttrs = new int[3];

    private static final int TEXTURE_ID = 0;
    private static final int TEXTURE_WIDTH = 1;
    private static final int TEXTURE_HEIGHT = 2;

    public Blade(Context context) {
        this.mVertex = ByteBuffer.allocateDirect(mQuadVertex.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.mVertex.put(mQuadVertex).position(0);
        this.mIndex = ByteBuffer.allocateDirect(mQuadIndex.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
        this.mIndex.put(mQuadIndex).position(0);

        int vertexShader = ShutterView.loadShader(GLES20.GL_VERTEX_SHADER, mVertexShaderCode);
        int fragmentShader = ShutterView.loadShader(GLES20.GL_FRAGMENT_SHADER, mFragmentShaderCode);
        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the mVertex shader to mProgram
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to mProgram
        GLES20.glLinkProgram(mProgram);                  // Link the mProgram
        int[] linked = new int[1];
        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linked, 0);// Check the link status
        if (linked[0] == 0) {
            GLES20.glDeleteProgram(mProgram);
            throw new RuntimeException("Error linking mProgram: " + GLES20.glGetProgramInfoLog(mProgram));
        }
        // Free up no longer needed shader resources
        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);

        mAttribPosition = GLES20.glGetAttribLocation(mProgram, "a_position");
        mAttribTexCoord = GLES20.glGetAttribLocation(mProgram, "a_texCoord");
        mUniformTexture = GLES20.glGetUniformLocation(mProgram, "u_samplerTexture");
        GLES20.glUseProgram(mProgram);
        GLES20.glEnableVertexAttribArray(mAttribPosition);
        GLES20.glEnableVertexAttribArray(mAttribTexCoord);
        GLES20.glUniform1i(mUniformTexture, 0);           // Set the sampler to texture unit 0


        int[] textureId = new int[1];
        GLES20.glGenTextures(1, textureId, 0);// Generate a texture object
        int[] result = null;
        if (textureId[0] != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.iris_shutter);
            mTextureAttrs[TEXTURE_ID] = textureId[0]; // TEXTURE_ID
            mTextureAttrs[TEXTURE_WIDTH] = bitmap.getWidth(); // TEXTURE_WIDTH
            mTextureAttrs[TEXTURE_HEIGHT] = bitmap.getHeight(); // TEXTURE_HEIGHT
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);// Bind to the texture in OpenGL
            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);// Load the bitmap into the bound texture.
            bitmap.recycle();// Recycle the bitmap, since its data has been loaded into OpenGL.
        } else {
            throw new RuntimeException("Error loading texture.");
        }
    }


    public void draw(float[] mvpMatrix) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureAttrs[TEXTURE_ID]);
        mVertex.position(0);
        // load the position
        // 3(x , y , z)
        // (2 + 3 )* 4 (float size) = 20
        GLES20.glVertexAttribPointer(mAttribPosition, 3, GLES20.GL_FLOAT, false, 20, mVertex);
        mVertex.position(3);
        // load the texture coordinate
        GLES20.glVertexAttribPointer(mAttribTexCoord, 2, GLES20.GL_FLOAT, false, 20, mVertex);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mIndex);
    }
}

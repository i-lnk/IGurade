package com.rl.geye.util;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;

import com.orhanobut.logger.Logger;
import com.rl.commons.BaseApp;
import com.rl.geye.R;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class MyRenderV2 implements Renderer {
    private final static int SIZE_OF_FLOAT = 4;
    final float[] positionBufferData;
    int mHeight = 0;
    ByteBuffer mUByteBuffer = null;
    ByteBuffer mVByteBuffer = null;
    int mWidth = 0;
    ByteBuffer mYByteBuffer = null;
    FloatBuffer positionBuffer = null;
    int positionSlot = 0;
    int programHandle = 0;
    int texRangeSlot = 0;
    int[] texture = new int[3];
    int[] textureSlot = new int[3];
    int vertexShader = 0;
    int yuvFragmentShader = 0;
    byte[] yuvData = null;
    float[] textCoodBufferData;
    FloatBuffer textCoodBuffer = null;
    boolean bNeedSleep = true;
    private float mRatio = 3.0f / 4.0f;


    public MyRenderV2(GLSurfaceView paramGLSurfaceView) {


		/*float[] arrayOfFloat1 = new float[16];
        float test = 1f;
		if( devtype == Constants.DeviceType.IP03 || devtype == Constants.DeviceType.IP04A ){
			arrayOfFloat1[0] = 1.0F*test;
			arrayOfFloat1[1] = 1.0F*test;
			arrayOfFloat1[2] = 0.0F*test;
			arrayOfFloat1[3] = 1.0F*test;

			arrayOfFloat1[4] = 1.0F*test;
			arrayOfFloat1[5] = 0.0F*test;
			arrayOfFloat1[6] = 0.0F*test;
			arrayOfFloat1[7] = 1.0F*test;

			arrayOfFloat1[8] = 0.0F*test;
			arrayOfFloat1[9] = 1.0F*test;
			arrayOfFloat1[10] = 0.0F*test;
			arrayOfFloat1[11] = 1.0F*test;

			arrayOfFloat1[12] = 0.0F*test;
			arrayOfFloat1[13] = 0.0F*test;
			arrayOfFloat1[14] = 0.0F*test;
			arrayOfFloat1[15] = 1.0F*test;
		}else{
			arrayOfFloat1[0] = 0.0F*test;
			arrayOfFloat1[1] = 0.0F*test;
			arrayOfFloat1[2] = 0.0F*test;
			arrayOfFloat1[3] = 1.0F*test;

			arrayOfFloat1[4] = 0.0F*test;
			arrayOfFloat1[5] = 1.0F*test;
			arrayOfFloat1[6] = 0.0F*test;
			arrayOfFloat1[7] = 1.0F*test;

			arrayOfFloat1[8] = 1.0F*test;
			arrayOfFloat1[9] = 0.0F*test;
			arrayOfFloat1[10] = 0.0F*test;
			arrayOfFloat1[11] = 1.0F*test;

			arrayOfFloat1[12] = 1.0F*test;
			arrayOfFloat1[13] = 1.0F*test;
			arrayOfFloat1[14] = 0.0F*test;
			arrayOfFloat1[15] = 1.0F*test;
		}
		this.textCoodBufferData = arrayOfFloat1;

		float[] arrayOfFloat = new float[16];

		arrayOfFloat[0] = -1.0F;
		arrayOfFloat[1] = 1.0F;
		arrayOfFloat[2] = 0.0F;
		arrayOfFloat[3] = 1.0F;

		arrayOfFloat[4] = -1.0F;
		arrayOfFloat[5] = -1.0F;
		arrayOfFloat[6] = 0.0F;
		arrayOfFloat[7] = 1.0F;

		arrayOfFloat[8] = 1.0F;
		arrayOfFloat[9] = 1.0F;
		arrayOfFloat[10] = 0.0F;
		arrayOfFloat[11] = 1.0F;

		arrayOfFloat[12] = 1.0F;
		arrayOfFloat[13] = -1.0F;
		arrayOfFloat[14] = 0.0F;
		arrayOfFloat[15] = 1.0F;

		this.positionBufferData = arrayOfFloat;*/


        float ratio = 1;

        float start = (1.0f - ratio) / 2.0f;
        float end = 1 - start;

        float[] coordVertices = {
                start, end, // top left (V2)
                end, end, // top right (V4)
                start, start, // bottom left (V1)
                end, start  // bottom right (V3)
        };

//		//翻转
//		float[] specialCoordVertices = {
//				end, start,  // bottom right (V3)
//				start, start, // bottom left (V1)
//				end, end, // top right (V4)
//				start, end // top left (V2)
//		};


        float[] squareVertices = {
                -1.0f, -1.0f, // V1 - bottom left
                1.0f, -1.0f, // V3 - bottom right
                -1.0f, 1.0f, // V2 - top left
                1.0f, 1.0f // V4 - top right
        };

        this.textCoodBufferData = coordVertices;

        this.positionBufferData = squareVertices;

        paramGLSurfaceView.setEGLContextClientVersion(2);

    }

//	public static int compileShader(String paramString, int paramInt) {
//		int i = GLES20.glCreateShader(paramInt);
//		if (i != 0) {
//			int[] arrayOfInt = new int[1];
//			GLES20.glShaderSource(i, paramString);
//			GLES20.glCompileShader(i);
//			GLES20.glGetShaderiv(i, GLES20.GL_COMPILE_STATUS, arrayOfInt, 0);
//			if (arrayOfInt[0] == 0) {
//				XLog.e("compileShader",
//						"compile shader err:" + GLES20.glGetProgramInfoLog(i));
//				GLES20.glDeleteShader(i);
//				i = 0;
//			}
//		}
//		return i;
//	}

    public long createShaders() {

        String vertexShaderCode = TextResourceReader.readTextFileFromResource(BaseApp.context(), R.raw.shader_vertex);
        String fragmentShaderCode = TextResourceReader.readTextFileFromResource(BaseApp.context(), R.raw.shader_fragment);

//		int i = compileShader(vertexShaderCode, GLES20.GL_VERTEX_SHADER);
        this.vertexShader = GlUtil.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        if (vertexShader == 0) {
            Logger.e("createShaders failed when compileShader(vertex)");
            return 0;
        }

//		int j = compileShader(fragmentShaderCode, GLES20.GL_FRAGMENT_SHADER);
        this.yuvFragmentShader = GlUtil.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        if (yuvFragmentShader == 0) {
            Logger.e("failed when compileShader(fragment)");
            return 0;
        }

        this.programHandle = GLES20.glCreateProgram();// create empty OpenGL ES Program
        GlUtil.checkGlError("glCreateProgram");
        if (programHandle == 0) {
            Logger.e("Could not create program");
            return 0;
        }
        GLES20.glAttachShader(this.programHandle, this.vertexShader);// add the vertex shader to program
        GlUtil.checkGlError("glAttachShader");
        GLES20.glAttachShader(this.programHandle, this.yuvFragmentShader);// add the fragment shader to program
        GlUtil.checkGlError("glAttachShader");
        GLES20.glLinkProgram(this.programHandle);// creates OpenGL ES program executables
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(this.programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

        if (linkStatus[0] != GLES20.GL_TRUE) {
            Logger.e("link program err:" + GLES20.glGetProgramInfoLog(this.programHandle));
            destroyShaders();
            return 0;
        }

        // 获取指向vertex shader的成员句柄
        this.texRangeSlot = GLES20.glGetAttribLocation(this.programHandle, "myTexCoord");
        GlUtil.checkLocation(texRangeSlot, "myTexCoord");
        this.textureSlot[0] = GLES20.glGetUniformLocation(this.programHandle, "Ytex");
        GlUtil.checkLocation(textureSlot[0], "Ytex");
        this.textureSlot[1] = GLES20.glGetUniformLocation(this.programHandle, "Utex");
        GlUtil.checkLocation(textureSlot[1], "Utex");
        this.textureSlot[2] = GLES20.glGetUniformLocation(this.programHandle, "Vtex");
        GlUtil.checkLocation(textureSlot[2], "Vtex");
        this.positionSlot = GLES20.glGetAttribLocation(this.programHandle, "vPosition");
        GlUtil.checkLocation(positionSlot, "vPosition");

        return 0;
    }

    public long destroyShaders() {
        if (this.programHandle != 0) {
            GLES20.glDetachShader(this.programHandle, this.yuvFragmentShader);
            GLES20.glDetachShader(this.programHandle, this.vertexShader);
            GLES20.glDeleteProgram(this.programHandle);
            this.programHandle = 0;
        }
        if (this.yuvFragmentShader != 0) {
            GLES20.glDeleteShader(this.yuvFragmentShader);
            this.yuvFragmentShader = 0;
        }
        if (this.vertexShader != 0) {
            GLES20.glDeleteShader(this.vertexShader);
            this.vertexShader = 0;
        }
        return 0L;
    }

    public int draw(ByteBuffer yByteBuffer, ByteBuffer uByteBuffer,
                    ByteBuffer vBteBuffer, int width, int height) {

        // 清除屏幕和深度缓存
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);// | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);

        GLES20.glUseProgram(this.programHandle);
        yByteBuffer.position(0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        loadTexture(this.texture[0], width, height, yByteBuffer);
        uByteBuffer.position(0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        loadTexture(this.texture[1], width >> 1, height >> 1, uByteBuffer);
        vBteBuffer.position(0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        loadTexture(this.texture[2], width >> 1, height >> 1, vBteBuffer);
        GLES20.glUniform1i(this.textureSlot[0], 0);
        GLES20.glUniform1i(this.textureSlot[1], 1);
        GLES20.glUniform1i(this.textureSlot[2], 2);

        this.positionBuffer.position(0);
        GLES20.glEnableVertexAttribArray(this.positionSlot);
        GLES20.glVertexAttribPointer(this.positionSlot, 2, GLES20.GL_FLOAT, false, 2 * SIZE_OF_FLOAT, this.positionBuffer);

        // textcood
        this.textCoodBuffer.position(0);

        GLES20.glEnableVertexAttribArray(this.texRangeSlot);
        GLES20.glVertexAttribPointer(this.texRangeSlot, 2, GLES20.GL_FLOAT, false, 2 * SIZE_OF_FLOAT, this.textCoodBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(this.positionSlot);

        GLES20.glDisableVertexAttribArray(this.texRangeSlot);

        return 0;
    }

    public int loadTexture(int paramInt1, int width, int height, Buffer paramBuffer) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, paramInt1);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, width, height, 0, GLES20.GL_LUMINANCE,
                GLES20.GL_UNSIGNED_BYTE, paramBuffer);

//		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, format, width, height, 0, format,
//				GLES20.GL_UNSIGNED_BYTE, data);

        return 0;
    }

    public int loadVBOs() {
        this.textCoodBuffer = ByteBuffer
                .allocateDirect(4 * this.textCoodBufferData.length)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.textCoodBuffer.put(this.textCoodBufferData).position(0);

        this.positionBuffer = ByteBuffer
                .allocateDirect(4 * this.positionBufferData.length)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.positionBuffer.put(this.positionBufferData).position(0);
        return 0;
    }

    public void onDrawFrame(GL10 paramGL10) {
        // Log.d("opengl1_yuvRender", "onDrawFrame");
        // 清除屏幕和深度缓存
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);// | GLES20.GL_DEPTH_BUFFER_BIT);

        synchronized (this) {
            if ((this.mWidth == 0)
                    || (this.mHeight == 0)
                    || (this.mYByteBuffer == null)
                    || (this.mUByteBuffer == null)
                    || (this.mVByteBuffer == null)) {
                return;
            }

            if (bNeedSleep) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            bNeedSleep = true;
            // Log.e("tag", "opengl1_mWidth:" + mWidth + "  mHeight:" +
            // mHeight);
            draw(this.mYByteBuffer, this.mUByteBuffer, this.mVByteBuffer,
                    this.mWidth, this.mHeight);
        }

    }

    public void onSurfaceChanged(GL10 paramGL10, int paramInt1, int paramInt2) {
//		XLog.e("Render","#####################################################");
//		XLog.e("Render","########################### onSurfaceChanged ##########################");
//		XLog.e("Render","#####################################################");
        GLES20.glViewport(0, 0, paramInt1, paramInt2);
    }

    public void onSurfaceCreated(GL10 paramGL10, EGLConfig paramEGLConfig) {
//		XLog.e("Render","#####################################################");
//		XLog.e("Render","########################### onSurfaceCreated ##########################");
//		XLog.e("Render","#####################################################");

        // 设置背景色
        GLES20.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
//		// 阴影平滑
//		paramGL10.glShadeModel(GL_SMOOTH);
//		// 设置深度值
//		GLES20.glClearDepthf(1.0f);


        GLES20.glGenTextures(3, this.texture, 0);
        try {
            createShaders();
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadVBOs();
    }

    public int unloadVBOs() {
        if (this.positionBuffer != null)
            this.positionBuffer = null;
        if (this.textCoodBuffer != null) {
            this.textCoodBuffer = null;
        }
        return 0;
    }

    public int writeSample(byte[] paramArrayOfByte, int width, int height) {
        float ratio = ((float) height) / ((float) width);
        if (mRatio != ratio) {
            mRatio = ratio;
//			boolean isSpecialDev = (mDevtype == Constants.DeviceType.IP03 || mDevtype == Constants.DeviceType.IP04A);
//			ratio = ((float) height *4.0f)/(3.0f*width);
//			if( ratio < 1.0f  ){
//				//宽度截取
//				float start = (1.0f- ratio)/2.0f;
//				float end = 1 - start;
//				float[] coordVertices = {
//						start, 1, // top left (V2)
//						end, 1, // top right (V4)
//						start, 0, // bottom left (V1)
//						end, 0  // bottom right (V3)
//				};
//
//				//翻转
//				float[] specialCoordVertices = {
//						end, 0,  // bottom right (V3)
//						start, 0, // bottom left (V1)
//						end, 1, // top right (V4)
//						start, 1 // top left (V2)
//				};
//				this.textCoodBufferData = isSpecialDev?specialCoordVertices:coordVertices;
//			}else{
//				//高度截取
//				ratio = ((float) width *3.0f)/(4.0f*height);
//				float start = (1.0f- ratio)/2.0f;
//				float end = 1 - start;
//				float[] coordVertices = {
//						0, end, // top left (V2)
//						1, end, // top right (V4)
//						0, start, // bottom left (V1)
//						1, start  // bottom right (V3)
//				};
//
//				//翻转
//				float[] specialCoordVertices = {
//						1, start,  // bottom right (V3)
//						0, start, // bottom left (V1)
//						1, end, // top right (V4)
//						0, end // top left (V2)
//				};
//				this.textCoodBufferData = isSpecialDev?specialCoordVertices:coordVertices;
//			}


        }


        synchronized (this) {
            if ((width == 0) || (height == 0)) {
                Logger.e("writesample invalid param");
                return 0;
            }
            if ((width != this.mWidth) || (height != this.mHeight)) {
                this.mWidth = width;
                this.mHeight = height;
                this.mYByteBuffer = ByteBuffer.allocate(this.mWidth * this.mHeight);
                this.mUByteBuffer = ByteBuffer.allocate(this.mWidth * this.mHeight / 4);
                this.mVByteBuffer = ByteBuffer.allocate(this.mWidth * this.mHeight / 4);
            }

            if (this.mYByteBuffer != null) {
                this.mYByteBuffer.position(0);
                this.mYByteBuffer.put(paramArrayOfByte, 0, this.mWidth * this.mHeight);
                this.mYByteBuffer.position(0);
            }

            if (this.mUByteBuffer != null) {
                this.mUByteBuffer.position(0);
                this.mUByteBuffer.put(paramArrayOfByte, this.mWidth * this.mHeight, this.mWidth * this.mHeight / 4);
                this.mUByteBuffer.position(0);
            }

            if (this.mVByteBuffer != null) {
                this.mVByteBuffer.position(0);
                this.mVByteBuffer.put(paramArrayOfByte, 5 * (this.mWidth * this.mHeight) / 4, this.mWidth * this.mHeight / 4);
                this.mVByteBuffer.position(0);
            }

            bNeedSleep = false;

            return 1;

        }
    }

}
package com.ljr.opengl_a;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Cube {
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;"+
                    "varying  vec4 vColor;"+
                    "attribute vec4 aColor;"+
                    "void main() {" +
                    "  gl_Position = vMatrix*vPosition;" +
                    "  vColor=aColor;"+
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private int mProgram;
    final float cubePositions[] = {
            -1.0f,1.0f,1.0f,    //正面左上0
            -1.0f,-1.0f,1.0f,   //正面左下1
            1.0f,-1.0f,1.0f,    //正面右下2
            1.0f,1.0f,1.0f,     //正面右上3
            -1.0f,1.0f,-1.0f,    //反面左上4
            -1.0f,-1.0f,-1.0f,   //反面左下5
            1.0f,-1.0f,-1.0f,    //反面右下6
            1.0f,1.0f,-1.0f,     //反面右上7
    };
    final short index[]={
            6,7,4,6,4,5,    //后面
            6,3,7,6,2,3,    //右面
            6,5,1,6,1,2,    //下面
            0,3,2,0,2,1,    //正面
            0,1,5,0,5,4,    //左面
            0,7,3,0,4,7,    //上面
    };
    float color[] = {
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
    };
    private FloatBuffer vertexBuffer,colorBuffer;
    private ShortBuffer indexBuffer;
    private float[] mViewMatrix=new float[16];
    private float[] mProjectMatrix=new float[16];
    private float[] mMVPMatrix=new float[16];
    final int COORDS_PER_VERTEX = 3;

    public Cube() {
        // 初始化ByteBuffer，长度为arr数组的长度*4，因为一个float占4个字节
        ByteBuffer bb = ByteBuffer.allocateDirect(cubePositions.length * 4);
        //数组排列用nativeOrder
        bb.order(ByteOrder.nativeOrder());
        //从ByteBuffer创建一个浮点缓冲区
        vertexBuffer = bb.asFloatBuffer();
        //将坐标添加到FloatBuffer
        vertexBuffer.put(cubePositions);
        //设置缓冲区来读取第一个坐标
        vertexBuffer.position(0);

        ByteBuffer dd = ByteBuffer.allocateDirect(color.length * 4);
        dd.order(ByteOrder.nativeOrder());
        colorBuffer = dd.asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.position(0);

        ByteBuffer cc= ByteBuffer.allocateDirect(index.length*2);
        cc.order(ByteOrder.nativeOrder());
        indexBuffer=cc.asShortBuffer();
        indexBuffer.put(index);
        indexBuffer.position(0);
        //顶点着色器
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,vertexShaderCode);
        //片元着色器
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        //创建一个空的OpenGLES程序
        mProgram = GLES20.glCreateProgram();
        //将顶点着色器加入到程序中
        GLES20.glAttachShader(mProgram,vertexShader);
        //将片元着色器加入到程序中
        GLES20.glAttachShader(mProgram,fragmentShader);
        //连接到着色器程序
        GLES20.glLinkProgram(mProgram);

        //获取片元着色器的vColor成员的句柄
        int mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        //设置绘制三角形的颜色
//        GLES20.glUniform4fv(mColorHandle, 2, color, 0);
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle,4,
                GLES20.GL_FLOAT,false,
                0,colorBuffer);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, index.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
    }

    private int loadShader(int type, String shaderCode) {
        //根据type创建顶点着色器或者片元着色器
        int shader = GLES20.glCreateShader(type);
        //将资源加入到着色器中，并编译
        GLES20.glShaderSource(shader,shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        //计算宽高比
        float ratio = (float)width/height;
        //设置透视投影
        Matrix.frustumM(mProjectMatrix,0,-ratio,ratio,-1,1,3,20);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix,0, 5.0f, 5.0f, 10.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);
    }
    public void onDrawFrame(GL10 gl){
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT| GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);
        //获取变换矩阵vMatrix成员句柄
        int mMatrixHandler= GLES20.glGetUniformLocation(mProgram,"vMatrix");
        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mMatrixHandler,1,false,mMVPMatrix,0);
        //获取顶点着色器的vPosition成员句柄
        int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, 3,
                GLES20.GL_FLOAT, false,
                0, vertexBuffer);
        //获取片元着色器的vColor成员的句柄
        int mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        //设置绘制三角形的颜色
//        GLES20.glUniform4fv(mColorHandle, 2, color, 0);
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle,4,
                GLES20.GL_FLOAT,false,
                0,colorBuffer);
        //索引法绘制正方体
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,index.length, GLES20.GL_UNSIGNED_SHORT,indexBuffer);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}

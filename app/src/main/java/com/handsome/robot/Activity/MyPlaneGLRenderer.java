package com.handsome.robot.Activity;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import com.handsome.robot.Activity.model.Square;
import com.handsome.robot.Activity.model.Triangle;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.R.attr.angle;
import static android.R.attr.width;
import static android.support.v7.appcompat.R.attr.height;

/**
 * Created by Joern on 2017/08/16.
 */

public class MyPlaneGLRenderer implements GLSurfaceView.Renderer {


    //顶点数组
    //顶点数组
    private float[] mArray = {

            -0.6f , 0.6f , 0f,

            -0.2f , 0f , 0f ,

            0.2f , 0.6f , 0f ,

            0.6f , 0f , 0f

    };



    private float[] mArrayPoint = { 0f, 0f, 0f };

    public void setArrayPoint(float[] mArrayPoint) {
        this.mArrayPoint = mArrayPoint;
    }

   /* //顶点颜色数组
    private float[] mcolorArray = {

            1f, 0f , 0f,0f,

            0f , 1f , 0f ,0f,

            0f , 0f , 1f ,0f
    };

    //环境光强度
    float[] amb_light = {1f, 1f, 1f, 1f };
    //漫反射光强度
    float[] diff_light = { 1f, 1f, 1f, 1f };
    //镜面反射光强度
    float[] spec_light = {1f, 1f, 1f, 1f };

    //光源位置
    float[] pos_light = {0f, 0.0f, 1f, 0.4f};

    //光源方向
    float[] spot_dir = { 0.0f, 0.0f,  -1f, };

    // 定义立方体的8个顶点
    float[] cubeVertices = {
            //左面
            -0.5f,0.5f,0.5f,
            -0.5f,-0.5f,0.5f,
            -0.5f,0.5f,-0.5f,
            -0.5f,-0.5f,-0.5f,

            //右面
            0.5f, 0.5f,0.5f,
            0.5f,-0.5f,0.5f,
            0.5f,-0.5f,-0.5f,
            0.5f,0.5f,-0.5f ,

            //前面
            -0.5f,0.5f,0.5f,
            -0.5f,-0.5f,0.5f,
            0.5f,-0.5f,0.5f,
            0.5f, 0.5f,0.5f,

            //后面
            0.5f,-0.5f,-0.5f,
            0.5f,0.5f,-0.5f,
            -0.5f,0.5f,-0.5f,
            -0.5f,-0.5f,-0.5f,

            //上面
            -0.5f,0.5f,0.5f,
            0.5f, 0.5f,0.5f,
            0.5f,0.5f,-0.5f,
            -0.5f,0.5f,-0.5f,

            //下面
            -0.5f,-0.5f,0.5f,
            0.5f,-0.5f,0.5f,
            0.5f,-0.5f,-0.5f,
            -0.5f,-0.5f,-0.5f
    };

    //  颜色数组
    float []  cubeColors = {
            1f,0f,0f,1f ,
            0f,1f,0f,1f,
            0f,0f,1f,1f,
            1f,0f,0f,1f,

            1f,0f,0f,1f ,
            0f,1f,0f,1f,
            0f,0f,1f,1f,
            1f,0f,0f,1f,

            1f,0f,0f,1f ,
            0f,1f,0f,1f,
            0f,0f,1f,1f,
            1f,0f,0f,1f,

            1f,0f,0f,1f ,
            0f,1f,0f,1f,
            0f,0f,1f,1f,
            1f,0f,0f,1f,


            1f,0f,0f,1f ,
            0f,1f,0f,1f,
            0f,0f,1f,1f,
            1f,0f,0f,1f,

            1f,0f,0f,1f ,
            0f,1f,0f,1f,
            0f,0f,1f,1f,
            1f,0f,0f,1f,
    };

    //索引数组
    private short[] indices={
            0,1,2,
            0,2,3,

            4,5,6,
            4,6,7,

            8,9,10,
            8,10,11,

            12,13,14,
            12,14,15,

            16,17,18,
            16,18,19,

            20,21,22,
            20,22,23,
    };*/



    // 缓冲区
    private FloatBuffer mBuffer;
    private FloatBuffer colorBuffer;
    private FloatBuffer VerticesBuffer;
    private FloatBuffer Colorbuffer;
    private ShortBuffer Indexbuffer;

    private FloatBuffer amb_light_buffer;
    private FloatBuffer diff_light_buffer;
    private FloatBuffer spec_light_buffer;
    private FloatBuffer pos_light_buffer;
    private FloatBuffer spot_dir_buffer;

    private float rotate;



    public MyPlaneGLRenderer() {
        //获取浮点形缓冲数据
        mBuffer = DrawUtils.getFloatBuffer(mArrayPoint);

    }

    public void setArray(float[] mArray) {
        this.mArray = mArray;
    }

    public void setBuffer(FloatBuffer mBuffer) {
        this.mBuffer = mBuffer;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // Set the background color to black ( rgba ).
        gl10.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);  // OpenGL docs.

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
// Sets the current view port to the new size.
        gl10.glViewport(0,0, width , height );

    }

    @Override
    public void onDrawFrame(GL10 gl) {


        // 清除屏幕缓存和深度缓存
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT );


        // 允许设置顶点 // GL10.GL_VERTEX_ARRAY顶点数组
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);


        // 设置顶点
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mBuffer);


        //设置点的颜色为黑色
        gl.glColor4f(1f, 1f, 1f, 0.5f);

        //设置点的大小
        gl.glPointSize(40f);


        // 绘制点
        gl.glDrawArrays(GL10.GL_POINTS, 0, 1);


        // 禁止顶点设置
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);


    }
}

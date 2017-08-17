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

public class MyGLRenderer implements GLSurfaceView.Renderer {


    //顶点数组
    //顶点数组
    private float[] mArray = {

            -0.6f , 0.6f , 0f,

            -0.2f , 0f , 0f ,

            0.2f , 0.6f , 0f ,

            0.6f , 0f , 0f

    };

    //顶点颜色数组
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
    };

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

    public MyGLRenderer() {
        //获取浮点形缓冲数据
        mBuffer = DrawUtils.getFloatBuffer(mArray);
        //获取浮点型颜色数据
        colorBuffer= DrawUtils.getFloatBuffer(mcolorArray);
        //获取浮点形缓冲数据
        VerticesBuffer = DrawUtils.getFloatBuffer(cubeVertices);
        //获取浮点型颜色数据
        Colorbuffer= DrawUtils.getFloatBuffer(cubeColors);
        //获取浮点型索引数据
        Indexbuffer= DrawUtils.getShortBuffer(indices);

        //获取浮点型环境光数据
        amb_light_buffer= DrawUtils.getFloatBuffer(amb_light);
        //获取浮点型漫反射光数据
        diff_light_buffer= DrawUtils.getFloatBuffer(diff_light);
        //获取浮点型镜面反射光数据
        spec_light_buffer= DrawUtils.getFloatBuffer(spec_light);
        //获取浮点型光源位置数据
        pos_light_buffer= DrawUtils.getFloatBuffer(pos_light);

        //获取浮点型光源方向数据
        spot_dir_buffer= DrawUtils.getFloatBuffer(spot_dir);

    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        rotate = 0.0f;
        // Set the background color to black ( rgba ).
        gl10.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);  // OpenGL docs.
       /* // Enable Smooth Shading, default not really needed.
        gl10.glShadeModel(GL10.GL_SMOOTH);// OpenGL docs.
        // Depth buffer setup.
        gl10.glClearDepthf(1.0f);// OpenGL docs.
        // Enables depth testing.
        gl10.glEnable(GL10.GL_DEPTH_TEST);// OpenGL docs.
        // The type of depth testing to do.
        gl10.glDepthFunc(GL10.GL_LEQUAL);// OpenGL docs.
        // Really nice perspective calculations.
        gl10.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, // OpenGL docs.
                GL10.GL_NICEST);
*/
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
// Sets the current view port to the new size.
        gl10.glViewport(width / 4, width / 2, width / 2, height / 2);
      /*  // Select the projection matrix
        gl10.glMatrixMode(GL10.GL_PROJECTION);// OpenGL docs.
        // Reset the projection matrix
        gl10.glLoadIdentity();// OpenGL docs.
        // Calculate the aspect ratio of the window
        GLU.gluPerspective(gl10, 45.0f,
                (float) width / (float) height,
                0.1f, 100.0f);
        // Select the modelview matrix
        gl10.glMatrixMode(GL10.GL_MODELVIEW);// OpenGL docs.
        // Reset the modelview matrix
        gl10.glLoadIdentity();// OpenGL docs.*/
    }

    @Override
    public void onDrawFrame(GL10 gl) {



        // 清除屏幕缓存和深度缓存
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        /*//打开光源总开关
        gl.glEnable(GL10.GL_LIGHTING);

        //打开0号光源
        gl.glEnable(GL10.GL_LIGHT0);

        //设置光源位置
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, pos_light_buffer);

        //设置光源方向
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPOT_DIRECTION , spot_dir_buffer);

        //设置光源种类
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT ,  amb_light_buffer );
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE , diff_light_buffer );
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, spec_light_buffer);


        //设置聚光强度
        gl.glLightf(GL10.GL_LIGHT0, GL10.GL_SPOT_EXPONENT, 64f);

        //设置聚光角度
        gl.glLightf(GL10.GL_LIGHT0, GL10.GL_SPOT_CUTOFF, 45f);*/


        // 允许设置顶点 // GL10.GL_VERTEX_ARRAY顶点数组
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        // 开启颜色渲染功能.
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        //设置为单位矩阵
        gl.glLoadIdentity();

        // 沿着Y轴旋转
        gl.glRotatef(rotate, 1.0f, 1.0f, 0.0f);


        // 设置顶点
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, VerticesBuffer);

        // 设置三角形顶点的颜色
        gl.glColorPointer(4, GL10.GL_FIXED, 0, Colorbuffer);

        //设置点的颜色为绿色
        gl.glColor4f(0f, 1f, 0f, 0f);

        /*//设置点的大小
        gl.glPointSize(80f);*/

      /*  //向右移动0.6f个x坐标
        gl.glTranslatef(0f, 0f, 0f);

        //缩小为原来0.3倍
        gl.glScalef(0.8f,0.8f, 0.8f);*/


       /* // 绘制点
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 24);*/

        gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, Indexbuffer);


        // 禁止顶点设置
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

        //关闭颜色渲染功能.
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

        // 旋转角度增加1
        rotate+=1;






      /*  // Clears the screen and depth buffer.
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT
                | GL10.GL_DEPTH_BUFFER_BIT);
        // Replace the current matrix with the identity matrix
        gl.glLoadIdentity();
        // Translates 10 units into the screen.
        gl.glTranslatef(0, 0, -10);

        // SQUARE A
        // Save the current matrix.
        gl.glPushMatrix();
        // Rotate square A counter-clockwise.
        gl.glRotatef(angle, 0, 0, 1);
        // Draw square A.
        mSquare.draw(gl);
        // Restore the last matrix.
        gl.glPopMatrix();

        // SQUARE B
        // Save the current matrix
        gl.glPushMatrix();
        // Rotate square B before moving it,
        //making it rotate around A.
        gl.glRotatef(-angle, 0, 0, 1);
        // Move square B.
        gl.glTranslatef(2, 0, 0);
        // Scale it to 50% of square A
        gl.glScalef(.5f, .5f, .5f);
        // Draw square B.
        mSquare.draw(gl);

        // SQUARE C
        // Save the current matrix
        gl.glPushMatrix();
        // Make the rotation around B
        gl.glRotatef(-angle, 0, 0, 1);
        gl.glTranslatef(2, 0, 0);
        // Scale it to 50% of square B
        gl.glScalef(.5f, .5f, .5f);
        // Rotate around it's own center.
        gl.glRotatef(angle*10, 0, 0, 1);
        // Draw square C.
        mSquare.draw(gl);

        // Restore to the matrix as it was before C.
        gl.glPopMatrix();
        // Restore to the matrix as it was before B.
        gl.glPopMatrix();

        // Increse the angle.
        angle++;*/
    }
}

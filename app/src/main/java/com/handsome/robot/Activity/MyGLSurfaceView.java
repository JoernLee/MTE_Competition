package com.handsome.robot.Activity;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by Joern on 2017/08/16.
 */

public class MyGLSurfaceView extends GLSurfaceView {

    private MyPlaneGLRenderer mRenderer;

    public MyGLSurfaceView(Context context) {
        super(context);
       /* // 创建一个OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyPlaneGLRenderer();
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);*/
    }
}

package com.handsome.robot.Activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Joern on 2017/08/21.
 */

public class MeasureView extends ImageView {

    private Bitmap bmPhoto;

    private Bitmap XPoint;
    private Bitmap YPoint;
    private Bitmap ZPoint;



    private Float[] mXLocation = {0f,0f};
    private Float[] mYLocation = {0f,0f};
    private Float[] mZLocation = {0f,0f};
    private Float[] mOLocation = {0f,0f};


    private Paint mPaint;
    private Rect bitmapReact;
    private Rect mViewReact;
    private int viewH;
    private int viewW;
    private int xyzNumber;


    public MeasureView(Context context) {
        super(context);
    }


    public MeasureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MeasureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
       /* //绘制背景
        //确保背景图完整显示
        bitmapReact = new Rect(0, 0, bmPhoto.getWidth(), bmPhoto.getHeight());
        mViewReact = new Rect(0, 0, viewW, viewH);
        canvas.drawBitmap(bmPhoto, bitmapReact, mViewReact, mPaint);*/
        //绘制三个标志点
        // 创建画笔
        Paint p = new Paint();
        p.setColor(Color.RED);// 设置红色点
        p.setStrokeWidth(15);
        for (int i = 0; i < 4; i++) {
            if (i == 0){
                //绘制O点(X Y 坐标)
                canvas.drawPoint(mOLocation[0],mOLocation[1],p);
            }
            if (i == 1){
                //绘制X点(X Y 坐标)
                canvas.drawPoint(mXLocation[0],mXLocation[1],p);
            }
            if (i == 2){
                //绘制Y点(X Y 坐标)
                canvas.drawPoint(mYLocation[0],mYLocation[1],p);
            }
            if (i == 3){
                //绘制Z点(X Y 坐标)
                canvas.drawPoint(mZLocation[0],mZLocation[1],p);
            }
        }
        //绘制总共三根线，o-x，y，z的-蓝色
        p.setColor(Color.BLUE);
        p.setStrokeWidth(10);
        canvas.drawLine(mOLocation[0],mOLocation[1],mXLocation[0],mXLocation[1],p);
        canvas.drawLine(mOLocation[0],mOLocation[1],mYLocation[0],mYLocation[1],p);
        canvas.drawLine(mOLocation[0],mOLocation[1],mZLocation[0],mZLocation[1],p);
    }



    public void setmXLocation(Float[] mXLocation) {
        this.mXLocation = mXLocation;
    }

    public void setmYLocation(Float[] mYLocation) {
        this.mYLocation = mYLocation;
    }

    public void setmZLocation(Float[] mZLocation) {
        this.mZLocation = mZLocation;
    }

    public void setmOLocation(Float[] mOLocation) {
        this.mOLocation = mOLocation;
    }




}

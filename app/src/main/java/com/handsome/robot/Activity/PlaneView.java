package com.handsome.robot.Activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Joern on 2017/08/25.
 */

public class PlaneView extends View {



    private float recLength = 0;
    private float recWidth = 0;

    private float pointX = 0;
    private float pointY = 0;


    public PlaneView(Context context) {
        super(context);
    }


    public PlaneView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlaneView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 创建画笔
        Paint p = new Paint();
        p.setColor(Color.WHITE);// 设置白色
        p.setStrokeWidth(10);
        p.setStyle(Paint.Style.STROKE);//设置空心
        //canvas.drawRect(60, 60, 80, 80, p);// 正方形
        // canvas.drawRect(160, 190, 260, 200, p);// 长方形
        //视图长840，宽450.
        canvas.drawRect(new RectF(420 - (recLength/2.0f)*1.7f,225 - (recWidth/2.0f)*1.7f,420 + (recLength/2.0f)*1.5f,225 + (recWidth/2.0f)*1.5f),p);// 长宽
        p.setColor(Color.RED);
        canvas.drawPoint(420 + pointX*1.7f,225 - pointY*1.7f,p);
    }

    public float getRecLength() {
        return recLength;
    }

    public void setRecLength(float recLength) {
        this.recLength = recLength;
    }

    public float getRecWidth() {
        return recWidth;
    }

    public void setRecWidth(float recWidth) {
        this.recWidth = recWidth;
    }

    public void setPointX(float pointX) {
        this.pointX = pointX;
    }

    public void setPointY(float pointY) {
        this.pointY = pointY;
    }
}

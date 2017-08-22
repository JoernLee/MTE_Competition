package com.handsome.robot.Activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by Joern on 2017/08/21.
 */

public class MeasureView extends View {

    private Bitmap bmPhoto;

    private Bitmap XPoint;
    private Bitmap YPoint;
    private Bitmap ZPoint;

    private Float[] mXLocation;
    private Float[] mYLocation;
    private Float[] mZLocation;


    private Paint mPaint;
    private Rect bitmapReact;
    private Rect mViewReact;
    private int viewH;
    private int viewW;
    private int xyzNumber;


    public MeasureView(Context context, Bitmap backgroundBitmap , Float[] XLocation , Float[] YLocation , Float[] ZLocation) {
        super(context);
        bmPhoto = backgroundBitmap;
        mXLocation = XLocation;
        mYLocation = YLocation;
        mZLocation = ZLocation;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewW = MeasureSpec.getSize(widthMeasureSpec);
        viewH = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(viewW, viewH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制背景
        //确保背景图完整显示
        bitmapReact = new Rect(0, 0, bmPhoto.getWidth(), bmPhoto.getHeight());
        mViewReact = new Rect(0, 0, viewW, viewH);
        canvas.drawBitmap(bmPhoto, bitmapReact, mViewReact, mPaint);
        //绘制三个标志点
        for (int i = 0; i < 3; i++) {
            if (i == 0){
                //绘制X点(X Y 坐标)
            }
        }
    }


}
